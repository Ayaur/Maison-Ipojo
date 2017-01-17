    package org.example.follow.me.configuration;
     
 
    public interface FollowMeConfiguration {
     
        public int getMaximumNumberOfLightsToTurnOn();
 
        public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn);
        
        
        public double getMaximumAllowedEnergyInRoom();
        
        public void setMaximumAllowedEnergyInRoom(double maximumEnergy);
    }