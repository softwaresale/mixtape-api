package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/{profileId}/playlist")
public class PlaylistController extends AbstractRestController {
    private final PlaylistService playlistService;

    public PlaylistController(ProfileService profileService, PlaylistService playlistService) {
        super(profileService);
        this.playlistService = playlistService;
    }

    @GetMapping
    public List<Playlist> getPlaylistsForProfile(@PathVariable String profileId) throws IOException {
        Profile profile = resolveProfileOr404(profileId);
        return playlistService.findPlaylistsForProfile(profile);
    }

    @PostMapping
    public Playlist createPlaylist(@PathVariable String profileId, @RequestBody PlaylistDTO.Create createPlaylist) {
        Profile target = resolveProfileOr404(createPlaylist.requestedUserID);
        Profile initiator = resolveProfileOr404(profileId);
        return playlistService.createPlaylist(initiator, createPlaylist, target);
    }

    @GetMapping("/{id}")
    public Playlist getById(@PathVariable String profileId, @PathVariable String id) throws IOException {
        Profile profile = resolveProfileOr404(profileId);
        return playlistService.findPlaylistForProfile(profile, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Playlist update(@PathVariable String profileId, @PathVariable String id, @RequestBody Playlist playlist) {
        return playlistService.updatePlaylist(playlist, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/cover-pic")
    public Playlist setCoverPic(@PathVariable String profileId, @PathVariable String id, @RequestParam("file") MultipartFile imageFile) throws IOException {
        return playlistService.setPlaylistPicture(id, imageFile);
    }
}
