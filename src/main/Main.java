package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	public static int k = 32;

	public static void main(String[] args) {
		File file = new File("C:\\Users\\Kyle\\Documents\\scores_week_0.txt"); 
		teamMap = new HashMap<String, Team>();
				  
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			
			String st; 
			while ((st = br.readLine()) != null) {
				String[] splitString = parseGame(st);
				String homeTeamString = splitString[0];
				Integer homeTeamScore = Integer.parseInt(splitString[1]);
				String awayTeamString = splitString[2];
				Integer awayTeamScore = Integer.parseInt(splitString[3]);
				
				// TODO: Fix this so they're actually the home and away team
				Team homeTeam = teamMap.get(homeTeamString);
				Team awayTeam = teamMap.get(awayTeamString);
				if (homeTeam == null) {
					homeTeam = new Team(homeTeamString);
					teamMap.put(homeTeamString, homeTeam);
				}
				
				if (awayTeam == null) {
					awayTeam = new Team(awayTeamString);
					teamMap.put(awayTeamString, awayTeam);
				}
				
				Game game = new Game(homeTeam, awayTeam);
				if (homeTeamScore > awayTeamScore) {
					updateRating(homeTeam, awayTeam);
				} else {
					updateRating(awayTeam, homeTeam);
				}
			}
			
			List<Team> teams = new ArrayList<>();
			for (Team team : teamMap.values()) {
				teams.add(team);
			}
			
			System.out.println(teams.size());
			
			Collections.sort(teams, new TeamSort());
			
			for (Team team : teams) {
				System.out.println(team.getName() + "\t\t" + team.getRating());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static Game parseGame(String st) {
		String[] splitStr = st.split("\\s+");
		
		String winningTeamStr = "";
		String winningTeamScore = "";
		String losingTeamStr = "";
		String losingTeamScore = "";
		
		boolean firstColumn = true;
		boolean firstTeam = false;
		for (String str : splitStr) {
			if (firstColumn) {
				firstColumn = false;
				firstTeam = true;
				continue;
			} else if (firstTeam) {
				if (!Character.isDigit(str.charAt(0))) {
					winningTeamStr += str + " ";
				} else {
					winningTeamScore = str;
				}
			} else {
				if (!Character.isDigit(str.charAt(0))) {
					losingTeamStr += str + " ";
				} else {
					losingTeamScore = str;
				}
			}
		}
		
		Team homeTeam = null;
		Team awayTeam = null;
		Integer home
		
		if (winningTeamStr.charAt(0) == '@') {
			winningTeamStr.replace("@", "");
			homeTeam = teamMap.get(winningTeamStr);
			if (homeTeam == null) {
				homeTeam = new Team(winningTeamStr);
			}
			
			awayTeam = teamMap.get(losingTeamStr);
			if (awayTeam == null) {
				awayTeam = new Team(losingTeamStr);
			}
		} else {
			losingTeamStr.replace("@", "");
			homeTeam = teamMap.get(losingTeamStr);
			if (homeTeam == null) {
				homeTeam = new Team(losingTeamStr);
			}
			
			awayTeam = teamMap.get(winningTeamStr);
			if (awayTeam == null) {
				awayTeam = new Team(winningTeamStr);
			}
		}
	}
	
	public static void updateRating(Team winningTeam, Team losingTeam ) {
		double winningTeamOriginalRating = winningTeam.getRating();
		double losingTeamOriginalRating = losingTeam.getRating();
		
		double winningTeamTransformedRating = Math.pow(10, (winningTeamOriginalRating / 400));
		double losingTeamTransformedRating = Math.pow(10, (losingTeamOriginalRating / 400));
		
		double winningTeamExpectedScore = winningTeamTransformedRating / (winningTeamTransformedRating + losingTeamTransformedRating);
		double losingTeamExpectedScore = losingTeamTransformedRating / (winningTeamTransformedRating + losingTeamTransformedRating);
		
		Double winningTeamUpdatedRating = winningTeamOriginalRating + (k * (1 - winningTeamExpectedScore));
		Double losingTeamUpdatedRating = losingTeamOriginalRating + (k * (0 - losingTeamExpectedScore));
		
		winningTeam.setRating(winningTeamUpdatedRating);
		losingTeam.setRating(losingTeamUpdatedRating);
		
	}
}