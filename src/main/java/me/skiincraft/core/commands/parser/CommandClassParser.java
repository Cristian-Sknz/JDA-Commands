package me.skiincraft.core.commands.parser;

import me.skiincraft.core.commands.annotation.CommandController;
import me.skiincraft.core.commands.annotation.CommandExecutor;
import me.skiincraft.core.commands.annotation.CommandOption;
import me.skiincraft.core.commands.configuration.ArgumentValidator;
import me.skiincraft.core.commands.impl.DefaultArgumentValidator;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandClassParser {

    private final Class<?> cmd;
    private final boolean argumentValidator;

    public CommandClassParser(Class<?> cmd, boolean containsArgumentValidator) {
        this.cmd = cmd;
        this.argumentValidator = containsArgumentValidator;
    }

    public Map<String, Object> parse(){
        Map<String, Object> attributes = new HashMap<>();
        checkClassAnnotation(attributes);
        checkClassMethods(attributes);
        if (attributes.get("options") != null){
            ((CommandData) attributes.get("commandData"))
                    .addOptions((OptionData[]) attributes.get("options"));
        }
        return attributes;
    }


    private void checkClassAnnotation(Map<String, Object> map){
        if (!cmd.isAnnotationPresent(CommandController.class)){
            throw new RuntimeException("Não está presente @CommandController, nesta classe!");
        }
        CommandController controller = cmd.getAnnotation(CommandController.class);
        CommandData data = new CommandData(controller.name(), controller.description());
        map.put("commandData", data);
        map.put("aliases", controller.aliases());
        map.put("isSlash", controller.slash());
        map.put("defaultCommand", controller.defaultCommand());
        checkValidator(map, controller);
        if (controller.options().length != 0){
            OptionData[] options = Arrays.stream(controller.options())
                    .map(this::createOption)
                    .toArray(OptionData[]::new);

            map.put("options", options);
            return;
        }
        map.put("options", null);
    }

    private void checkValidator(Map<String, Object> map, CommandController controller){
        if (!argumentValidator){
            Class<? extends ArgumentValidator> validator = controller.validator();
            if (validator == DefaultArgumentValidator.class){
                map.put("validator", DefaultArgumentValidator.getInstance());
                return;
            }
            Optional<Constructor<?>> optional = Arrays.stream(validator.getConstructors())
                    .filter(constructor -> constructor.getParameterCount() == 0).findFirst();

            if (optional.isEmpty()){
                throw new RuntimeException("Validator sem construtor vazio");
            }
            try {
                Constructor<?> constructor = optional.get();
                ArgumentValidator instance = ((ArgumentValidator) constructor.newInstance());
                map.put("validator", instance);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void checkClassMethods(Map<String, Object> map){
        Optional<Method> optional = Arrays.stream(this.cmd.getMethods())
                .filter(m -> m.isAnnotationPresent(CommandExecutor.class)).findFirst();

        if (optional.isEmpty()) {
            throw new RuntimeException("Não está presente @CommandExecutor, em nenhum método desta classe!");
        }

        CommandExecutor commandExecutor = optional.get().getAnnotation(CommandExecutor.class);
        map.put("commandExecutor", optional.get());
        if (commandExecutor.options().length != 0){
            OptionData[] options = Arrays.stream(commandExecutor.options())
                    .map(this::createOption)
                    .toArray(OptionData[]::new);

            map.put("options", options);
        }
    }

    private OptionData createOption(CommandOption option){
        if (option.choices().length != 0){
            OptionData optionData = new OptionData(option.type(), option.name(), option.description(), option.isRequired());
            optionData.addChoices(Arrays.stream(option.choices())
                    .map(this::createChoice)
                    .collect(Collectors.toList()));

            return optionData;
        }
        return new OptionData(option.type(), option.name(), option.description(), option.isRequired());
    }

    private Command.Choice createChoice(CommandOption.Choice option){
        return new Command.Choice(option.name(), option.value());
    }
}
