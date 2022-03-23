package org.suyue.bot;

import java.io.*;

public class FileUtils {
    public static String readFileToString(String path) throws IOException {
        File file = new File(path);
        if(!file.exists())
            return "";
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
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
        if(!file.exists()&&file.getParentFile().mkdirs())
            file.createNewFile();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(str);
        bufferedWriter.close();
        writer.close();
    }
}
