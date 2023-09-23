package com.mixtape.mixtapeapi.invitation;

import org.springframework.stereotype.Service;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;

    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public Invitation findInvitation(String id) {
        return invitationRepository.getReferenceById(id);
    }

    public Invitation save(Invitation newInvitation) {
        return invitationRepository.save(newInvitation);
    }
}
