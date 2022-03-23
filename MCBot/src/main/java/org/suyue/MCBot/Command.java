package org.suyue.MCBot;

public class Command {
    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public void setCommandStr(String commandStr) {
        this.commandStr = commandStr;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public String getCommandStr() {
        return commandStr;
    }

    public String getReturnInfo() {
        return returnInfo;
    }

    public Command() {
    }

    public Command(String commandStr, String returnInfo,boolean administrator) {
        this.commandStr = commandStr;
        this.returnInfo = returnInfo;
        this.administrator = administrator;
    }
    private boolean administrator = true;
    private String commandStr;
    private String returnInfo;
}
