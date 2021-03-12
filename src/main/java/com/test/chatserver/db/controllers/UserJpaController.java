/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver.db.controllers;

import com.test.chatserver.db.controllers.exceptions.IllegalOrphanException;
import com.test.chatserver.db.controllers.exceptions.NonexistentEntityException;
import com.test.chatserver.db.controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.test.chatserver.db.entities.Message;
import com.test.chatserver.db.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Rooter-than-root
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, Exception {
        if (user.getMessageListFrom() == null) {
            user.setMessageListFrom(new ArrayList<Message>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Message> attachedMessageListFrom = new ArrayList<Message>();
            for (Message messageListFromMessageToAttach : user.getMessageListFrom()) {
                messageListFromMessageToAttach = em.getReference(messageListFromMessageToAttach.getClass(), messageListFromMessageToAttach.getId());
                attachedMessageListFrom.add(messageListFromMessageToAttach);
            }
            user.setMessageListFrom(attachedMessageListFrom);
            em.persist(user);
            for (Message messageListFromMessage : user.getMessageListFrom()) {
                User oldUserFromOfMessageListFromMessage = messageListFromMessage.getUserFrom();
                messageListFromMessage.setUserFrom(user);
                messageListFromMessage = em.merge(messageListFromMessage);
                if (oldUserFromOfMessageListFromMessage != null) {
                    oldUserFromOfMessageListFromMessage.getMessageListFrom().remove(messageListFromMessage);
                    oldUserFromOfMessageListFromMessage = em.merge(oldUserFromOfMessageListFromMessage);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getId()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getId());
            List<Message> messageListFromOld = persistentUser.getMessageListFrom();
            List<Message> messageListFromNew = user.getMessageListFrom();
            List<String> illegalOrphanMessages = null;
            for (Message messageListFromOldMessage : messageListFromOld) {
                if (!messageListFromNew.contains(messageListFromOldMessage)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Message " + messageListFromOldMessage + " since its userFrom field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Message> attachedMessageListFromNew = new ArrayList<Message>();
            for (Message messageListFromNewMessageToAttach : messageListFromNew) {
                messageListFromNewMessageToAttach = em.getReference(messageListFromNewMessageToAttach.getClass(), messageListFromNewMessageToAttach.getId());
                attachedMessageListFromNew.add(messageListFromNewMessageToAttach);
            }
            messageListFromNew = attachedMessageListFromNew;
            user.setMessageListFrom(messageListFromNew);
            user = em.merge(user);
            for (Message messageListFromNewMessage : messageListFromNew) {
                if (!messageListFromOld.contains(messageListFromNewMessage)) {
                    User oldUserFromOfMessageListFromNewMessage = messageListFromNewMessage.getUserFrom();
                    messageListFromNewMessage.setUserFrom(user);
                    messageListFromNewMessage = em.merge(messageListFromNewMessage);
                    if (oldUserFromOfMessageListFromNewMessage != null && !oldUserFromOfMessageListFromNewMessage.equals(user)) {
                        oldUserFromOfMessageListFromNewMessage.getMessageListFrom().remove(messageListFromNewMessage);
                        oldUserFromOfMessageListFromNewMessage = em.merge(oldUserFromOfMessageListFromNewMessage);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Message> messageListFromOrphanCheck = user.getMessageListFrom();
            for (Message messageListFromOrphanCheckMessage : messageListFromOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Message " + messageListFromOrphanCheckMessage + " in its messageListFrom field has a non-nullable userFrom field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public User findUser(String login, String pwd) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("SELECT u FROM User u WHERE u.login = :login and u.pwd = :pwd",User.class);
            query.setParameter("login",login);
            query.setParameter("pwd",pwd);
            return (User) query.getSingleResult();
        } finally {
            em.close();
        }
    }
    public User findUserByLogin(String login){
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("SELECT u FROM User u WHERE u.login = :login",User.class);
            query.setParameter("login",login);
            return (User) query.getSingleResult();
        } finally {
            em.close();
        }
    }
    public static UserJpaController getInstance() {
        return UserJpaControllerHolder.INSTANCE;
    }
    
    private static class UserJpaControllerHolder {

        private static final UserJpaController INSTANCE = new UserJpaController(Persistence.createEntityManagerFactory("ChatServerPU"));
    }
}
