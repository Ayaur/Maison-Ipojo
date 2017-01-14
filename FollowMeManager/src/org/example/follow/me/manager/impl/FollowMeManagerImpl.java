package org.example.follow.me.manager.impl;

import org.example.follow.me.manager.FollowMeAdministration;
import org.example.follow.me.manager.IlluminanceGoal;

public class FollowMeManagerImpl implements FollowMeAdministration{

	private IlluminanceGoal goal;
	
	@Override
	public void setIlluminancePreference(IlluminanceGoal illuminanceGoal) {
		goal = illuminanceGoal;
		
	}

	@Override
	public IlluminanceGoal getIlluminancePreference() {
		return goal;
	}

}
