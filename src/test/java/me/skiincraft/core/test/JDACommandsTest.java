package me.skiincraft.core.test;

import me.skiincraft.core.commands.JDACommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.function.BiConsumer;

public class JDACommandsTest {

    private static final String BOT_TOKEN = "";

    public static void main(String[] args) throws LoginException {
        runDiscordBot((jda, commands) -> {});
    }

    public static void runDiscordBot(BiConsumer<JDA, JDACommands> consumer) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(BOT_TOKEN);
        JDACommands commands = new JDACommands(guild -> "+");
        builder.setActivity(Activity.playing("JDA CommandTest"));
        builder.addEventListeners(commands);
        commands.register(HelloCommand.class);
        JDA jda = builder.build();

        commands.publishCommands(jda.updateCommands());
        consumer.accept(jda, commands);
    }

}
