package com.mixtape.mixtapeapi.mixtape;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @ManyToOne
    @JoinColumn(name="reactor_id")
    private Profile reactor;
    private ReactionType reactionType;

    @ManyToOne
    @JoinColumn(name="mixtape_id")
    private Mixtape mixtape;

    public Reaction() {
        this(null, null, ReactionType.LIKE, null);
    }

    public Reaction(Integer id, Profile reactor, ReactionType reactionType, Mixtape mixtape) {
        this.id = id;
        this.reactor = reactor;
        this.reactionType = reactionType;
        this.mixtape = mixtape;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Profile getReactor() {
        return reactor;
    }

    public void setReactor(Profile reactor) {
        this.reactor = reactor;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    @JsonIgnore
    public Mixtape getMixtape() {
        return mixtape;
    }

    @JsonIgnore
    public void setMixtape(Mixtape mixtape) {
        this.mixtape = mixtape;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Reaction{");
        sb.append("id=").append(id);
        sb.append(", reactor=").append(reactor);
        sb.append(", reactionType=").append(reactionType);
        sb.append(", mixtape=").append(mixtape);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reaction reaction = (Reaction) o;
        return Objects.equals(id, reaction.id) && Objects.equals(reactor, reaction.reactor) && reactionType == reaction.reactionType && Objects.equals(mixtape, reaction.mixtape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reactor, reactionType, mixtape);
    }
}
