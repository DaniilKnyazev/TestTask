/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver.db.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Rooter-than-root
 */
@JsonRootName(value="User")
@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name"),
    @NamedQuery(name = "User.findByPwd", query = "SELECT u FROM User u WHERE u.pwd = :pwd"),
    @NamedQuery(name = "User.findByLogin", query = "SELECT u FROM User u WHERE u.login = :login")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
   
    @Basic(optional = false)
    @Column(name = "id")
    @JsonIgnore
    @Id
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    @Basic(optional = false)
    @Column(name = "pwd")
    private String pwd;
    @Basic(optional = false)
    @Column(name = "login")
    private String login;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userTo", fetch = FetchType.EAGER)
    private List<Message> messageListTo;
    @JsonIgnore
     @OneToMany(cascade = CascadeType.ALL, mappedBy = "userFrom", fetch = FetchType.EAGER)
    private List<Message> messageListFrom;

    public List<Message> getMessageListFrom() {
        return messageListFrom;
    }

    public void setMessageListFrom(List<Message> messageListFrom) {
        this.messageListFrom = messageListFrom;
    }

    public User() {
    }

    

    public User(Integer id, String name, String pwd, String login) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.login = login;
    }
    public User( String name, String pwd, String login) {
        this.name = name;
        this.pwd = pwd;
        this.login = login;
    }

    

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //@JsonGetter("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //@JsonGetter("pwd")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    @JsonGetter("login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    @JsonIgnore
    public List<Message> getMessageList() {
        return messageListTo;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageListTo = messageList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.test.chatserver.db.entities.User[ userPK=" + id + " ]";
    }
    
}
