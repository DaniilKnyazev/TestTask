/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver;

/**
 *
 * @author Rooter-than-root
 */
public class ChatCommand {
    public static final int LOGIN = 0;

    public static final int QUIT = 1;

    public static final int MSG = 2;

    private final int num;

    private ChatCommand(int num) {
        this.num = num;
    }

    public int toInt() {
        return num;
    }

    public static ChatCommand valueOf(String s) {
        s = s.toUpperCase();
        if ("LOGIN".equals(s)) {
            return new ChatCommand(LOGIN);
        }
        if ("QUIT".equals(s)) {
            return new ChatCommand(QUIT);
        }
        if ("MSG".equals(s)) {
            return new ChatCommand(MSG);
        }

        throw new IllegalArgumentException("Unrecognized command: " + s);
    }
}

