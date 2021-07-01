package me.skiincraft.core.commands.parser;

import me.skiincraft.core.commands.exception.CommandArgumentException;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OptionMappingParser extends OptionMapping {

    private final OptionData optionData;
    private final Object object;
    private final Message message;

    public OptionMappingParser(Message message, OptionData option, Object object) {
        super(DataObject.fromJson("{\"name\":\"" + option.getName() +"\"}"), null);
        this.optionData = option;
        this.object = object;
        this.message = message;
    }

    @Override
    public String getAsString() {
        return (Objects.isNull(object)) ? null : object.toString();
    }

    @NotNull
    @Override
    public OptionType getType() {
        return optionData.getType();
    }

    @Override
    public boolean getAsBoolean() {
        if (getType() != OptionType.BOOLEAN)
            throw new IllegalStateException("Cannot convert option of type " + getType() + " to boolean");

        return valid(object, false);
    }

    @Override
    public long getAsLong() {
        if (!containsLong()) {
            List<Command.Choice> choices = optionData.getChoices();
            if (choices.isEmpty()) {
                throw new IllegalStateException("Cannot convert option of type " + getType() + " to long");
            }
            Optional<Long> value = choices.stream().filter(choice -> choice.getName().equalsIgnoreCase(getAsString()))
                    .map(Command.Choice::getAsString)
                    .map(Long::parseLong)
                    .findFirst();

            return (value.isEmpty()) ? -1 : value.get();
        }

        return valid(object, -1L);
    }

    private boolean containsLong(){
        switch (getType()){
            case USER:
            case CHANNEL:
            case ROLE:
            case MENTIONABLE:
            case INTEGER:
                return true;
            default:
                return false;
        }
    }

    @Override
    public IMentionable getAsMentionable() {
        if (getAsLong() == -1){
            return null;
        }
        Optional<IMentionable> optional = message.getMentions().stream().filter(mention ->
            Long.parseLong(mention.getAsMention().replaceAll("\\D+", "")) == getAsLong()
        ).findFirst();
        if (optional.isEmpty()){
            return null;
        }
        return optional.get();
    }

    @Nullable
    @Override
    public Member getAsMember() {
        return message.getGuild().getMember(getAsUser());
    }

    @NotNull
    @Override
    public User getAsUser() {
        IMentionable mention = getAsMentionable();
        if (mention instanceof User){
            return (User) mention;
        }
        return null;
    }

    @NotNull
    @Override
    public Role getAsRole() {
        IMentionable mention = getAsMentionable();
        if (mention instanceof Role){
            return (Role) mention;
        }
        throw new RuntimeException("");
    }

    @NotNull
    @Override
    public GuildChannel getAsGuildChannel() {
        return message.getTextChannel();
    }

    @Nullable
    @Override
    public MessageChannel getAsMessageChannel() {
        return message.getTextChannel();
    }

    @NotNull
    @Override
    public ChannelType getChannelType() {
        return ChannelType.TEXT;
    }

    public <T> T valid(Object value, T defaultItem){
        if (value == null)
            return defaultItem;
        return (T) value;
    }


    public static List<OptionMapping> of(Message message, Map<OptionData, Object> map){
        List<OptionMapping> mappings = new ArrayList<>();
        for (OptionData next : map.keySet()) {
            if (next.isRequired())
                if (map.get(next) == null)
                    throw new CommandArgumentException("Argumento obrigatório está nulo");

            mappings.add(new OptionMappingParser(message, next, map.get(next)));
        }
        return mappings;
    }
}
