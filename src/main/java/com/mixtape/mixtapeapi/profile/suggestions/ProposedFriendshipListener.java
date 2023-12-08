package com.mixtape.mixtapeapi.profile.suggestions;

import com.mixtape.mixtapeapi.events.FriendshipProposedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ProposedFriendshipListener implements ApplicationListener<FriendshipProposedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ProposedFriendshipListener.class);

    private final SuggestedProfileService suggestedProfileService;

    public ProposedFriendshipListener(SuggestedProfileService suggestedProfileService) {
        this.suggestedProfileService = suggestedProfileService;
    }

    @Override
    public void onApplicationEvent(FriendshipProposedEvent event) {
        logger.info("Friendship proposed event raised: {}", event);
        suggestedProfileService.deleteSuggestionsBetweenUsers(event.getFriendship().getInitiator(), event.getRequestedProfile());
    }
}
