/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver.db.controllers.exceptions;

/**
 *
 * @author Rooter-than-root
 */
public class IncorrectAuthException extends Exception{
    
        private String login;
        private String explanation;
    
    public IncorrectAuthException(String explanation){
        super(explanation);
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
