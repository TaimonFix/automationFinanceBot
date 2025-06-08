package ru.vsu.cs.automationFinanceBot.model.enums;

public enum Command {
    PREFIX("/"),
    START("/start"),
    MENU("/menu");

    private final String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
