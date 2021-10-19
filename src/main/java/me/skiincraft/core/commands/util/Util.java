package me.skiincraft.core.commands.util;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {

    public static String[] getCommandPath(CommandData command){
        List<String> options = new ArrayList<>();
        options.add(command.getName());
        options.addAll(command.getOptions().stream().map(option -> {
            if (!option.getChoices().isEmpty()){
                return String.format((option.isRequired()) ? "<%s>" : "[%s]", option.getChoices().stream()
                        .map(Command.Choice::getName)
                        .collect(Collectors.joining("/")));
            }
            return String.format((option.isRequired()) ? "<%s>" : "[%s]", option.getName());
        }).collect(Collectors.toList()));
        return options.toArray(String[]::new);
    }

    public static boolean startWithIgnoreCase(String arg, String prefix){
        return arg.toLowerCase().startsWith(prefix.toLowerCase());
    }

    public static String[] removePrefix(String[] args, String prefix){
        args[0] = args[0].substring(prefix.length());
        return args;
    }

    public static String removePrefix(String args, String prefix){
        if (startWithIgnoreCase(args, prefix)){
            return args.substring(prefix.length());
        }
        return args;
    }

    public static String[] getCommandArgs(String text){
        List<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher regexMatcher = regex.matcher(text);
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList.toArray(String[]::new);
    }

    public static String[] getArgs(String prefix, String text){
        return removePrefix(getCommandArgs(text), prefix);
    }

    public static Optional<Map<String, Object>> getAttributeByName(Map<Class<?>, Map<String, Object>> commands, String name, boolean withAliases){
        Stream<Map<String, Object>> stream = commands.values().stream()
                .filter(atributes -> {
                    CommandData data = (CommandData) atributes.get("commandData");
                    if (data.getName().equalsIgnoreCase(name)){
                        return true;
                    }
                    if (withAliases){
                        return Arrays.stream((String[]) atributes.get("aliases")).filter(Objects::nonNull).anyMatch(name::equalsIgnoreCase);
                    }
                    return false;
                });

        return stream.findFirst();
    }

}
