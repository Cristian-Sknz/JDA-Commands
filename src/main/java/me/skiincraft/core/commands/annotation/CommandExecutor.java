package me.skiincraft.core.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**<h2>CommandExecutorInterface</h2>
 * <small>CommandController Annotations</small>
 *
 * <p>Use esta anotação para indicar qual método
 *   <br> de uma classe é um CommandExecutor de um {@link CommandController}
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandExecutor {

    /**
     * <h3>options()</h3>
     * <p>São as opções/parametros do comando.</p>
     */
    CommandOption[] options() default {};
}
