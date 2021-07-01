package me.skiincraft.core.commands.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandParameterInjector implements Cloneable {

    List<Object> instances = new ArrayList<>();

    public CommandParameterInjector addBean(Object object){
        if (object == Class.class){
            throw new UnsupportedOperationException("Não é possível adicionar classes como parâmetros");
        }
        instances.add(object);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T inject(Class<T> type){
        return (T) instances.stream().filter(Objects::nonNull).filter(type::isInstance).findFirst()
                .orElse(null);
    }
}
