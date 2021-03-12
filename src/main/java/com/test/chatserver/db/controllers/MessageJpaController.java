/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver.db.controllers;

import com.test.chatserver.db.controllers.exceptions.NonexistentEntityException;
import com.test.chatserver.db.controllers.exceptions.PreexistingEntityException;
import com.test.chatserver.db.entities.Message;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.test.chatserver.db.entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Rooter-than-root
 */
public class MessageJpaController implements Serializable {

    public MessageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Message message) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User userTo = message.getUserTo();
            if (userTo != null) {
                userTo = em.getReference(userTo.getClass(), userTo.getId());
                message.setUserTo(userTo);
            }
            User userFrom = message.getUserFrom();
            if (userFrom != null) {
                userFrom = em.getReference(userFrom.getClass(), userFrom.getId());
                message.setUserFrom(userFrom);
            }
            em.persist(message);
            if (userTo != null) {
                userTo.getMessageListFrom().add(message);
                userTo = em.merge(userTo);
            }
            if (userFrom != null) {
                userFrom.getMessageListFrom().add(message);
                userFrom = em.merge(userFrom);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMessage(message.getId()) != null) {
                throw new PreexistingEntityException("Message " + message + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Message message) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Message persistentMessage = em.find(Message.class, message.getId());
            User userToOld = persistentMessage.getUserTo();
            User userToNew = message.getUserTo();
            User userFromOld = persistentMessage.getUserFrom();
            User userFromNew = message.getUserFrom();
            if (userToNew != null) {
                userToNew = em.getReference(userToNew.getClass(), userToNew.getId());
                message.setUserTo(userToNew);
            }
            if (userFromNew != null) {
                userFromNew = em.getReference(userFromNew.getClass(), userFromNew.getId());
                message.setUserFrom(userFromNew);
            }
            message = em.merge(message);
            if (userToOld != null && !userToOld.equals(userToNew)) {
                userToOld.getMessageListFrom().remove(message);
                userToOld = em.merge(userToOld);
            }
            if (userToNew != null && !userToNew.equals(userToOld)) {
                userToNew.getMessageListFrom().add(message);
                userToNew = em.merge(userToNew);
            }
            if (userFromOld != null && !userFromOld.equals(userFromNew)) {
                userFromOld.getMessageListFrom().remove(message);
                userFromOld = em.merge(userFromOld);
            }
            if (userFromNew != null && !userFromNew.equals(userFromOld)) {
                userFromNew.getMessageListFrom().add(message);
                userFromNew = em.merge(userFromNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = message.getId();
                if (findMessage(id) == null) {
                    throw new NonexistentEntityException("The message with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Message message;
            try {
                message = em.getReference(Message.class, id);
                message.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The message with id " + id + " no longer exists.", enfe);
            }
            User userTo = message.getUserTo();
            if (userTo != null) {
                userTo.getMessageListFrom().remove(message);
                userTo = em.merge(userTo);
            }
            User userFrom = message.getUserFrom();
            if (userFrom != null) {
                userFrom.getMessageListFrom().remove(message);
                userFrom = em.merge(userFrom);
            }
            em.remove(message);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Message> findMessageEntities() {
        return findMessageEntities(true, -1, -1);
    }

    public List<Message> findMessageEntities(int maxResults, int firstResult) {
        return findMessageEntities(false, maxResults, firstResult);
    }

    private List<Message> findMessageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Message.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Message findMessage(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Message.class, id);
        } finally {
            em.close();
        }
    }

    public int getMessageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Message> rt = cq.from(Message.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Message> getMsgListByUsers(User from, User to){
        List result = null;
        Query query = getEntityManager().
                createQuery("select m from Message m where m.userTo = :usrTo and m.userFrom = :usr");
        query.setParameter("usrTo",to);
        query.setParameter("usr",from);
        result = query.getResultList();
        return result;
    }
    public List<Message> getMsgListByLogin(String fromLogin, String toLogin){
        List result = null;
        Query query = getEntityManager().
                createQuery("select m from Message m where m.userTo.login = :usrTo and m.userFrom.login = :usr");
        query.setParameter("usrTo",toLogin);
        query.setParameter("usr",fromLogin);
        result = query.getResultList();
        return result;
    }
    public List<Message> getMsgListByUsers(String from, String to){
        List result = null;
        Query query = getEntityManager().
                createQuery("select m from Message m where m.userTo.login = :usrTo and m.userFrom.login = :usr order by m.at");
        query.setParameter("usrTo",to);
        query.setParameter("usr",from);
        result = query.getResultList();
        return result;
    }
    public static MessageJpaController getInstance() {
        return MessageJpaControllerHolder.INSTANCE;
    }
    
    private static class MessageJpaControllerHolder {

        private static final MessageJpaController INSTANCE = new MessageJpaController(Persistence.createEntityManagerFactory("ChatServerPU"));
    }
}
