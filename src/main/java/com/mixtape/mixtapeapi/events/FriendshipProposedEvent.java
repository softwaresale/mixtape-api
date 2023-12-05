package com.mixtape.mixtapeapi.events;

import com.mixtape.mixtapeapi.friendship.Friendship;
import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.context.ApplicationEvent;

public class FriendshipProposedEvent extends ApplicationEvent {
    private final Friendship friendship;
    private final Profile requestedProfile;

    public FriendshipProposedEvent(Object source, Friendship friendship, Profile requestedProfile) {
        super(source);
        this.friendship = friendship;
        this.requestedProfile = requestedProfile;
    }

    public Friendship getFriendship() {
        return friendship;
    }

    public Profile getRequestedProfile() {
        return requestedProfile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FriendshipProposedEvent{");
        sb.append("friendship=").append(friendship);
        sb.append(", requestedProfile=").append(requestedProfile);
        sb.append('}');
        return sb.toString();
    }
}
