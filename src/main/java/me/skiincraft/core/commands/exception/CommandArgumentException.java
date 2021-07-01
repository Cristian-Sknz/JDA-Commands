package me.skiincraft.core.commands.exception;

public class CommandArgumentException extends RuntimeException{

    public CommandArgumentException(String message) {
        super(message);
    }

    public CommandArgumentException(String format, Object... args) {
        super(String.format(format, args));
    }

    public CommandArgumentException(String format, Throwable cause, Object... args) {
        super(String.format(format, args), cause);
    }

    public CommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
