package me.skiincraft.core.commands.model;

public class CommandMessage {

    private final String name;
    private final String[] args;
    private final CommandType type;
    private final Class<?> command;
    private final Object commandInstance;

    public CommandMessage(String name, String[] args, CommandType type, Class<?> command, Object commandInstance) {
        this.name = name;
        this.args = args;
        this.type = type;
        this.command = command;
        this.commandInstance = commandInstance;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }

    public CommandType getType() {
        return type;
    }

    public Class<?> getCommand() {
        return command;
    }

    public Object getCommandInstance() {
        return commandInstance;
    }
}
