package main;

import java.util.List;

public class Team {

	private String name;
	private List<Game> schedule;
	private Double rating = 1500.0;
	private Long strengthOfSchedule;
	
	public Team(List<Game> schedule) {
		this.schedule = schedule;
	}
	
	public Team(String name) {
		this.name = name;
	}
	
	public List<Game> getSchedule() {
		return schedule;
	}
	public void setSchedule(List<Game> schedule) {
		this.schedule = schedule;
	}
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public Long getStrengthOfSchedule() {
		return strengthOfSchedule;
	}
	public void setStrengthOfSchedule(Long strengthOfSchedule) {
		this.strengthOfSchedule = strengthOfSchedule;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
