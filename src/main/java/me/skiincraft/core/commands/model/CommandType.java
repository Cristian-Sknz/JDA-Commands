package me.skiincraft.core.commands.model;

public enum CommandType {

    SlashCommand(0), DefaultCommand(1);

    private int id;

    CommandType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
