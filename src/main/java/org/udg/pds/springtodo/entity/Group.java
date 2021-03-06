package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


@Entity(name = "usergroup")
public class Group {

    public Group() {
    }

    public Group (String name, String description) {
        this.name = name;
        this.description = description;
    }


    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // Relations

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_owner")
    private User owner;

    @Column(name = "fk_owner", insertable = false, updatable = false)
    private Long ownerId;

    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<User> members = new ArrayList<>();

    // Methods

    public void addUser(User user) {
        members.add(user);
    }

    @JsonIgnore
    public User getOwner() { return owner; }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @JsonView(Views.Private.class)
    public Long getId() {
        return id;
    }

    @JsonView(Views.Private.class)
    public String getName() { return name; }

    @JsonView(Views.Private.class)
    public String getDescription() { return description; }

    public boolean isMember(Long userId) { return members.stream().anyMatch(x -> x.getId().equals(userId)); }

}
