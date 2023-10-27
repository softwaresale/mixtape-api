package com.mixtape.mixtapeapi.playlist;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
