package me.skiincraft.core.commands.impl;

import me.skiincraft.core.commands.configuration.ArgumentValidator;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Map;

public class DefaultArgumentValidator implements ArgumentValidator {

    private static ArgumentValidator instance = new DefaultArgumentValidator();

    @Override
    public Map<OptionData, Object> validate(Map<OptionData, Object> parameters, String[] args) {
        OptionData[] values = parameters.keySet().toArray(OptionData[]::new);
        String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
        for (int i = 0; i < values.length; i++){
            OptionData value = values[i];
            if (i + 1 > realArgs.length) {
                parameters.put(value, null);
                continue;
            }
            switch (value.getType()) {
                case USER:
                case CHANNEL:
                case ROLE:
                case MENTIONABLE:
                    if (isMentionable(realArgs[i])) {
                        parameters.put(value, Long.parseLong(realArgs[i].replaceAll("\\D+", "")));
                    }
                    continue;
                case STRING:
                    parameters.put(value, realArgs[i]);
                    continue;
                case BOOLEAN:
                    parameters.put(value, Boolean.parseBoolean(realArgs[i]));
                    continue;
                case INTEGER:
                    if (realArgs[i].matches("-?\\d+(\\.\\d+)?")) {
                        parameters.put(value, Long.parseLong(realArgs[i]));
                    }
                    parameters.put(value, -1L);
            }
        }

        return parameters;
    }

    public boolean isMentionable(String string){
        return startsWithAndEndsWith(string, "<@", ">")||
                startsWithAndEndsWith(string, "<@!", ">") ||
                startsWithAndEndsWith(string, "<#", ">")||
                startsWithAndEndsWith(string, "<@&", ">");
    }

    private boolean startsWithAndEndsWith(String text, String start, String end){
        return text.startsWith(start) && text.endsWith(end);
    }

    public static ArgumentValidator getInstance() {
        return instance;
    }
}
