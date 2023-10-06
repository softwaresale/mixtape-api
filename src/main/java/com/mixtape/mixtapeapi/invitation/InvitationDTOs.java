package com.mixtape.mixtapeapi.invitation;

public class InvitationDTOs {
    public static class Create {
        private String targetId;
        private String initiatorId;
        private InvitationType invitationType;

        public Create() {
        }

        public Create(String targetId, String initiatorId, InvitationType invitationType) {
            this.targetId = targetId;
            this.initiatorId = initiatorId;
            this.invitationType = invitationType;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public String getInitiatorId() {
            return initiatorId;
        }

        public void setInitiatorId(String initiatorId) {
            this.initiatorId = initiatorId;
        }

        public InvitationType getInvitationType() {
            return invitationType;
        }

        public void setInvitationType(InvitationType invitationType) {
            this.invitationType = invitationType;
        }
    }
}
