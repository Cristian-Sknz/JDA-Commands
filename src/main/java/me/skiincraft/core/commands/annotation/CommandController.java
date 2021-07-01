package me.skiincraft.core.commands.annotation;

import me.skiincraft.core.commands.configuration.ArgumentValidator;
import me.skiincraft.core.commands.impl.DefaultArgumentValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**<h2>CommandController</h2>
 * <small>CommandController Annotations</small>
 * <p>
 *     Use esta anotação para registrar comandos automaticamente.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandController {

    /**
     * <h3>name()</h3>
     * <p>Nome que o comando deve ser chamado.</p>
     */
    String name();

    /**
     * <h3>aliases()</h3>
     * <p>Nomes alternativos que o comando pode ser chamado.</p>
     * <i>Somente em comandos não slash (defaultCommands)!</i>
     */
    String[] aliases() default {};

    /**
     * <h3>description()</h3>
     * <p>Descrição do comando.</p>
     */
    String description() default "This command has no description.";

    /**
     * <h3>options()</h3>
     * <p>São as opções/parametros do comando.</p>
     *
     * <p>
     *     Também podem ser implementados direto no metodo {@link CommandExecutor}
     * </p>
     */
    CommandOption[] options() default {};

    /**
     * <h3>validator()</h3>
     * <p>Indique a classe que fara a conversão dos argumentos, para comandos não slash (default commands)</p>
     * <p>Talvez seja necessário para alguns comands, e quase obrigatório caso
     *    <br><code>defaultCommand()</code> seja <code>true</code>.
     * </p>
     */
    Class<? extends ArgumentValidator> validator() default DefaultArgumentValidator.class;

    /**
     * <h3>slash()</h3>
     * <p>Fazer deste comando um SlashCommand?</p>
     */
    boolean slash() default true;

    /**
     * <h3>defaultCommand()</h3>
     * <p>Fazer com que este comando funcione sem ser slash?</p>
     */
    boolean defaultCommand() default true;
}
