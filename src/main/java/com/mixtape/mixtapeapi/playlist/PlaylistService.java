package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.invitation.Invitation;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final TrackService trackService;

    public PlaylistService(PlaylistRepository playlistRepository, TrackService trackService) {
        this.playlistRepository = playlistRepository;
        this.trackService = trackService;
    }

    public Optional<Playlist> findPlaylist(String id) {
        return playlistRepository.findById(id);
    }

    public Optional<Playlist> findPlaylistForProfile(Profile profile, String playlistId) throws IOException {
        Optional<Playlist> playlistOpt = playlistRepository.findByIdAndInitiatorOrTarget(playlistId, profile, profile);
        if (playlistOpt.isEmpty()) {
            return playlistOpt;
        }

        Playlist inflatedPlaylist = trackService.inflatePlaylist(playlistOpt.get());
        return Optional.of(inflatedPlaylist);
    }

    public List<Playlist> findPlaylistsForProfile(Profile profile) throws IOException {
        List<Playlist> playlists = playlistRepository.findByInitiatorOrTarget(profile, profile);
        for (var playlist : playlists) {
            trackService.inflatePlaylist(playlist);
        }

        return playlists;
    }

    public Playlist save(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Playlist createPlaylistFromInvitation(Invitation invitation) {
        // Create Playlist
        String defaultName = String.format("%s and %s's playlist", invitation.getInitiator().getDisplayName(), invitation.getTarget().getDisplayName());
        Playlist newPlaylist = new Playlist(null, null, defaultName, invitation.getInitiator(), invitation.getTarget(), null, null);

        // Save to repository
        return playlistRepository.save(newPlaylist);
    }

    public Optional<Playlist> updatePlaylist(Playlist playlist, String id) {
        // Create Optional
        Optional<Playlist> optionalPlaylist = Optional.empty();

        // If exists, add to optional
        if (playlistRepository.existsById(id)) {
            optionalPlaylist = Optional.of(playlistRepository.save(playlist));
        }

        // Return final optional
        return optionalPlaylist;
    }

}
