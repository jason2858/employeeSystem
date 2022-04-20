package com.yesee.gov.website.exception;

public class AccountingException extends Exception {

    private String message;

    public AccountingException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}