package com.mixtape.mixtapeapi.invitation;

import com.mixtape.mixtapeapi.friendship.FriendshipService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvitationServiceTest {
    @Mock
    InvitationRepository mockInvitationRepository;
    @Mock
    FriendshipService mockFriendshipService;

    @Mock
    ProfileService mockProfileService;
    InvitationService invitationService;

    @BeforeEach
    void beforeEach() {
        invitationService = new InvitationService(mockInvitationRepository, mockFriendshipService, mockProfileService);
    }

    @Test
    void createNewInvitation_profilesExists() {
        String initiatorId = "initId";
        String targetId = "tarId";
        InvitationType type = InvitationType.PLAYLIST;
        InvitationDTOs.Create dtoInvitation = new InvitationDTOs.Create(targetId, initiatorId, type);
        Profile initiator = new Profile();
        Profile target = new Profile();
        Invitation invitation = new Invitation();

        // when(mockProfileService.findProfile(initiatorId)).thenReturn(Optional.of(initiator));
        when(mockProfileService.findProfile(targetId)).thenReturn(Optional.of(target));
        when(mockInvitationRepository.save(any())).thenReturn(invitation);

        Invitation retVal = invitationService.createNewInvitation(initiator, dtoInvitation);

        assertThat(retVal).isEqualTo(invitation);

        verify(mockProfileService).findProfile(targetId);
        verify(mockInvitationRepository).save((any()));
    }

}
