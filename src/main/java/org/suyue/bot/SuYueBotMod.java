package org.suyue.bot;


import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public interface SuYueBotMod {
    void receiveGroupMessage(GroupMessageEvent event);
    void receiveMessage(MessageEvent event);
    void receiveFriendMessage(FriendMessageEvent event);
}
