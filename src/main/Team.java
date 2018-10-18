package main;

import java.util.Comparator;

public class Team {

	private String name;
	private int rating = 1500;
	private int wins = 0;
	private int losses = 0;
	private int ties = 0;
	
	public Team(String name) {
		this.name = name;
	}
	
	public int getRating() {
		return rating;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public void incrementWins() {
		this.wins++;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}
	
	public void incrementLosses() {
		this.losses++;
	}

	public int getTies() {
		return ties;
	}

	public void setTies(int ties) {
		this.ties = ties;
	}
	
	public void incrementTies() {
		this.ties++;
	}
}
