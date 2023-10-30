package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/{playlistId}")
    public Playlist getPlaylist(@PathVariable String profileId, @PathVariable String playlistId) throws IOException {
        Profile profile = resolveProfileOr404(profileId);
        return playlistService.findPlaylistForProfile(profile, playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<Playlist> getPlaylistsForProfile(@PathVariable String profileId) throws IOException {
        Profile profile = resolveProfileOr404(profileId);
        return playlistService.findPlaylistsForProfile(profile);
    }

    @PostMapping
    public Playlist createPlaylist(@PathVariable String profileId, @RequestBody PlaylistDTO.Create newPlaylistDTO) {
        Profile target = resolveProfileOr404(newPlaylistDTO.requestedUserID);
        Profile initiator = resolveProfileOr404(profileId);
        return playlistService.createPlaylist(initiator, newPlaylistDTO, target);
    }

    @PutMapping("/{playlistId}/cover-pic")
    public Playlist setCoverPic(@PathVariable String profileId, @PathVariable String playlistId, @RequestParam("file") MultipartFile imageFile) throws IOException {
        return playlistService.setPlaylistPicture(playlistId, imageFile);
    }

    @PutMapping("/{playlistId}/accept")
    public Playlist acceptPlaylist(@PathVariable String profileId, @PathVariable String playlistId) {
        Profile profile = resolveProfileOr404(profileId);
        return playlistService.acceptPlaylist(profile, playlistId);
    }

    @DeleteMapping("/{playlistId}/deny")
    public void denyPlaylist(@PathVariable String profileId, @PathVariable String playlistId) {
        Profile profile = resolveProfileOr404(profileId);
        playlistService.denyPlaylist(profile, playlistId);
    }
}
