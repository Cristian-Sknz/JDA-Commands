package me.skiincraft.core.commands.configuration;

import me.skiincraft.core.commands.exception.CommandArgumentException;
import me.skiincraft.core.commands.model.CommandReply;
import me.skiincraft.core.commands.util.Util;


/** <h1>CommandExceptionHandler</h1>
 *
 *  <p>Está classe irá manipular todos os erros que podem ocorrer em um comando,
 *      <br> Seja erros de argumentos incorretos ou exceções disparadas por erros internos.
 *  </p>
 */
public interface CommandExceptionHandler {

    default void onCommandArgumentException(CommandArgumentException exception, CommandReply reply) {
        reply.reply(String.format("Try to use: '%s'", String.join(" ", Util.getCommandPath(exception.getCommandData())))).queue();
    }

    default void onCommandException(Exception e, CommandReply reply) {
        reply.reply("There was a problem processing your command. Please contact the developer.").queue();
    }
}
