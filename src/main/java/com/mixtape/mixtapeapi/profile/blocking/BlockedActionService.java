package com.mixtape.mixtapeapi.profile.blocking;

import com.mixtape.mixtapeapi.profile.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockedActionService extends BaseBlockedService {

    public BlockedActionService(BlockedProfileRepository blockedProfileRepository) {
        super(blockedProfileRepository);
    }

    public List<Profile> filterProfilesByBlocked(Profile currentProfile, List<Profile> profiles) {
        return profiles.stream()
                .filter(profile -> !isBlockedSymmetrical(currentProfile, profile))
                .toList();
    }
}
