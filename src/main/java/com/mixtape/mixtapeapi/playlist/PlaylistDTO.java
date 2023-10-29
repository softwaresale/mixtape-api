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
    }
}
