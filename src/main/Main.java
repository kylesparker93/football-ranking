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
	public static double k = 32.0;
	public static Connection conn = null;

	public static void main(String[] args) {	    
		connect();
		
		String gamesQuery = "SELECT week, winner, loser, winning_score, losing_score FROM Games";
		try {
			Statement statement = conn.createStatement();
			ResultSet results = statement.executeQuery(gamesQuery);
			
			while (results.next()) {
				int week = results.getInt("week");
				String winner = results.getString("winner");
				String loser = results.getString("loser");
				int winningScore = results.getInt("winning_score");
				int losingScore = results.getInt("losing_score");
				System.out.println(week + " " + winner + " " + loser + " " + winningScore + " " + losingScore);
				
				String teamQuery = "SELECT rating FROM teams WHERE team = ?";
				PreparedStatement winnerPreparedStatement = conn.prepareStatement(teamQuery);
				winnerPreparedStatement.setString(1, winner);
				int winnerRating = winnerPreparedStatement.executeQuery().getInt("rating");
				
				PreparedStatement loserPreparedStatement = conn.prepareStatement(teamQuery);
				loserPreparedStatement.setString(1, loser);
				int loserRating = loserPreparedStatement.executeQuery().getInt("rating");
				
				System.out.println("Winner rating: " + winnerRating);
				System.out.println("Loser rating: " + loserRating);
				
				List<Integer> updatedRatings = updateRatings(winnerRating, loserRating);
				
				int winnerUpdatedRating = updatedRatings.get(0);
				int loserUpdatedRating = updatedRatings.get(1);
				
				System.out.println("Winner updated rating: " + winnerUpdatedRating);
				System.out.println("Loser updated rating: " + loserUpdatedRating);
				
				String weekText = "week_" + convertWeek(week) + "_rating";
				String updateQuery = "UPDATE Teams SET rating = ?, " + weekText + " = ? WHERE team = ?";
				
				PreparedStatement winnerUpdateStatement = conn.prepareStatement(updateQuery);
				winnerUpdateStatement.setDouble(1, winnerUpdatedRating);
				winnerUpdateStatement.setDouble(2, winnerUpdatedRating);
				winnerUpdateStatement.setString(3, winner);
				winnerUpdateStatement.executeUpdate();
				
				PreparedStatement loserUpdateStatement = conn.prepareStatement(updateQuery);
				loserUpdateStatement.setDouble(1, loserUpdatedRating);
				loserUpdateStatement.setDouble(2, loserUpdatedRating);
				loserUpdateStatement.setString(3, loser);
				loserUpdateStatement.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public static List<Integer> updateRatings(int winnerRating, int loserRating) {
		double winningTeamOriginalRating = winnerRating;
		double losingTeamOriginalRating = loserRating;
		
		double winningTeamTransformedRating = Math.pow(10.0, (winningTeamOriginalRating / 400.0));
		double losingTeamTransformedRating = Math.pow(10.0, (losingTeamOriginalRating / 400.0));
		
		double winningTeamExpectedScore = winningTeamTransformedRating / (winningTeamTransformedRating + losingTeamTransformedRating);
		double losingTeamExpectedScore = losingTeamTransformedRating / (winningTeamTransformedRating + losingTeamTransformedRating);
		
		Integer winningTeamUpdatedRating = (int) (winningTeamOriginalRating + (k * (1.0 - winningTeamExpectedScore)));
		Integer losingTeamUpdatedRating = (int) (losingTeamOriginalRating + (k * (0.0 - losingTeamExpectedScore)));
		
		List<Integer> updatedRatings = new ArrayList<>(2);
		updatedRatings.add(winningTeamUpdatedRating);
		updatedRatings.add(losingTeamUpdatedRating);
		
		return updatedRatings;
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
}