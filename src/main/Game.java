package main;

public class Game {
	private Team homeTeam;
	private Team awayTeam;
	
	private boolean played = false;
	private Team winningTeam = null;
	private Team losingTeam = null;
	private Integer winningScore;
	private Integer losingScore;
	
	public Game(Team homeTeam, Team awayTeam) {
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
	}
	
	public Game(Team homeTeam, Team awayTeam,
				Team winningTeam, Integer winningScore,
				Team losingTeam, Integer losingScore) {
		this(homeTeam, awayTeam);
		this.winningTeam = winningTeam;
		this.winningScore = winningScore;
		this.losingTeam = losingTeam;
		this.losingScore = losingScore;
	}
	
	public Team getTeam1() {
		return homeTeam;
	}
	public void setTeam1(Team team1) {
		this.homeTeam = team1;
	}
	public Team getTeam2() {
		return awayTeam;
	}
	public void setTeam2(Team team2) {
		this.awayTeam = team2;
	}
	public boolean isPlayed() {
		return played;
	}
	public void setPlayed(boolean played) {
		this.played = played;
	}
	public Team getWinningTeam() {
		return homeTeam;
	}
	public void setWinningTeam(Team winningTeam) {
		this.homeTeam = winningTeam;
	}
	public Team getLosingTeam() {
		return awayTeam;
	}
	public void setLosingTeam(Team losingTeam) {
		this.awayTeam = losingTeam;
	}
	public Integer getWinningScore() {
		return winningScore;
	}
	public void setWinningScore(Integer winningScore) {
		this.winningScore = winningScore;
	}
	public Integer getLosingScore() {
		return losingScore;
	}
	public void setLosingScore(Integer losingScore) {
		this.losingScore = losingScore;
	}
}
