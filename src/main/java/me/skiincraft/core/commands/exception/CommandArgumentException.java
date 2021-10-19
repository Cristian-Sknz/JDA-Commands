package me.skiincraft.core.commands.exception;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandArgumentException extends RuntimeException {

    private final OptionData option;
    private CommandData commandData;
    private Class<?> command;
    private Guild guild;
    private Message message;
    private User user;

    public CommandArgumentException(String message, OptionData option) {
        super(message);
        this.option = option;
    }
    public CommandArgumentException(String message, OptionData option, Throwable cause) {
        super(message, cause);
        this.option = option;
    }

    public CommandArgumentException(CommandArgumentException e, Class<?> command, CommandData commandData, Guild guild, Message message, User user){
        this(e.getMessage(), e.getOption(), e.getCause());
        this.command = command;
        this.commandData = commandData;
        this.guild = guild;
        this.message = message;
        this.user = user;
    }

    public Class<?> getCommand() {
        return command;
    }

    public Guild getGuild() {
        return guild;
    }

    public Message getCommandMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public OptionData getOption() {
        return option;
    }

    public CommandData getCommandData() {
        return commandData;
    }
}
