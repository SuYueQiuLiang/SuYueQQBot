package org.suyue.bot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException{
        System.out.println("开始读取配置文件");
        if(!Config.loadConfig()){
            System.out.println("读取配置文件失败！");
            System.exit(0);
        }else System.out.println("读取成功");
        Scanner scanner = new Scanner(System.in);
        String line;
        while (scanner.hasNext()){
            line = scanner.nextLine();
            CommandHandler.handleCommand(line);
        }
    }
}
