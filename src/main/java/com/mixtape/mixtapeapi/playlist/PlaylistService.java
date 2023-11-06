package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.notification.NotificationService;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.tracks.TrackService;
import jakarta.transaction.Transactional;
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

    public Optional<Playlist> findPlaylistForProfile(Profile profile, String playlistId) {
        Optional<Playlist> playlistOpt = playlistRepository.findByIdAndInitiatorOrIdAndTarget(playlistId, profile, playlistId, profile);
        if (playlistOpt.isEmpty()) {
            return playlistOpt;
        }

        Playlist inflatedPlaylist = trackService.inflatePlaylist(playlistOpt.get());
        return Optional.of(inflatedPlaylist);
    }

    public List<Playlist> findPlaylistsForProfile(Profile profile) {
        // Grab playlists
        List<Playlist> playlists = playlistRepository.findByInitiatorAndTargetNotNullOrTarget(profile, profile);

        // Fill out playlist
        playlists.forEach(trackService::inflatePlaylist);

        return playlists;
    }

    public Playlist savePlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Playlist createPlaylist(Profile initiator, PlaylistDTO.Create newPlaylistDTO, Profile requestedTarget) {
        // Create partial playlist
        Playlist playlist = new Playlist(null, "", newPlaylistDTO.name, initiator, null, newPlaylistDTO.description, newPlaylistDTO.coverPicURL);
        playlist = savePlaylist(playlist);

        // Create contents and notification for accepting or denying playlist
        String contents = String.format("%s wants to invite you to the playlist %s", initiator.getDisplayName(), playlist.getName());
        notificationService.createNotificationFromTrigger(playlist, initiator, requestedTarget, contents);

        return playlist;
    }

    @Transactional
    public Playlist acceptPlaylist(Profile target, String playlistId) {
        // Grab playlist and delete corresponding notification
        Playlist playlist = grabPlaylistAndDeleteNotification(target, playlistId);

        // Fill out fields to update
        playlist.setTarget(target);

        // Update playlist
        return savePlaylist(playlist);
    }

    @Transactional
    public void denyPlaylist(Profile target, String playlistId) {
        // Grab playlist and delete corresponding notification
        Playlist playlist = grabPlaylistAndDeleteNotification(target, playlistId);

        // Delete playlist
        playlistRepository.delete(playlist);
    }

    private Playlist grabPlaylistAndDeleteNotification(Profile target, String playlistId) {
        // Delete notification
        notificationService.deleteNotificationByTargetAndExternalId(target, playlistId);

        // Return playlist
        return findPlaylist(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Playlist does not exist"));
    }

    public void removePlaylist(Profile profile, String playlistId) {
        // Verify profile owns playlist
        Playlist playlist = playlistRepository
                .findByIdAndInitiatorOrIdAndTarget(playlistId, profile, playlistId, profile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist given is not part of profile"));

        // Delete all notifications within playlist
        notificationService.deleteNotificationsOfMixtapes(playlist);

        // Delete playlist
        playlistRepository.deleteById(playlistId);
    }

    public void removePlaylistsByInitiatorAndTarget(Profile initiator, Profile target) {
        // Delete playlist
        playlistRepository.deleteAllByInitiatorAndTarget(initiator, target);
    }

    public Playlist setPlaylistPicture(String playlistId, MultipartFile picture) throws IOException {
        Playlist requestedPlaylist = findPlaylist(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested playlist does not exist"));

        String pictureURL = pictureUploadService.uploadPictureForPlaylist(playlistId, picture);
        requestedPlaylist.setCoverPicURL(pictureURL);
        return savePlaylist(requestedPlaylist);
    }
}
