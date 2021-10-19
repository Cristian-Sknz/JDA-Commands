package me.skiincraft.core.commands.configuration;

import me.skiincraft.core.commands.exception.CommandArgumentException;
import me.skiincraft.core.commands.model.CommandReply;
import me.skiincraft.core.commands.util.Util;

public interface CommandExceptionHandler {

    default void onCommandArgumentException(CommandArgumentException exception, CommandReply reply) {
        reply.reply(String.format("Tente utilizar: '%s'", String.join(" ", Util.getCommandPath(exception.getCommandData())))).queue();
    }

    default void onCommandException(Exception e, CommandReply reply) {
        reply.reply("There was a problem processing your command. Please contact the developer.").queue();
    }
}
