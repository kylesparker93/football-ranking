package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFBRanker {
	
	public static Map<String, Team> teamMap;
	public static List<Game> schedule;
	public static double k = 32.0;
	public static Connection conn = null;
	
	public CFBRanker() {
		
	}
	
	public void run() {
		conn = ConnectionManager.getCFBDatabaseConnection();
		
		teamMap = new HashMap<String, Team>();
		createTeamMap();
		
		schedule = new ArrayList<>();
		createSchedule();
		
		playbackSchedule();
		
		persistTeams();
	}
	
	private void createTeamMap() {
		String teamsQuery = "SELECT team, rating FROM Teams";
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(teamsQuery);
			
			while (results.next()) {
				String teamName = results.getString("team");
				int finalRating = results.getInt("rating");
				teamMap.put(teamName, new Team(teamName, finalRating));
			} 
			
			Team fcsPlaceholder = new Team("FCS", 1200);
			teamMap.put("FCS", fcsPlaceholder);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createSchedule() {
		// Put a placeholder for FCS opponents
		Team fcsPlaceholder = new Team("FCS", 1200);
		teamMap.put("FCS", fcsPlaceholder);
		
		String gamesQuery = "SELECT week, winner, winning_team_home, winning_score, winner_fcs, loser, losing_score, loser_fcs FROM Games";
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(gamesQuery);
			
			while (results.next()) {
				int week = results.getInt("week");
				int winningScore = results.getInt("winning_score");
				int losingScore = results.getInt("losing_score");
				
				
				String winningTeamName = sanatizeTeamString(results.getString("winner"));
				Team winningTeam = teamMap.get(winningTeamName);
				int winningTeamFCS = results.getInt("winner_fcs");
				if (winningTeamFCS == 0 && teamMap.get(winningTeamName) == null) {
					winningTeam = new Team(winningTeamName, 1500);
					teamMap.put(winningTeamName, winningTeam);
				} else if (winningTeamFCS == 1) {
					winningTeam = teamMap.get("FCS");
				}
				
				String losingTeamName = sanatizeTeamString(results.getString("loser"));
				Team losingTeam = teamMap.get(losingTeamName);
				int losingTeamFCS = results.getInt("loser_fcs");
				if (losingTeamFCS == 0 && teamMap.get(losingTeamName) == null) {
					losingTeam = new Team(winningTeamName, 1500);
					teamMap.put(losingTeamName, losingTeam);
				} else if (losingTeamFCS == 1) {
					losingTeam = teamMap.get("FCS");
				}
				
				
				int winningTeamHome = results.getInt("winning_team_home");
				Team homeTeam;
				if (winningTeamHome == 1) {
					homeTeam = winningTeam;
				} else {
					homeTeam = losingTeam;
				}
				
				Game game = new Game(winningTeam, winningScore, losingTeam, losingScore, homeTeam, week);
				schedule.add(game);
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String sanatizeTeamString(String team) {
		if (team.charAt(0) == '(') {
			return team.split("\\)")[1].substring(1);
		} else {
			return team;
		}
		
	}
	
	private void playbackSchedule() {
		int week = 1;
		for (Game game : schedule) {
			if (game.getWeek() != week) {
				printRankings();
				week++;
			}
			
			Team winningTeam = game.getWinningTeam();
			Team losingTeam = game.getLosingTeam();
			int margin = Math.abs(game.getWinningScore() - game.getLosingScore());
			Team homeTeam = game.getHomeTeam();
			
			updateRatings(winningTeam, losingTeam, margin, homeTeam);
		}
		
		printRankings();
	}
	
	private void updateRatings(Team winningTeam, Team losingTeam, int margin, Team homeTeam) {
		
		// TODO: Factor in margin of victory and location
		
		// TODO: Handle ties better
		if (margin == 0) {
			winningTeam.incrementTies();
			losingTeam.incrementTies();
			return;
		}
		
		double winningTeamFinalRating = winningTeam.getFinalRating();
		double losingTeamFinalRating = losingTeam.getFinalRating();
		
		double winningTeamCurrentRating = winningTeam.getCurrentRating();
		double losingTeamCurrentRating = losingTeam.getCurrentRating();
		
		double winningTeamTransformedRating = Math.pow(10.0, (winningTeamFinalRating / 400.0));
		double losingTeamTransformedRating = Math.pow(10.0, (losingTeamFinalRating / 400.0));
		
		double winningTeamExpectedScore = winningTeamTransformedRating / (winningTeamTransformedRating + losingTeamTransformedRating);
		double losingTeamExpectedScore = losingTeamTransformedRating / (winningTeamTransformedRating + losingTeamTransformedRating);
		
		int winningTeamUpdatedRating = (int) (winningTeamCurrentRating + (k * (1.0 - winningTeamExpectedScore)));
		int losingTeamUpdatedRating = (int) (losingTeamCurrentRating + (k * (0.0 - losingTeamExpectedScore)));
		
		if (winningTeam != teamMap.get("FCS")) {
			winningTeam.setCurrentRating(winningTeamUpdatedRating);
			winningTeam.incrementWins();
		}
		
		if (losingTeam != teamMap.get("FCS")) {
			losingTeam.setCurrentRating(losingTeamUpdatedRating);
			losingTeam.incrementLosses();
		}
	}
	
	private void persistTeams() {
		for (Map.Entry<String, Team> entry : teamMap.entrySet()) {
			Team team = entry.getValue();
			
			try {
				String updateQuery = "UPDATE Teams SET rating = ?, wins = ?, losses = ?, ties = ? WHERE team = ?";
				
				PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
				updateStatement.setDouble(1, team.getCurrentRating());
				updateStatement.setDouble(2, team.getWins());
				updateStatement.setDouble(3, team.getLosses());
				updateStatement.setDouble(4, team.getLosses());
				updateStatement.setString(5, team.getName());
				updateStatement.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void printRankings() {
		List<Team> teamList = new ArrayList<Team>(teamMap.values());
		Collections.sort(teamList, new TeamComparator());
		
		int count = 1;
		for (Team team : teamList) {
			String teamNameString = count + ". " + team.getName();
			
			int wins = team.getWins();
			int losses = team.getLosses();
			int ties = team.getTies();
			String recordString = wins + "-" + losses + "-" + ties;
			
			int rating = team.getCurrentRating();
			
			String format = "%-30.30s %-10.30s  %-3.4s%n";
			System.out.printf(format, teamNameString, recordString, rating);
			count++;
		}
		
		System.out.println();
	}
}
