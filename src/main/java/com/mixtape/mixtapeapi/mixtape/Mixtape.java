package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Mixtape {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String playlistId;
    private String name;
    private LocalDateTime createdAt;
    private String description;
    private Long durationMS;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Profile creator;

    @ElementCollection
    private List<String> songIDs;

    @Transient
    private List<TrackInfo> songs;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Reaction> reactions;

    public Mixtape() {
        this("", "", "", LocalDateTime.now(), "", 0L, null, new ArrayList<>(), new ArrayList<>());
    }

    public Mixtape(MixtapeDTO.Create createDTO) {
        this(null, null, createDTO.name, LocalDateTime.now(), createDTO.description, 0L, null, createDTO.songIDs, new ArrayList<>());
    }

    public Mixtape(String id, String playlistId, String name, LocalDateTime createdAt, String description, Long durationMS, Profile creator, List<String> songIDs, List<Reaction> reactions) {
        this.id = id;
        this.playlistId = playlistId;
        this.name = name;
        this.createdAt = createdAt;
        this.description = description;
        this.durationMS = durationMS;
        this.creator = creator;
        this.songIDs = songIDs;
        this.reactions = reactions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Profile getCreator() {
        return creator;
    }

    public void setCreator(Profile creator) {
        this.creator = creator;
    }

    public List<String> getSongIDs() {
        return songIDs;
    }

    public void setSongIDs(List<String> songIDs) {
        this.songIDs = songIDs;
    }

    public void addSongID(String songID) {
        this.songIDs.add(songID);
    }

    public List<TrackInfo> getSongs() {
        return songs;
    }

    public void setSongs(List<TrackInfo> songs) {
        this.songs = songs;
    }

    public Long getDurationMS() {
        return durationMS;
    }

    public void setDurationMS(Long durationMS) {
        this.durationMS = durationMS;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public void addReaction(Reaction reaction) {
        Optional<Reaction> existingReaction = this
                .reactions
                .stream()
                .filter(existing -> existing.getId().equals(reaction.getId()))
                .findFirst();

        existingReaction.ifPresent((existing) -> {
            this.reactions.remove(existing);
        });

        this.reactions.add(reaction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mixtape mixtape = (Mixtape) o;
        return Objects.equals(id, mixtape.id) && Objects.equals(playlistId, mixtape.playlistId) && Objects.equals(name, mixtape.name) && Objects.equals(createdAt, mixtape.createdAt) && Objects.equals(description, mixtape.description) && Objects.equals(durationMS, mixtape.durationMS) && Objects.equals(creator, mixtape.creator) && Objects.equals(songIDs, mixtape.songIDs) && Objects.equals(songs, mixtape.songs) && Objects.equals(reactions, mixtape.reactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playlistId, name, createdAt, description, durationMS, creator, songIDs, songs, reactions);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Mixtape{");
        sb.append("id='").append(id).append('\'');
        sb.append(", playlistID='").append(playlistId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", description='").append(description).append('\'');
        sb.append(", durationMS=").append(durationMS);
        sb.append(", creator=").append(creator);
        sb.append(", songIDs=").append(songIDs);
        sb.append(", songs=").append(songs);
        sb.append(", reactions=").append(reactions);
        sb.append('}');
        return sb.toString();
    }
}
