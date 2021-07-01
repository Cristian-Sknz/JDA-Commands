package me.skiincraft.core.commands.annotation;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**<h2>CommandOption</h2>
 * <small>CommandController Annotations</small>
 * <p>
 *     Use esta anotação para definir opções/parâmetros de um comando.
 *     <i>Essa anotação deve ser utilizada em conjunto com {@link CommandController CommandAnnotation} </i>
 * </p>
 *
 * @see CommandController
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface CommandOption {

    OptionType type();
    String name();
    String description();
    Choice[] choices() default {};
    boolean isRequired() default false;

    @interface Choice {
        String name();
        int value();
    }
}
