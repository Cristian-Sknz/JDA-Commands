package me.skiincraft.core.commands;

import me.skiincraft.core.commands.annotation.CommandParameter;
import me.skiincraft.core.commands.configuration.ArgumentValidator;
import me.skiincraft.core.commands.configuration.CommandExceptionHandler;
import me.skiincraft.core.commands.configuration.PrefixConfiguration;
import me.skiincraft.core.commands.exception.CommandArgumentException;
import me.skiincraft.core.commands.exception.CommandRegisterException;
import me.skiincraft.core.commands.model.CommandReply;
import me.skiincraft.core.commands.parser.CommandClassParser;
import me.skiincraft.core.commands.parser.CommandParameterInjector;
import me.skiincraft.core.commands.parser.OptionMappingParser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static me.skiincraft.core.commands.util.Util.*;

/** <h1>JDACommands</h1>
 *
 * <p>Está classe irá administrar os comandos da sua aplicação JDA,
 * convertendo para SlashCommands e facilitando o trace de erros.</p>
 *
 * <p>Esta classe pode ser registrada como EventListener do JDA</p>
 */
public class JDACommands implements EventListener {

    private final PrefixConfiguration prefixConfiguration;
    private final CommandExceptionHandler exceptionHandler;
    private final Map<Class<?>, Map<String, Object>> commands;


    /**Este construtor possui parametros para configurar coisas basicas como o prefixo
     * e o manipulador de exceções.
     *
     * @param prefix Configuração de prefixo dos comandos.
     * @param exceptionHandler Configuração do manipulador de exceções.
     *
     * @see PrefixConfiguration
     * @see CommandExceptionHandler
     */
    public JDACommands(PrefixConfiguration prefix, CommandExceptionHandler exceptionHandler) {
        this.prefixConfiguration = prefix;
        this.exceptionHandler = exceptionHandler;
        this.commands = new HashMap<>();
    }

    /** Este método serve para fazer o registro de comandos que
     * serão convertidos para SlashCommands e posteriormente
     * publicado.
     *
     * @param commandClass Classe que contém o comando com as anotações
     */
    public <T> void register(@Nonnull Class<T> commandClass) {
        this.register(commandClass, null, null);
    }

    /** Este método serve para fazer o registro de comandos que
     * serão convertidos para SlashCommands e posteriormente
     * publicado.
     *
     * @param commandClass Classe que contém o comando com as anotações
     * @param argumentValidator Uma classe que fará a validação do comando antes de ser executado.
     *
     * @see me.skiincraft.core.commands.impl.DefaultArgumentValidator
     */
    public <T> void register(@Nonnull Class<T> commandClass, ArgumentValidator argumentValidator) {
        this.register(commandClass, null, argumentValidator);
    }

    /** Este método serve para fazer o registro de comandos que
     * serão convertidos para SlashCommands e posteriormente
     * publicado.
     *
     * @param commandClass Classe que contém o comando com as anotações
     * @param instance Uma instância ativa que poderá ser utilizada nos comandos.
     *                 <p>Example: <code>new MyCommand()</code></p>
     */
    public <T> void register(@Nonnull Class<T> commandClass, Supplier<T> instance) {
        this.register(commandClass, instance, null);
    }

    /** Este método serve para fazer o registro de comandos que
     * serão convertidos para SlashCommands e posteriormente
     * publicado.
     *
     * @param commandClass Classe que contém o comando com as anotações
     * @param instance Uma instância ativa que poderá ser utilizada nos comandos.
     *                 <p>Example: <code>new MyCommand()</code></p>
     * @param argumentValidator Uma classe que fará a validação do comando antes de ser executado.
     *
     * @see me.skiincraft.core.commands.impl.DefaultArgumentValidator
     */
    public <T> void register(@Nonnull Class<T> commandClass, Supplier<T> instance, ArgumentValidator argumentValidator) {
        Map<String, Object> commandConfiguration = new CommandClassParser(commandClass, Objects.nonNull(argumentValidator)).parse();
        if (Objects.isNull(instance)) {
            Optional<Constructor<?>> optional = stream(commandClass.getConstructors())
                    .filter(constructor -> constructor.getParameterCount() == 0)
                    .findFirst();

            if (optional.isEmpty()) {
                throw new CommandRegisterException("Class [%s] cannot be instantiated as it does " +
                        "not have an accessible public constructor.", commandClass.getSimpleName());
            }

            try {
                Constructor<?> constructor = optional.get();
                commandConfiguration.put("instance", constructor.newInstance());
            } catch (Exception e) {
                throw new CommandRegisterException("An error occurred while instantiating [%s] class", e, commandClass.getSimpleName());
            }
        } else {
            commandConfiguration.put("instance", instance.get());
        }
        if (Objects.nonNull(argumentValidator)) {
            commandConfiguration.put("validator", argumentValidator);
        }

        commands.put(commandClass, commandConfiguration);
    }

    @Override
    @SubscribeEvent
    public void onEvent(@NotNull GenericEvent event) {
        try {
            if (event instanceof SlashCommandEvent)
                this.slash((SlashCommandEvent) event);

            if (event instanceof GuildMessageReceivedEvent)
                this.defaultCommand((GuildMessageReceivedEvent) event);
        } catch (Exception e) {
            if (event instanceof SlashCommandEvent)
                exceptionHandler.onCommandException(e, new CommandReply(((SlashCommandEvent) event).getInteraction()));

            if (event instanceof GuildMessageReceivedEvent)
                exceptionHandler.onCommandException(e, new CommandReply(((GuildMessageReceivedEvent) event).getMessage()));
        }
    }

    public CommandParameterInjector prepareInjector(SlashCommandEvent e) {
        CommandParameterInjector injector = new CommandParameterInjector();
        injector.addBean(e.getGuild())
                .addBean(e.getOptions())
                .addBean(e.getUser())
                .addBean(e.getMember())
                .addBean(e.getCommandPath().split("/"))
                .addBean(new CommandReply(e.getInteraction()))
                .addBean(e);

        switch (e.getChannelType()) {
            case TEXT:
                injector.addBean(e.getTextChannel());
                break;
            case PRIVATE:
                injector.addBean(e.getPrivateChannel());
                break;
            default:
                injector.addBean(e.getMessageChannel());
                break;
        }
        return injector;
    }

    private CommandParameterInjector prepareInjector(GuildMessageReceivedEvent e) {
        CommandParameterInjector injector = new CommandParameterInjector();
        return injector.addBean(e.getGuild())
                .addBean(e.getAuthor())
                .addBean(e.getMember())
                .addBean(new CommandReply(e.getMessage()))
                .addBean(e)
                .addBean(e.getChannel());
    }

    private void slash(SlashCommandEvent e) throws Exception {
        Optional<Map<String, Object>> optional = getAttributeByName(commands, e.getName(), false);
        if (optional.isEmpty()) {
            return;
        }
        CommandParameterInjector injector = this.prepareInjector(e);
        Map<String, Object> attributes = optional.get();
        Method commandExecutor = (Method) attributes.get("commandExecutor");
        Object[] parameters = stream(commandExecutor.getParameters()).map(parameter -> {
            if (parameter.isAnnotationPresent(CommandParameter.class)) {
                String value = parameter.getAnnotation(CommandParameter.class).value();
                OptionMapping option = e.getOption(value);
                if (option == null) {
                    OptionData optionData = getOption(attributes,value);
                    if (optionData != null && optionData.getType() == OptionType.BOOLEAN){
                        return false;
                    }
                }
                return getCommandParameter(parameter, value, option, ((CommandData) attributes.get("commandData"))
                        .getOptions());
            }
            return injector.inject(parameter.getType());
        }).toArray(Object[]::new);
        commandExecutor.invoke(attributes.get("instance"), parameters);
    }

    private OptionData getOption(Map<String, Object> attributes, String name){
        Object options = attributes.get("options");
        if (Objects.isNull(options)){
            return null;
        }
        Optional<OptionData> option = Arrays.stream((OptionData[])options)
                .filter(optionData -> optionData.getName().equalsIgnoreCase(name))
                .findFirst();

        return option.orElse(null);
    }

    private void defaultCommand(GuildMessageReceivedEvent e) throws Exception {
        User member = Objects.requireNonNull(e.getMember(), "user").getUser();
        if (member.isBot() || !e.getChannel().canTalk()) {
            return;
        }
        String messageRaw = e.getMessage().getContentRaw();
        String prefix = prefixConfiguration.getPrefix(e.getGuild());

        if (!startWithIgnoreCase(messageRaw.split(" ")[0], prefix)) {
            return;
        }
        String[] args = getArgs(prefix, messageRaw);
        Optional<Map<String, Object>> optional = getAttributeByName(commands, args[0], true);
        if (optional.isEmpty()) {
            return;
        }

        Map<String, Object> attributes = optional.get();
        List<OptionData> dataOptions = ((CommandData) attributes.get("commandData")).getOptions();
        ArgumentValidator validator = (ArgumentValidator) attributes.get("validator");
        List<OptionMapping> options;
        try {
            options = OptionMappingParser.of(e.getMessage(), validator.validate(dataOptions.stream()
                    .collect(Collectors.toMap((value) -> value, (value) -> new Object[0])), args));
        } catch (CommandArgumentException ex) {
            exceptionHandler.onCommandArgumentException(new CommandArgumentException(ex,
                    attributes.get("instance").getClass(),
                            (CommandData) attributes.get("commandData"),
                    e.getGuild(),
                    e.getMessage(),
                    e.getAuthor()),
                    new CommandReply(e.getMessage()));
            return;
        }

        CommandParameterInjector injector = prepareInjector(e)
                .addBean(options)
                .addBean(args);

        Method commandExecutor = (Method) attributes.get("commandExecutor");
        Object[] methodParameters = stream(commandExecutor.getParameters()).map(parameter -> {
            if (parameter.isAnnotationPresent(CommandParameter.class)) {
                String value = parameter.getAnnotation(CommandParameter.class).value();
                OptionMapping option = options.stream().filter(map -> map.getName().equalsIgnoreCase(value))
                        .findFirst()
                        .orElse(null);

                return getCommandParameter(parameter, value, option, ((CommandData) attributes.get("commandData"))
                        .getOptions());
            }
            return injector.inject(parameter.getType());
        }).toArray(Object[]::new);
        commandExecutor.invoke(attributes.get("instance"), methodParameters);
    }

    private Object getCommandParameter(Parameter parameter, String value, OptionMapping option, List<OptionData> commandData) {
        Optional<OptionData> optional = commandData.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(value)).findFirst();
        if (Objects.isNull(option)) {
            return null;
        }

        if (optional.isPresent()) {
            OptionData optionData = optional.get();
            if (!optionData.getChoices().isEmpty()) {
                return optional.get().getChoices().stream()
                        .filter(choice -> Long.parseLong(choice.getAsString()) == option.getAsLong())
                        .map(Command.Choice::getName).findFirst().orElse(option.getAsString());
            }
        }
        if ((parameter.getType() == String.class))
            return option.getAsString();
        switch (option.getType()) {
            case STRING:
                return option.getAsString();
            case INTEGER:
                return option.getAsLong();
            case BOOLEAN:
                return (Objects.isNull(option.getAsString())) ? false : option.getAsBoolean();
            case USER:
                return option.getAsUser();
            case CHANNEL:
                return option.getAsMessageChannel();
            case ROLE:
                return option.getAsRole();
            case MENTIONABLE:
                return option.getAsMentionable();
            default:
                return null;
        }
    }

    public void publishCommands(CommandListUpdateAction cmdListUpdateAction) {
        for (Map<String, Object> attributes : commands.values()) {
            cmdListUpdateAction.addCommands((CommandData) attributes.get("commandData"));
        }
        cmdListUpdateAction.queue();
    }
}
