package org.hxzon.project;

public class NotExistException extends AppRuntimeException {
    private static final long serialVersionUID = -1384810439112385034L;

    public NotExistException(String message) {
        super(message);
    }

}
