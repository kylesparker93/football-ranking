package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
	
	public static Map<String, Team> teamMap;
	public static List<Game> schedule;
	public static double k = 32.0;
	public static Connection conn = null;

	public static void main(String[] args) {	    
		connect();
		
		teamMap = new HashMap<String, Team>(32);
		createTeamMap();
		
		schedule = new ArrayList<>();
		createSchedule();
		
		playbackSchedule();
		
		persistTeams();
	}
	
	/**
     * Connect to a sample database
     */
    public static void connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/Kyle/Development/sr-stat-parsing/nfl_teams.sqlite";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public static void createTeamMap() {
		String teamsQuery = "SELECT team FROM Teams";
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(teamsQuery);
			
			while (results.next()) {
				String teamName = results.getString("team");
				teamMap.put(teamName, new Team(teamName));
			} 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createSchedule() {
		String gamesQuery = "SELECT week, winner, winning_team_home, winning_score, loser, losing_score FROM Games";
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(gamesQuery);
			
			while (results.next()) {
				int week = results.getInt("week");
				Team winningTeam = teamMap.get(results.getString("winner"));
				Team losingTeam = teamMap.get(results.getString("loser"));
				int winningScore = results.getInt("winning_score");
				int losingScore = results.getInt("losing_score");
				
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
	
	public static void playbackSchedule() {
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
	
	
	
	public static void updateRatings(Team winningTeam, Team losingTeam, int margin, Team homeTeam) {
		
		// TODO: Factor in margin of victory and location
		
		// TODO: Handle ties better
		if (margin == 0) {
			winningTeam.incrementTies();
			losingTeam.incrementTies();
			return;
		}
		
		double winningTeamOriginalRating = winningTeam.getRating();
		double losingTeamOriginalRating = losingTeam.getRating();
		
		double winningTeamTransformedRating = Math.pow(10.0, (winningTeamOriginalRating / 400.0));
		double losingTeamTransformedRating = Math.pow(10.0, (losingTeamOriginalRating / 400.0));
		
		double winningTeamExpectedScore = winningTeamTransformedRating / (winningTeamTransformedRating + losingTeamTransformedRating);
		double losingTeamExpectedScore = losingTeamTransformedRating / (winningTeamTransformedRating + losingTeamTransformedRating);
		
		int winningTeamUpdatedRating = (int) (winningTeamOriginalRating + (k * (1.0 - winningTeamExpectedScore)));
		int losingTeamUpdatedRating = (int) (losingTeamOriginalRating + (k * (0.0 - losingTeamExpectedScore)));
		
		winningTeam.setRating(winningTeamUpdatedRating);
		winningTeam.incrementWins();
		losingTeam.setRating(losingTeamUpdatedRating);
		losingTeam.incrementLosses();
	}
	
	public static void persistTeams() {
		for (Map.Entry<String, Team> entry : teamMap.entrySet()) {
			Team team = entry.getValue();
			
			try {
				String updateQuery = "UPDATE Teams SET rating = ?, wins = ?, losses = ?, ties = ? WHERE team = ?";
				
				PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
				updateStatement.setDouble(1, team.getRating());
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
	
	public static String convertWeek(int week) {
		String weekString = null;
		switch (week) {
			case 1: weekString = "one";
					break;
			case 2: weekString = "two";
					break;
			case 3: weekString = "three";
					break;
			case 4: weekString = "four";
					break;
			case 5: weekString = "five";
					break;
			case 6: weekString = "six";
					break;
			case 7: weekString = "seven";
					break;
			case 8: weekString = "eight";
					break;
			case 9: weekString = "nine";
					break;
			case 10: weekString = "ten";
					break;
			case 11: weekString = "eleven";
					break;
			case 12: weekString = "twelve";
					break;
			case 13: weekString = "thirteen";
					break;
			case 14: weekString = "fourteen";
					break;
			case 15: weekString = "fifteen";
					break;
			case 16: weekString = "sixteen";
					break;
		}
		
		return weekString;
	}
	
	public static void printRankings() {
		List<Team> teamList = new ArrayList<Team>(teamMap.values());
		Collections.sort(teamList, new TeamComparator());
		
		int count = 1;
		for (Team team : teamList) {
			String teamNameString = count + ". " + team.getName();
			
			int wins = team.getWins();
			int losses = team.getLosses();
			int ties = team.getTies();
			String recordString = wins + "-" + losses + "-" + ties;
			
			int rating = team.getRating();
			
			String format = "%-30.30s %-10.30s  %-3.4s%n";
			System.out.printf(format, teamNameString, recordString, rating);
			count++;
		}
		
		System.out.println();
	}
}