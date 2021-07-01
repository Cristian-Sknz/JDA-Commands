package me.skiincraft.core.test;

import me.skiincraft.core.commands.annotation.CommandController;
import me.skiincraft.core.commands.annotation.CommandExecutor;
import me.skiincraft.core.commands.annotation.CommandOption;
import me.skiincraft.core.commands.annotation.CommandParameter;
import me.skiincraft.core.commands.model.CommandReply;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@CommandController(name = "hello", description = "Say hello to the world, say hello to someone")
public class HelloCommand {

    @CommandExecutor(options = {
            @CommandOption(type = OptionType.STRING,
                    name = "name",
                    description = "Enter someone's name!",
                    isRequired = true
            )})
    public void execute(CommandReply commandReply, @CommandParameter("name") String name){
        commandReply.replyFormat("Hello World! Have a nice day %s!", name).queue();
    }
}
