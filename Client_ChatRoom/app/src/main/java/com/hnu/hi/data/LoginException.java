package com.hnu.hi.data;

public class LoginException extends Exception {
    private String exc;//出错原因
    public LoginException(String exc){
        this.exc = exc;
    }

    public String getExc() {
        return exc;
    }
}
