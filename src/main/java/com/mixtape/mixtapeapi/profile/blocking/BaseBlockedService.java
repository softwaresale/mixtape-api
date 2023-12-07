package com.mixtape.mixtapeapi.profile.blocking;

import com.mixtape.mixtapeapi.profile.Profile;

public abstract class BaseBlockedService {
    protected final BlockedProfileRepository blockedProfileRepository;

    public BaseBlockedService(BlockedProfileRepository blockedProfileRepository) {
        this.blockedProfileRepository = blockedProfileRepository;
    }

    public boolean isBlockedSymmetrical(Profile profile1, Profile profile2) {
        return blockedProfileRepository
                .existsDistinctByBlockerAndBlockeeOrBlockeeAndBlocker(profile1, profile2, profile1, profile2);
    }

    public BlockedProfile save(BlockedProfile profile) {
        return this.blockedProfileRepository.save(profile);
    }
}
