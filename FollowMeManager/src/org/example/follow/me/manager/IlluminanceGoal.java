package org.example.follow.me.manager;

public enum IlluminanceGoal {
	 
    // The goal associated with soft illuminance. */
    SOFT(1),
    // The goal associated with medium illuminance. */
    MEDIUM(2),
    /// The goal associated with full illuminance. */
    FULL(3);
 
    private int numberOfLightsToTurnOn;
 
    public int getNumberOfLightsToTurnOn() {
        return numberOfLightsToTurnOn;
    }
 
    // Instantiates a new illuminance goal
    private IlluminanceGoal(int numberOfLightsToTurnOn) {
        this.numberOfLightsToTurnOn = numberOfLightsToTurnOn;
    }
}