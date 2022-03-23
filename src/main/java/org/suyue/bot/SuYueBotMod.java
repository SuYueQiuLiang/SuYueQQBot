package org.suyue.bot;

import net.mamoe.mirai.message.data.MessageChain;

public interface SuYueBotMod {
    public String receiveGroupMessage(long groupId,long userId,MessageChain messages);
    public String receiveMessage(long userId,MessageChain messages);
}
