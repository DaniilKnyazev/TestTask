/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver.ifaces;

/**
 *
 * @author Rooter-than-root
 */
public interface Messenger {
    void offlineUsr(String login, String msg);
    void onlineUsr(String login, String msg);
    void sendTo(String toLogin, String msg);
    void onAddUsr(String login, String msg);
    void onError(String login, String msg);
}
