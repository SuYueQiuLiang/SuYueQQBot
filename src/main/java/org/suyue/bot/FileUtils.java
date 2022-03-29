package org.suyue.bot;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static String readFileToString(String path) throws IOException {
        File file = new File(path);
        if(!file.exists())
            return "";
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine())!=null)
            str.append(line).append("\n");
        bufferedReader.close();
        inputStreamReader.close();
        return str.toString();
    }
    public static void saveFileWithString(String str,String path) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(str);
            bufferedWriter.close();
            writer.close();
        }else if(file.exists()){
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(str);
            bufferedWriter.close();
            writer.close();
        }
    }
}
