package com.portfolio.demo.model;

import com.portfolio.demo.model.myenums.ThingType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "saved_things")
public class SavedThing {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "thing_id")
    private Long thingId;

    @Column(name = "thing_type")
    @Enumerated(EnumType.STRING)
    private ThingType thingType;


    public SavedThing() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getThingId() {
        return thingId;
    }

    public void setThingId(Long thingId) {
        this.thingId = thingId;
    }

    public ThingType getThingType() {
        return thingType;
    }

    public void setThingType(ThingType thingType) {
        this.thingType = thingType;
    }
    
}
