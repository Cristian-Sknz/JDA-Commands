package me.skiincraft.core.commands.configuration;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Map;

@FunctionalInterface
public interface ArgumentValidator {

    Map<OptionData, Object> validate(Map<OptionData, Object> parameters, String[] args);


}
