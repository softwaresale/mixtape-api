package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.profile.Profile;
import jakarta.persistence.*;

@Entity
public class SuggestedProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "suggested_for_id")
    private Profile suggestionFor;

    @ManyToOne
    @JoinColumn(name = "suggested_id")
    private Profile suggested;

    public SuggestedProfile(String id, Profile suggestionFor, Profile suggested) {
        this.id = id;
        this.suggestionFor = suggestionFor;
        this.suggested = suggested;
    }

    public SuggestedProfile() {
        this(null, null, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Profile getSuggestionFor() {
        return suggestionFor;
    }

    public void setSuggestionFor(Profile suggestionFor) {
        this.suggestionFor = suggestionFor;
    }

    public Profile getSuggested() {
        return suggested;
    }

    public void setSuggested(Profile suggested) {
        this.suggested = suggested;
    }
}
