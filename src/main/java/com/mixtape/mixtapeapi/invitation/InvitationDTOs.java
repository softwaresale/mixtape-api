package com.mixtape.mixtapeapi.invitation;

public class InvitationDTOs {
    public static class Create {
        private String targetId;
        private InvitationType invitationType;

        public Create() {
        }

        public Create(String targetId, String initiatorId, InvitationType invitationType) {
            this.targetId = targetId;
            this.invitationType = invitationType;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public InvitationType getInvitationType() {
            return invitationType;
        }

        public void setInvitationType(InvitationType invitationType) {
            this.invitationType = invitationType;
        }
    }
}
