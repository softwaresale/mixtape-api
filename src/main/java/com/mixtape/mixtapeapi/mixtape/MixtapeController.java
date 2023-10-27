package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/{profileId}/playlist/{playlistId}/mixtape")
public class MixtapeController extends AbstractRestController {

    private final MixtapeService mixtapeService;

    public MixtapeController(ProfileService profileService, MixtapeService mixtapeService) {
        super(profileService);
        this.mixtapeService = mixtapeService;
    }

    @GetMapping
    public List<Mixtape> getAllForPlaylist(@PathVariable String profileId, @PathVariable String playlistId) throws IOException {
        Profile profile = resolveProfileOr404(profileId);
        return mixtapeService.getAllForPlaylist(profile, playlistId);
    }

    @PostMapping
    public Mixtape createNewMixtape(@PathVariable String profileId, @PathVariable String playlistId, @RequestBody MixtapeDTO.Create createMixtapeDTO) {
        Profile creatingProfile = resolveProfileOr404(profileId);
        Mixtape newMixtape = new Mixtape(createMixtapeDTO);
        return mixtapeService.createMixtapeForPlaylist(creatingProfile, playlistId, newMixtape);
    }

    @GetMapping("/{mixtapeId}")
    public Mixtape getMixtape(@PathVariable String profileId, @PathVariable String playlistId, @PathVariable String mixtapeId) throws IOException {
        // TODO add some sort of check with the playlist id
        return mixtapeService.getById(mixtapeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
