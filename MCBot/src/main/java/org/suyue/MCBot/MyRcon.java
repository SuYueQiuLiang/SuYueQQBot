package org.suyue.MCBot;

import net.kronos.rkon.core.Rcon;
import net.kronos.rkon.core.ex.AuthenticationException;

import java.io.IOException;

public class MyRcon {
    private Rcon rcon;
    private String password,rconIp;
    private int rconPort;
    public MyRcon(String rconIp,int rconPort,String password){
        this.rconIp = rconIp;
        this.rconPort = rconPort;
        this.password = password;
    }
    public boolean login(){
        try {
            rcon = new Rcon(rconIp,rconPort,password.getBytes());
            return true;
        } catch (AuthenticationException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
