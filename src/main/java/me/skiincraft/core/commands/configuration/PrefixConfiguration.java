package me.skiincraft.core.commands.configuration;

import net.dv8tion.jda.api.entities.Guild;

/**<h1>PrefixConfiguration</h1>
 *
 * <p>Classe de configuração de prefixos, todo default command antes
 * de ser chamado, ira passar por esta classe.</p>
 *
 * <h2>Exemplos de uso:</h2>
 * <h3>Prefixo unico</h3>
 * <pre>
 * JDACommands commands = new JDACommands((guild) -> "+", exceptionHandler);
 * </pre>
 * <h3>Com banco de dados</h3>
 * <pre>
 * PrefixConfiguration prefix = (guild) -> {
 *  GuildRepository repository = Util.getRepository();
 *  GuildDatabase guildDatabase = repository.getByGuildId(guild.getIdLong());
 *  return guildDatabase.getPrefix();
 * }
 * </pre>
 */
@FunctionalInterface
public interface PrefixConfiguration {
    String getPrefix(Guild guild);
}
