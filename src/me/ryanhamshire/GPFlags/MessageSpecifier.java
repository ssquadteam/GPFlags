package me.ryanhamshire.GPFlags;

class MessageSpecifier
{
    Messages messageID;
    String [] messageParams;
    
    MessageSpecifier(Messages messageID, String ... messageParams)
    {
        this.messageID = messageID;
        this.messageParams = messageParams;
    }
}
