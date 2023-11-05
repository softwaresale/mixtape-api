package com.mixtape.mixtapeapi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
