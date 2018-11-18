package main;

import java.util.Comparator;

public class TeamSort implements Comparator<Team> {
	
	public int compare(Team teamOne, Team teamTwo) {
		if (teamOne.getCurrentRating() < teamTwo.getCurrentRating()) return 1;
        if (teamOne.getCurrentRating() > teamTwo.getCurrentRating()) return -1;
        return 0;
	}

}