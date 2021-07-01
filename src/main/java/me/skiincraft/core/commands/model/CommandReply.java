package me.skiincraft.core.commands.model;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CommandReply {

    private final Object replier;

    public CommandReply(Interaction interaction) {
        this.replier = interaction;
    }

    public CommandReply(Message message) {
        this.replier = message;
    }

    public RestAction<?> reply(@Nonnull Message message) {
        return this.ignoreReflectionException("reply", message, new Class[] { Message.class });
    }

    public RestAction<?> reply(@Nonnull CharSequence message) {
        return this.ignoreReflectionException("reply", message, new Class[] { CharSequence.class, String.class });
    }

    public RestAction<?> reply(@Nonnull String message) {
        return this.reply((CharSequence) message);
    }

    public RestAction<?> replyFormat(@Nonnull String format, @Nonnull Object... obj){
        return this.reply(String.format(format, obj));
    }

    public MessageAction reply(@Nonnull File file, @Nonnull AttachmentOption... options){
        return getMessage().reply(file, options);
    }

    public MessageAction reply(@Nonnull InputStream data, @Nonnull String name, @Nonnull AttachmentOption... options){
        return getMessage().reply(data, name, options);
    }

    public MessageAction reply(@Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options){
        return getMessage().reply(data, name, options);
    }

    private Message getMessage(){
        if (replier instanceof Message){
            return ((Message) replier);
        }
        throw new UnsupportedOperationException("Isso não pode ser feito em um SlashCommand!");
    }


    public RestAction<?> ignoreReflectionException(String methodName, Object message, @Nonnull Class<?>[] parameters){
        try {
            Method method = getReplierClass().getMethod(methodName, parameters[0]);
            return (RestAction<?>) method.invoke(replier, message);
        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored){
            if (parameters.length >= 2)
            return this.ignoreReflectionException(methodName, message, new Class[] {parameters[1]});
        }
        return null;
    }

    public Class<?> getReplierClass(){
        return replier.getClass();
    }

    public boolean isSlash() {
        return replier instanceof Interaction;
    }
}
