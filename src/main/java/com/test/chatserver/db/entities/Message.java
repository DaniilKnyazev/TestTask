/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
/**
 *
 * @author Rooter-than-root
 */
@JsonRootName(value="Message")
@Entity
@Table(name = "message")
@NamedQueries({
    @NamedQuery(name = "Message.findAll", query = "SELECT m FROM Message m"),
    @NamedQuery(name = "Message.findById", query = "SELECT m FROM Message m WHERE m.id = :id"),
    @NamedQuery(name = "Message.findByTxt", query = "SELECT m FROM Message m WHERE m.txt = :txt"),
    @NamedQuery(name = "Message.findByAt", query = "SELECT m FROM Message m WHERE m.at = :at")})
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @Column(name = "id")
    @Id
    @JsonIgnore
    private Integer id;
    @Basic(optional = false)
    @Column(name = "txt")
    private String txt;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "at_time")
    private String at;
    
    
    @JoinColumns({
        @JoinColumn(name = "to_usr", referencedColumnName = "id"),
        })
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User userTo;
    @JoinColumns({
        @JoinColumn(name = "from_usr", referencedColumnName = "id")})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User userFrom;

    public Message() {
    }

    

    public Message(String txt) {
        this.id = id;
        this.txt = txt;
    }

    

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

   

    public User getUserTo() {
        return userTo;
    }

    public void setUser(User userTo) {
        this.userTo = userTo;
    }

    public User getUserFrom(){
        return userFrom;
    }
    
    public void setUserFrom(User userFrom){
        this.userFrom = userFrom;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this != null ? this.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Message)) {
            return false;
        }
        Message other = (Message) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.test.chatserver.db.entities.Message[ ID=" + id + " ]";
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }
    
}
