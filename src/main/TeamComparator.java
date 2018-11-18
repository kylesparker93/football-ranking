package main;

import java.util.Comparator;

public class TeamComparator implements Comparator<Team> {

	@Override
	public int compare(Team t1, Team t2) {
		// TODO Auto-generated method stub
		return t2.getCurrentRating() - t1.getCurrentRating();
	}
	
}
