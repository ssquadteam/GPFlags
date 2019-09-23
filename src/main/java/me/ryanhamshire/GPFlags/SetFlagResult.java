package me.ryanhamshire.GPFlags;

public class SetFlagResult {

    boolean success;
    MessageSpecifier message;

    public SetFlagResult(boolean success, MessageSpecifier message) {
        this.success = success;
        this.message = message;
    }

    SetFlagResult(boolean success, Messages messageID, String... args) {
        this.success = success;
        this.message = new MessageSpecifier(messageID, args);
    }

}
