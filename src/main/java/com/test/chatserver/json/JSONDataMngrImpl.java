/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.test.chatserver.db.controllers.MessageJpaController;
import com.test.chatserver.db.controllers.UserJpaController;
import com.test.chatserver.db.controllers.exceptions.IncorrectAuthException;
import com.test.chatserver.db.entities.Message;
import com.test.chatserver.db.entities.User;
import com.test.chatserver.ifaces.DataManager;
import com.test.chatserver.ifaces.Messenger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Persistence;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Rooter-than-root
 */
public class JSONDataMngrImpl implements DataManager{
    Messenger messenger;
    JSONParser jsonParser;
    ObjectMapper mapper;
    JSONObject jsonObj;
    
    public JSONDataMngrImpl(){
            jsonParser = new JSONParser();
            mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
            jsonObj = null;
        }
    
    @Override
    public void setMessenger(Messenger msgr) {
        this.messenger = msgr;
    }

    @Override
    public void receiveMgs(Object msg) {
            
        try {
            jsonObj = (JSONObject)jsonParser.parse( msg.toString());
        } catch (ParseException ex) {
            jsonObj.put("ERROR", ex.getLocalizedMessage());
            messenger.onError("",jsonObj.toJSONString());
            Logger.getLogger(JSONDataMngrImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(jsonObj.containsKey("ADD")){
            onAdd();
        }else  if(jsonObj.containsKey("QUIT")){
            onQuit();
        }else if(jsonObj.containsKey("Message")){
            onMsg();
        }else if(jsonObj.containsKey("HISTORY")){
            onHistoryRequest();
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Метод обработки добавления пользователя">
    private void onAdd(){
        
        User userIn = null;
                UserJpaController usrJpa = new UserJpaController(Persistence.createEntityManagerFactory("ChatServerPU"));
                jsonObj = (JSONObject)jsonObj.get("ADD");
                jsonObj = (JSONObject) jsonObj.get("User");
            try {
                userIn = mapper.readValue(jsonObj.toJSONString(), User.class);
            } catch (JsonProcessingException ex) {
                jsonObj.put("ERROR", ex.getLocalizedMessage());
                messenger.onError("",jsonObj.toJSONString());
                Logger.getLogger(JSONDataMngrImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                usrJpa.create(userIn);
            } catch (Exception ex) {
                jsonObj.put("ERROR", ex.getLocalizedMessage());
                messenger.onError("",jsonObj.toJSONString());
                Logger.getLogger(JSONDataMngrImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
                String s = null;
            try {
                s = mapper.writeValueAsString(userIn);
                //jsonObj.clear();
            } catch (JsonProcessingException ex) {
                jsonObj.put("ERROR", ex.getLocalizedMessage());
                messenger.onError("",jsonObj.toJSONString());
                Logger.getLogger(JSONDataMngrImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
                
                
                //js = (JSONObject)jsonParser.parse((String) jsonObj.get("User"));
                //{"ADD":{"User":{"id":1,"name":"Даниил Сергеевич Князев","pwd":"123","login":"danielly858"}}}
                //userIn = mapper.readValue(js.toJSONString(), User.class);
                //{"ADD":{"User":{"name":"ADM","pwd":"admin","login":"admin"}}}
                jsonObj = new JSONObject();
                jsonObj.put("ADDED", s);
                messenger.onAddUsr(userIn.getLogin(),jsonObj.toJSONString());//asJSON
    
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Метод для авторизации">
    public String onLogin(Object rxMessage) throws IncorrectAuthException{
        try {
            jsonObj = (JSONObject) jsonParser.parse(rxMessage.toString());
        } catch (ParseException ex) {
            Logger.getLogger(JSONDataMngrImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!jsonObj.containsKey("LOGIN")){
            jsonObj.clear();
            jsonObj.put("ERROR", "Invalid JSON message");
            throw new IncorrectAuthException(jsonObj.toJSONString());
        }
                jsonObj = (JSONObject) jsonObj.get("LOGIN");
                jsonObj = (JSONObject) jsonObj.get("User");
                User userIn = null;
                try {
                    userIn = mapper.readValue(jsonObj.toJSONString(), User.class);
                } catch (JsonProcessingException ex) {
                    jsonObj.clear();
                    jsonObj.put("ERROR", "Неверная комбинация логин/пароль");
                    messenger.onError("", jsonObj.toJSONString());
                    Logger.getLogger(JSONDataMngrImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
                    UserJpaController usrJpa = new UserJpaController(Persistence.createEntityManagerFactory("ChatServerPU"));
                    userIn = usrJpa.findUser(userIn.getLogin(), userIn.getPwd());
                if(userIn==null){
                    jsonObj.clear();
                    jsonObj.put("ERROR", "Неверная комбинация логин/пароль");
                    throw new IncorrectAuthException(jsonObj.toJSONString());
                }else {
                    jsonObj.clear();
                    try {
                        jsonObj.put("ONLINE", mapper.writeValueAsString(userIn));
                        messenger.onlineUsr(userIn.getLogin(),jsonObj.toJSONString());
                    } catch (JsonProcessingException ex) {
                        Logger.getLogger(JSONDataMngrImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
//                if(userSessions.containsKey(userIn)){
//                    session.write(new JSONObject().put("ERROR", "Пользователь уже установил соединение!"));
//                }else{
//                    userSessions.put(userIn.getLogin(), session);
//                    session.setAttribute("user", userIn.getLogin());
//                    MdcInjectionFilter.setProperty(session, "user", userIn.getLogin());
//                    session.write(mapper.writeValueAsString(usrJpa.findUserEntities()));//test ONLINEList
//                    broadcast(new JSONObject().put("ONLINE", userIn.getLogin()).toString());
//                }
        return userIn.getLogin();
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Метод обработки сообщения">
    private void onMsg(){
            jsonObj = (JSONObject) jsonObj.get("Message");
            Message rx = null;
            String onMsg="";
        try {
            rx = mapper.readValue(jsonObj.toJSONString(), Message.class);
            MessageJpaController msgMng = new MessageJpaController(Persistence.createEntityManagerFactory("ChatServerPU"));
            msgMng.create(rx);
            onMsg = mapper.writeValueAsString(rx);
        } catch (Exception ex) {
            Logger.getLogger(JSONDataMngrImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
            messenger.sendTo(rx.getUserTo().getLogin(), onMsg);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Методы обработки команды выхода из чата">
    private void onQuit(){
        jsonObj = (JSONObject) jsonObj.get("QUIT");
        jsonObj = (JSONObject) jsonObj.get("User");
        String login = (String) jsonObj.get("login");
        jsonObj.clear();
        jsonObj.put("OFFLINE", login);
        messenger.offlineUsr(login, jsonObj.toJSONString());
    }
    public String onQuit(String login){
        jsonObj = new JSONObject();
        jsonObj.put("OFFLINE", login);
        return jsonObj.toJSONString();
    }
//</editor-fold>

    private void onHistoryRequest() {
        jsonObj = (JSONObject) jsonObj.get("QUIT");
        jsonObj = (JSONObject) jsonObj.get("User");
        
        
    }
}
