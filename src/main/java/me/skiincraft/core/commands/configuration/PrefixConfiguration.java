package me.skiincraft.core.commands.configuration;

import net.dv8tion.jda.api.entities.Guild;

@FunctionalInterface
public interface PrefixConfiguration {
    String getPrefix(Guild guild);
}
