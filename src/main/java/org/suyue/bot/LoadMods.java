package org.suyue.bot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadMods {
    public static Map<String,SuYueBotMod> mods = new HashMap<>();
    public static Map<String,String> modHelps = new HashMap<>();
    public static void loadMods(String path) throws IOException {
        File modDir = new File(path);
        if(!modDir.isDirectory()||!modDir.exists())
            return;
        File[] files = modDir.listFiles();
        if(files==null)
            return;
        ArrayList<URL> urls = new ArrayList<>();
        for(File file : files){
            if(file.getName().endsWith(".jar")) {
                urls.add(new URL("file:" + path + file.getName()));
            }
        }
        URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]),Thread.currentThread().getContextClassLoader());
        for(URL url : urls){
            URL modInfo = new URL("jar:" + url + "!/modInfo.json");
            URL modHelp = new URL("jar:" + url + "!/help.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(modInfo.openStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder info = new StringBuilder(),help = new StringBuilder();
            while ((line = bufferedReader.readLine())!=null)
                info.append(line);
            bufferedReader = new BufferedReader(new InputStreamReader(modHelp.openStream(), StandardCharsets.UTF_8));
            while ((line = bufferedReader.readLine())!=null)
                help.append(line).append("\n");
            help.deleteCharAt(help.length()-1);
            try{
                JSONObject modInfoJson = JSON.parseObject(info.toString());
                mods.put(modInfoJson.getString("modName"),(SuYueBotMod) loader.loadClass(modInfoJson.getString("modInterface")).getDeclaredConstructor().newInstance());
                modHelps.put(modInfoJson.getString("modName"), help.toString());
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException | InstantiationException e) {
                e.printStackTrace();
                System.out.println("载入 "+url.toString()+" 失败！");
            }
        }
    }
    public static void loadNewMod(String path,String modName) throws IOException {
        URL url = new URL("file:" + path + modName);
        URLClassLoader loader = new URLClassLoader(new URL[]{url},Thread.currentThread().getContextClassLoader());
        URL modInfo = new URL("jar:" + url + "!/modInfo.json");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(modInfo.openStream(), StandardCharsets.UTF_8));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = bufferedReader.readLine())!=null)
            builder.append(line);
        try{
            JSONObject modInfoJson = JSON.parseObject(builder.toString());
            mods.put(modInfoJson.getString("modName"),(SuYueBotMod) loader.loadClass(modInfoJson.getString("modInterface")).getDeclaredConstructor().newInstance());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
            System.out.println("载入 "+ url +" 失败！");
        }
    }

}