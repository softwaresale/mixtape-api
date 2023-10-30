package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final NotificationService notificationService;
    private final TrackService trackService;
    private final PlaylistPicUploadService pictureUploadService;

    public PlaylistService(PlaylistRepository playlistRepository, NotificationService notificationService, TrackService trackService, PlaylistPicUploadService pictureUploadService) {
        this.playlistRepository = playlistRepository;
        this.notificationService = notificationService;
        this.trackService = trackService;
        this.pictureUploadService = pictureUploadService;
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

    public Playlist savePlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Playlist createPlaylist(Profile initiator, PlaylistDTO.Create newPlaylistDTO, Profile requestedTarget) {
        // Create the playlist
        Playlist playlist = new Playlist(null, "", newPlaylistDTO.name, initiator, null, newPlaylistDTO.description, newPlaylistDTO.coverPicURL);
        playlist = savePlaylist(playlist);

        // Create notification for accepting or denying playlist
        notificationService.createNotificationFromPlaylist(playlist, requestedTarget);

        return playlist;
    }

    public Playlist acceptPlaylistInvitation(Profile acceptor, String playlistId) {
        Playlist requestedPlaylist = this.findPlaylist(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requestedPlaylist.setTarget(acceptor);
        return savePlaylist(requestedPlaylist);
    }

    public Playlist acceptPlaylist(Profile target, String playlistId) {
        // TODO
        return null;
    }

    public void denyPlaylist(Profile target, String playlistId) {
        // Grab friendship
        Playlist playlist = findPlaylist(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship does not exist"));

        // Delete notification
        notificationService.deleteNotificationFromPlaylist(playlist, target);

        // Delete friendship
        playlistRepository.delete(playlist);

    }

    public void removePlaylist(String playlistId) {
        this.playlistRepository.deleteById(playlistId);
    }

    public Playlist setPlaylistPicture(String playlistId, MultipartFile picture) throws IOException {
        Playlist requestedPlaylist = findPlaylist(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested playlist does not exist"));

        String pictureURL = pictureUploadService.uploadPictureForPlaylist(playlistId, picture);
        requestedPlaylist.setCoverPicURL(pictureURL);
        return savePlaylist(requestedPlaylist);
    }
}
