package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	
	public static Map<String, Team> teamMap;
	public static List<Game> schedule;
	public static double k = 32.0;
	public static Connection conn = null;

	public static void main(String[] args) {
		NFLRanker nflRanker = new NFLRanker();
		CFBRanker cfbRanker = new CFBRanker(); 
		
		//nflRanker.run();
		cfbRanker.run();
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
			
			int rating = team.getCurrentRating();
			
			String format = "%-30.30s %-10.30s  %-3.4s%n";
			System.out.printf(format, teamNameString, recordString, rating);
			count++;
		}
		
		System.out.println();
	}
}