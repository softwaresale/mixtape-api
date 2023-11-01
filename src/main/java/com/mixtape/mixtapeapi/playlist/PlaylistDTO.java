package com.mixtape.mixtapeapi.playlist;

public class PlaylistDTO {

    public static class Create {
        public String name;
        public String description;
        public String coverPicURL;
        public String requestedUserID;

        public Create(String name, String description, String coverPicURL) {
            this.name = name;
            this.description = description;
            this.coverPicURL = coverPicURL;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCoverPicURL() {
            return coverPicURL;
        }

        public void setCoverPicURL(String coverPicURL) {
            this.coverPicURL = coverPicURL;
        }

        public String getRequestedUserID() {
            return requestedUserID;
        }

        public void setRequestedUserID(String requestedUserID) {
            this.requestedUserID = requestedUserID;
        }
    }

    public static class Update {
        public String name;
        public String description;
        public String coverPicURL;

        public Update(String name, String description, String coverPicURL) {
            this.name = name;
            this.description = description;
            this.coverPicURL = coverPicURL;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCoverPicURL() {
            return coverPicURL;
        }

        public void setCoverPicURL(String coverPicURL) {
            this.coverPicURL = coverPicURL;
        }
    }
}
