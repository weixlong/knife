package com.wxl.mvp.event;

/**
 * create file time : 2020/12/12
 * create user : wxl
 * subscribe :
 */
public class SnackEvent {
    private boolean isLong;
    private String message;

    public SnackEvent(boolean isLong, String message) {
        this.isLong = isLong;
        this.message = message;
    }

    public SnackEvent(String message) {
        this.message = message;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean aLong) {
        isLong = aLong;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
