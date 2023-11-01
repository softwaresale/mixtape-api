package com.mixtape.mixtapeapi.mixtape;

import java.util.List;

public class MixtapeDTO {
    public static class Create {
        public String name;
        public String description;
        public List<String> songIDs;

        public Create(String name, String description, List<String> songIDs) {
            this.name = name;
            this.description = description;
            this.songIDs = songIDs;
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

        public List<String> getSongIDs() {
            return songIDs;
        }

        public void setSongIDs(List<String> songIDs) {
            this.songIDs = songIDs;
        }
    }
}
