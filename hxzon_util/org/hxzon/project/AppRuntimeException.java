package org.hxzon.project;

public class AppRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 950013706503904204L;

    protected AppRuntimeException(String message) {
        super(message);
    }

    protected AppRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    protected AppRuntimeException(Throwable cause) {
        super(cause);
    }

    public static AppRuntimeException rethrow(String message, Throwable cause) {
        if (cause instanceof AppRuntimeException) {
            message += (":" + cause.getMessage());
        }
        return new OldRuntimeException(message, cause);
    }

    public static AppRuntimeException error(String message) {
        return new NewRuntimeException(message);
    }

    public static class OldRuntimeException extends AppRuntimeException {
        private static final long serialVersionUID = -2403871268694203255L;

        protected OldRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }

        protected OldRuntimeException(Throwable cause) {
            super(cause);
        }
    }

    public static class NewRuntimeException extends AppRuntimeException {
        private static final long serialVersionUID = 2010247104673727882L;

        protected NewRuntimeException(String message) {
            super(message);
        }
    }
}
