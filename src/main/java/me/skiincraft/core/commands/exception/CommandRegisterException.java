package me.skiincraft.core.commands.exception;

public class CommandRegisterException extends RuntimeException {

    public CommandRegisterException(String message) {
        super(message);
    }

    public CommandRegisterException(String format, Object... args) {
        super(String.format(format, args));
    }

    public CommandRegisterException(String format, Throwable cause, Object... args) {
        super(String.format(format, args), cause);
    }

    public CommandRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
