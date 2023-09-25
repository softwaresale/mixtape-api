package com.mixtape.mixtapeapi.invitation;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;

    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public Optional<Invitation> findInvitation(String id) {
        return invitationRepository.findById(id);
    }

    public Invitation save(Invitation newInvitation) {
        return invitationRepository.save(newInvitation);
    }
}
