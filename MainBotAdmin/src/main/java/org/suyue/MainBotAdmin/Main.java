package org.suyue.MainBotAdmin;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.suyue.bot.Perms;
import org.suyue.bot.SuYueBotMod;
import org.suyue.bot.Target;

import java.io.IOException;
import java.util.Map;

public class Main implements SuYueBotMod {
    private static boolean isMessageOnlyPlainText(MessageChain messageChain){
        for(int i =1;i< messageChain.size();i++)
            if(!(messageChain.get(i) instanceof PlainText))
                return false;
        return true;
    }
    @Override
    public void receiveMessage(MessageEvent messageEvent) {

    }
    @Override
    public void receiveFriendMessage(FriendMessageEvent friendMessageEvent) {
        long senderId = friendMessageEvent.getSender().getId();
        MessageChain messageChain = friendMessageEvent.getMessage();
        String messageStr = messageChain.contentToString();
        if(Perms.getPerms(senderId)!= Target.owner)
            return;
        if(messageStr.startsWith("addAdministrator")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<2)
                    friendMessageEvent.getSender().sendMessage("请求格式错误");
                else {
                    try {
                        long userId = Integer.parseInt(split[1]);
                        Perms.addPerms(userId,Target.administrator);
                        friendMessageEvent.getSender().sendMessage("保存成功");
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        friendMessageEvent.getSender().sendMessage("请求格式错误");
                    } catch (IOException e) {
                        e.printStackTrace();
                        friendMessageEvent.getSender().sendMessage("保存权限组失败");
                    }
                }
            }else friendMessageEvent.getSender().sendMessage("携带非法资源");
        }else if(messageStr.startsWith("addOwner")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<2)
                    friendMessageEvent.getSender().sendMessage("请求格式错误");
                else {
                    try {
                        long userId = Integer.parseInt(split[1]);
                        Perms.addPerms(userId,Target.owner);
                        friendMessageEvent.getSender().sendMessage("保存成功");
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        friendMessageEvent.getSender().sendMessage("请求格式错误");
                    } catch (IOException e) {
                        e.printStackTrace();
                        friendMessageEvent.getSender().sendMessage("保存权限组失败");
                    }
                }
            }else friendMessageEvent.getSender().sendMessage("携带非法资源");
        }else if(messageStr.startsWith("removePerm")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<2)
                    friendMessageEvent.getSender().sendMessage("请求格式错误");
                else {
                    try {
                        long userId = Integer.parseInt(split[1]);
                        Perms.removePerms(userId);
                        friendMessageEvent.getSender().sendMessage("保存成功");
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        friendMessageEvent.getSender().sendMessage("请求格式错误");
                    } catch (IOException e) {
                        e.printStackTrace();
                        friendMessageEvent.getSender().sendMessage("保存权限组失败");
                    }
                }
            }else friendMessageEvent.getSender().sendMessage("携带非法资源");
        }else if(messageStr.startsWith("listPerm")){
            if(isMessageOnlyPlainText(messageChain)){
                Map<Long,Target> targetMap = Perms.listPerms();
                StringBuilder builder = new StringBuilder();
                for(Map.Entry<Long,Target> m: targetMap.entrySet())
                    builder.append("userId：").append(m.getKey()).append("  权限等级：").append(m.getValue()).append("\n");
                friendMessageEvent.getSender().sendMessage(builder.toString());
            }else friendMessageEvent.getSender().sendMessage("携带非法资源");
        }
    }

    @Override
    public void receiveGroupMessage(GroupMessageEvent groupMessageEvent) {
        long senderId = groupMessageEvent.getSender().getId();
        System.out.println(Perms.getPerms(senderId));
        MessageChain messageChain = groupMessageEvent.getMessage();
        String messageStr = messageChain.contentToString();
        if(Perms.getPerms(senderId)!= Target.owner)
            return;
        if(messageStr.startsWith("addAdministrator")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<2)
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                else {
                    try {
                        long userId = Integer.parseInt(split[1]);
                        Perms.addPerms(userId,Target.administrator);
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存成功"));
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存权限组失败"));
                    }
                }
            }else if(messageChain.get(1) instanceof PlainText && messageChain.get(2) instanceof At){
                try {
                    long userId = ((At) messageChain.get(2)).getTarget();
                    Perms.addPerms(userId,Target.administrator);
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存成功"));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                } catch (IOException e) {
                    e.printStackTrace();
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存权限组失败"));
                }
            }else groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("携带非法资源"));
        }else if(messageStr.startsWith("addOwner")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<2)
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                else {
                    try {
                        long userId = Integer.parseInt(split[1]);
                        Perms.addPerms(userId,Target.owner);
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存成功"));
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存权限组失败"));
                    }
                }
            }else if(messageChain.get(1) instanceof PlainText && messageChain.get(2) instanceof At){
                try {
                    long userId = ((At) messageChain.get(2)).getTarget();
                    Perms.addPerms(userId,Target.owner);
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存成功"));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                } catch (IOException e) {
                    e.printStackTrace();
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存权限组失败"));
                }
            }else groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("携带非法资源"));
        }else if(messageStr.startsWith("removePerm")){
            if(isMessageOnlyPlainText(messageChain)){
                String[] split = messageStr.split(" ");
                if(split.length<2)
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                else {
                    try {
                        long userId = Integer.parseInt(split[1]);
                        Perms.removePerms(userId);
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存成功"));
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存权限组失败"));
                    }
                }
            }else if(messageChain.get(1) instanceof PlainText && messageChain.get(2) instanceof At){
                try {
                    long userId = ((At) messageChain.get(2)).getTarget();
                    Perms.removePerms(userId);
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存成功"));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("请求格式错误"));
                } catch (IOException e) {
                    e.printStackTrace();
                    groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("保存权限组失败"));
                }
            }else groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("携带非法资源"));
        }else if(messageStr.startsWith("listPerm")){
            if(isMessageOnlyPlainText(messageChain)){
                Map<Long,Target> targetMap = Perms.listPerms();
                StringBuilder builder = new StringBuilder("\n");
                for(Map.Entry<Long,Target> m: targetMap.entrySet())
                    builder.append("userId：").append(m.getKey()).append("  权限等级：").append(m.getValue()).append("\n");
                groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus(builder.toString()));
            }else groupMessageEvent.getGroup().sendMessage(new At(groupMessageEvent.getSender().getId()).plus("携带非法资源"));
        }
    }

}
