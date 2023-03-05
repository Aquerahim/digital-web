package com.phoenixacces.apps.exceptions;

public class MailerException extends Exception {
    public MailerException(String message) {
        super(message);
    }

    public MailerException(String message, Throwable cause) {
        super(message, cause);
    }
}
