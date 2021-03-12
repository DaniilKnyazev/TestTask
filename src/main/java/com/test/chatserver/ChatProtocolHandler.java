
package com.test.chatserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.chatserver.db.controllers.exceptions.IncorrectAuthException;
import com.test.chatserver.ifaces.DataManager;
import com.test.chatserver.ifaces.Messenger;
import com.test.chatserver.json.JSONDataMngrImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChatProtocolHandler extends IoHandlerAdapter implements Messenger{
    private final static Logger LOGGER = LoggerFactory.getLogger(ChatProtocolHandler.class);

    private final Set<IoSession> sessions = Collections
            .synchronizedSet(new HashSet<IoSession>());

    private final Set<String> users = Collections
            .synchronizedSet(new HashSet<String>());
    private final Map<String, IoSession> userSessions = 
            Collections.synchronizedMap(new HashMap());
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        LOGGER.warn("Unexpected exception.", cause);
        // Close connection when unexpected exception is caught.
        session.close(true);
    }

    @Override
    public void messageSent(IoSession session, Object message) {
        System.out.println( message );
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        Logger log = LoggerFactory.getLogger(ChatProtocolHandler.class);
        log.info("received: " + message);
        JSONDataMngrImpl dataManager = new JSONDataMngrImpl();
        dataManager.setMessenger(this);
        dataManager.receiveMgs(message);
            if(!sessions.contains(session)){
                sessions.add(session);
                String login = null;
            try {
                login = dataManager.onLogin(message);
                session.setAttribute("user",login);
                users.add(login);
            } catch (IncorrectAuthException ex) {
                java.util.logging.Logger.getLogger(ChatProtocolHandler.class.getName()).log(Level.SEVERE, null, ex);
                sessions.remove(session);
            }
                
            }
        /*
        
                //js = (JSONObject)jsonParser.parse((String) jsonObj.get("User"));
                //{"ADD":{"User":{"id":1,"name":"Даниил Сергеевич Князев","pwd":"123","login":"danielly858"}}}
            {"LOGIN":{"User":{"name":"Даниил Сергеевич Князев","pwd":"123","login":"danielly858"}}}
                //userIn = mapper.readValue(js.toJSONString(), User.class);
                //{"ADD":{"User":{"name":"ADM","pwd":"admin","login":"qwerty"}}}
               
        */
    }

    public void adresat(String message) {
        String[] adresat = message.split(": ",2);
        adresat = adresat[1].split(", ",2);
        synchronized (userSessions) {
            IoSession session = userSessions.get(adresat[0]);
                if (session.isConnected()) {
                    session.write(message);
                }
            
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        JSONDataMngrImpl json = new JSONDataMngrImpl();
        String user = (String) session.getAttribute("user");
        
        users.remove(user);
        sessions.remove(session);
        broadcast(json.onQuit(user));
    }
    
    public void broadcast(String message) {
        synchronized (sessions) {
            for (IoSession session : sessions) {
                if (session.isConnected()) {
                    session.write(message);
                }
            }
        }
    }

    public boolean isChatUser(String name) {
        return users.contains(name);
    }

    public int getNumberOfUsers() {
        return users.size();
    }

    public void kick(String name) {
        synchronized (sessions) {
            for (IoSession session : sessions) {
                if (name.equals(session.getAttribute("user"))) {
                    session.close(true);
                    break;
                }
            }
        }
    }

    @Override
    public void offlineUsr(String login, String msg) {
                IoSession session =  userSessions.get(login);
                session.write("QUIT OK");
                session.close(true);
                userSessions.remove(login);
                broadcast(msg);
                //new JSONObject().put("OFFLINE", jsonObj.get("QUIT")).toString());
    }


    @Override
    public void sendTo(String toLogin, String msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onlineUsr(String login, String msg) {
        broadcast(msg);
    }

    @Override
    public void onAddUsr(String login, String msg) {
        broadcast(msg);
    }

    @Override
    public void onError(String login, String msg) {
        
    }

    

    
}