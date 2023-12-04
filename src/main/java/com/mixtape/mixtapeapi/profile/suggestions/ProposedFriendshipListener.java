package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.events.FriendshipProposedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ProposedFriendshipListener implements ApplicationListener<FriendshipProposedEvent> {

    private final SuggestedProfileService suggestedProfileService;

    public ProposedFriendshipListener(SuggestedProfileService suggestedProfileService) {
        this.suggestedProfileService = suggestedProfileService;
    }

    @Override
    public void onApplicationEvent(FriendshipProposedEvent event) {
        suggestedProfileService.deleteSuggestionsBetweenUsers(event.getFriendship().getInitiator(), event.getRequestedProfile());
    }
}
