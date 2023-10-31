package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.profile.Profile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MixtapeTest {

    @Test
    void addReaction_insertsANewReaction() {
        Mixtape mixtape = new Mixtape();
        Profile reactor = new Profile();
        reactor.setId("id");
        Profile newReactor = new Profile();
        newReactor.setId("id2");
        Reaction existingReaction = new Reaction(0, reactor, ReactionType.LIKE, mixtape);
        mixtape.setReactions(new ArrayList<>(List.of(existingReaction)));

        Reaction newReaction = new Reaction(1, newReactor, ReactionType.LIKE, mixtape);

        mixtape.addReaction(newReaction);

        assertThat(mixtape.getReactions()).contains(existingReaction, newReaction);
    }

    @Test
    void addReaction_overwritesExistingWithSameId() {
        Mixtape mixtape = new Mixtape();
        Profile reactor = new Profile();
        reactor.setId("id");
        Reaction existingReaction = new Reaction(0, reactor, ReactionType.LIKE, mixtape);
        mixtape.setReactions(new ArrayList<>(List.of(existingReaction)));

        Reaction newReaction = new Reaction(0, reactor, ReactionType.DISLIKE, mixtape);

        mixtape.addReaction(newReaction);

        assertThat(mixtape.getReactions()).containsExactly(newReaction);
    }
}
