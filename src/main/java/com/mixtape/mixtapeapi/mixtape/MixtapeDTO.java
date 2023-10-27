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
    }
}
