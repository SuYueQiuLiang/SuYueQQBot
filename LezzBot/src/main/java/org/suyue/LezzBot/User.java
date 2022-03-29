package org.suyue.LezzBot;

public class User {
    public User(long userQQ, String userId, String userPassword, LezzTask scheduledTask,double validMileage) {
        this.validMileage = validMileage;
        this.userQQ = userQQ;
        this.userId = userId;
        this.userPassword = userPassword;
        this.scheduledTask = scheduledTask;
    }
    public long userQQ;
    public double validMileage;
    public String userId,userPassword;
    public LezzTask scheduledTask;
}
