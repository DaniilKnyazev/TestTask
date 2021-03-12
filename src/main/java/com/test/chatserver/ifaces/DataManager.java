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
public interface DataManager {
    void setMessenger(Messenger msgr);
    void receiveMgs(Object msg);
}
