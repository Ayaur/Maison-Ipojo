package org.example.follow.me.manager.impl;

import org.example.follow.me.manager.FollowMeAdministration;
import org.example.follow.me.manager.IlluminanceGoal;
import org.example.follow.me.configuration.FollowMeConfiguration;
import java.util.Map;

public class FollowMeManagerImpl implements FollowMeAdministration {

	private IlluminanceGoal goal;
	/** Field for Configuration dependency */
	private FollowMeConfiguration Configuration;

	@Override
	public void setIlluminancePreference(IlluminanceGoal illuminanceGoal) {
		goal = illuminanceGoal;
		Configuration.setMaximumNumberOfLightsToTurnOn(illuminanceGoal.getNumberOfLightsToTurnOn());
	}

	@Override
	public IlluminanceGoal getIlluminancePreference() {
		return goal;
	}

	/** Component Lifecycle Method */
	public void stop() {
		// TODO: Add your implementation code here
	}

	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
	}

}
