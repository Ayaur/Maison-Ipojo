package org.example.follow.me.manager;

/*
 * The Interface FollowMeAdministration allows the administrator to configure
 * its preference regarding the management of the Follow Me application.
 */
public interface FollowMeAdministration {
 
    public void setIlluminancePreference(IlluminanceGoal illuminanceGoal);
 
    public IlluminanceGoal getIlluminancePreference();
 
}