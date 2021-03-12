/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.test.chatserver.db.entities.Message;
import com.test.chatserver.db.entities.User;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.stream.JsonParser;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.json.simple.JSONObject;


/**
 *
 * @author Rooter-than-root
 */
public class DBUtils {
//    public URL globalUrl = null;
//{
//    globalUrl=getClass().getResource("/resources/testchat.db");
//    Connection c;
//    try {
//         c = DriverManager.getConnection("jdbc:sqlite:"+globalUrl);
//         Statement state = c.createStatement();
//         state.execute("select * from user");
//         ResultSet rs = state.getResultSet();
//         for(int i = 1;i<=rs.getMetaData().getColumnCount();i++){
//            var v = rs.getObject(i);
//         };
//    } catch (SQLException ex) {
//        System.out.println(ex.getLocalizedMessage());
//    }
//
//}
    public static void test(User usr){
       EntityManager entMan = Persistence.createEntityManagerFactory("ChatServerPU").createEntityManager();
       List list = entMan.createNamedQuery("User.findAll").getResultList();
       ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        String result = "";
        try {
            result = mapper.writeValueAsString((User)list.get(1));
           result = mapper.writeValueAsString(list.toArray(new User[0]));
        } catch (JsonProcessingException ex) {
            System.out.print(ex.getLocalizedMessage());
        }
       
//        Query query = entMan.
//                createQuery("select m from Message m where m.userTo = :usrTo and m.userFrom = :usr");
//        query.setParameter("usrTo",list.get(0));
//        query.setParameter("usr",list.get(1));
//        list = query.getResultList();
    }
}
