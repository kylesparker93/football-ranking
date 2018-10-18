package main;

public class Game {
	
	Team winningTeam;
	int winningScore;
	
	Team losingTeam;
	int losingScore;
	
	Team homeTeam;
	
	int week;
	
	public Game(Team winningTeam, int winningScore,
				Team losingTeam, int losingScore,
				Team homeTeam, int week) {
		this.winningTeam = winningTeam;
		this.winningScore = winningScore;
		this.losingTeam = losingTeam;
		this.losingScore = losingScore;
		this.homeTeam = homeTeam;
		this.week = week;
	}

	public Team getWinningTeam() {
		return winningTeam;
	}

	public void setWinningTeam(Team winningTeam) {
		this.winningTeam = winningTeam;
	}

	public int getWinningScore() {
		return winningScore;
	}

	public void setWinningScore(int winningScore) {
		this.winningScore = winningScore;
	}

	public Team getLosingTeam() {
		return losingTeam;
	}

	public void setLosingTeam(Team losingTeam) {
		this.losingTeam = losingTeam;
	}

	public int getLosingScore() {
		return losingScore;
	}

	public void setLosingScore(int losingScore) {
		this.losingScore = losingScore;
	}

	public Team getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}
	
	public int getWeek() {
		return this.week;
	}
	
	public void setWeek(int week) {
		this.week = week;
	}
	
	
}
