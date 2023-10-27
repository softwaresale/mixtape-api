package com.mixtape.mixtapeapi.mixtape;

import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MixtapeControllerTest {

    @Mock
    ProfileService mockProfileService;

    @Mock
    MixtapeService mockMixtapeService;

    MixtapeController mixtapeController;

    @BeforeEach
    void setUp() {
        mixtapeController = new MixtapeController(mockProfileService, mockMixtapeService);
    }

    @Test
    void createNewMixtape_succeeds_whenGoodInput() throws IOException {
        String mockProfileId = "profile-id";
        String mockPlaylistId = "playlist-id";
        MixtapeDTO.Create mockCreate = new MixtapeDTO.Create("mixtape", "desc", List.of("hi", "there"));
        Profile mockProfile = new Profile();
        Mixtape createdMixtape = new Mixtape(mockCreate);

        when(mockProfileService.findProfile(mockProfileId)).thenReturn(Optional.of(mockProfile));
        when(mockMixtapeService.createMixtapeForPlaylist(eq(mockProfile), eq(mockPlaylistId), any())).thenReturn(createdMixtape);

        Mixtape result = mixtapeController.createNewMixtape(mockProfileId, mockPlaylistId, mockCreate);
        assertThat(result).isEqualTo(createdMixtape);
        verify(mockMixtapeService).createMixtapeForPlaylist(eq(mockProfile), eq(mockPlaylistId), ArgumentMatchers.assertArg(mixtape -> {
            assertThat(mixtape.getName()).isEqualTo("mixtape");
            assertThat(mixtape.getDescription()).isEqualTo("desc");
            assertThat(mixtape.getSongIDs()).isEqualTo(List.of("hi", "there"));
        }));
    }

    @Test
    void createNewMixtape_throws400_whenMissingSongs() throws IOException {
        String mockProfileId = "profile-id";
        String mockPlaylistId = "playlist-id";
        MixtapeDTO.Create mockCreate = new MixtapeDTO.Create("mixtape", "desc", List.of());
        Profile mockProfile = new Profile();

        when(mockProfileService.findProfile(mockProfileId)).thenReturn(Optional.of(mockProfile));

        assertThatThrownBy(() -> {
            mixtapeController.createNewMixtape(mockProfileId, mockPlaylistId, mockCreate);
        })
                .isInstanceOf(ResponseStatusException.class);
    }
}
