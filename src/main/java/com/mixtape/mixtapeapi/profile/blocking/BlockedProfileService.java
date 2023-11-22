package com.mixtape.mixtapeapi.profile.blocking;

import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.stereotype.Service;

@Service
public class BlockedProfileService extends BaseBlockedService {

    private final FriendshipService friendshipService;

    public BlockedProfileService(BlockedProfileRepository repository, FriendshipService friendshipService) {
        super(repository);
        this.friendshipService = friendshipService;
    }

    public boolean blockProfile(Profile blocker, Profile blockee) {
        if (isBlockedSymmetrical(blocker, blockee)) {
            return false;
        }

        // save the record
        save(new BlockedProfile(null, blocker, blockee));

        // remove friendship, which will cascade delete any records created between these two users
        friendshipService.removeFriendshipByFriend(blocker, blockee);

        return true;
    }

}
