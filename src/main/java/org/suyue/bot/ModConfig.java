package org.suyue.bot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;

public class ModConfig {
    private final File configFile;
    public ModConfig(String modName){
        configFile = new File("./mods/" + modName + "/" + modName + ".cfg");
    }
    public String readConfig(){
        if(!configFile.exists())
            return "";
        else {
            try{
                StringBuilder builder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
                String line;
                while ((line=bufferedReader.readLine())!=null)
                    builder.append(line);
                bufferedReader.close();
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public boolean saveConfig(String str){
        try {
            if(!configFile.exists()&&(!configFile.getParentFile().isDirectory()||!configFile.getParentFile().exists())){
                if(!configFile.getParentFile().mkdirs())
                    return false;
                if(!configFile.createNewFile())
                    return false;
            }else if(!configFile.exists()&&configFile.getParentFile().isDirectory()){
                if(!configFile.createNewFile())
                    return false;
            }
            if(!configFile.exists())
                return false;
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
            bufferedWriter.write(str);
            bufferedWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
