# JDA-Commands
An experimental project to create JDA commands using annotations

## Readme!
This project is completely experimental, it's an attempt to merge
"commands common" to slash commands. To make this more interesting,
decide to make annotations interact fully with the commands.

I'll leave examples below if you want to understand how it works.
If you support this idea and want some features and other changes, don't hesitate to make issues and pull requests!

## Examples
Commands are made in slash command format, <br> after being slashed, they are converted to "common commands".
### Start JDA Application
```java
public static void main(String[] args) {
     JDABuilder builder = JDABuilder.createDefault("BOT_TOKEN");
     
     JDACommands commands = new JDACommands(guild -> "+");
     builder.addEventListeners(commands);
     commands.register(HelloCommand.class);
     JDA jda = builder.build();

     commands.publishCommands(jda.updateCommands());
```

You can use a database to set the prefix for each guild:
```java
PrefixConfiguration prefix = (guild) -> {
    GuildRepository repository = Util.getRepository();
    GuildDatabase guildDatabase = repository.getByGuildId(guild.getIdLong());
    return guildDatabase.getPrefix();
};

JDACommands commands = new JDACommands(prefix);
```

### Hello World
Here is an example of a simple command, a Hello World!
```java
import ...

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
```
#### Output
- Slash Command <br>
![SlashCommand](.assets/example-1-0.png)
![SlashCommand Reply](.assets/example-1-1.png)<br>
  
- Common Command <br>
![img.png](.assets/example-1-3.png)
![img.png](.assets/example-1-2.png)


