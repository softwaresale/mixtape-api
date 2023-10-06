package com.mixtape.spotifyauthserver.data.authclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JpaAuthorizedClientServiceTest {

    private static final AuthorizedClient MOCK_AUTHORIZED_CLIENT = new AuthorizedClient(
        "id", "provider-token", "client", "user", Instant.now(), Instant.now().plus(Duration.ofHours(1)), Set.of()
    );

    @Mock JpaAuthorizedClientRepository mockRepository;

    JpaAuthorizedClientService service;

    @BeforeEach
    void beforeEach() {
        service = new JpaAuthorizedClientService(mockRepository);
    }

    @Test
    void getMostRecentExclusive_returnsEmpty_whenNoEntities() {
        when(mockRepository.findAllByClientRegistrationIdAndPrincipalUserIdOrderByIssuedAtDesc(any(), any()))
                .thenReturn(List.of());

        Optional<AuthorizedClient> mostRecent = service.getMostRecentExclusive("client", "user");

        assertThat(mostRecent).isEmpty();
        verify(mockRepository).findAllByClientRegistrationIdAndPrincipalUserIdOrderByIssuedAtDesc("client", "user");
        verify(mockRepository, times(0)).deleteAll(any());
    }

    @Test
    void getMostRecentExclusive_returnsSingleAndDoesntDeleteAnything_whenOnlyOneIsPresent() {
        when(mockRepository.findAllByClientRegistrationIdAndPrincipalUserIdOrderByIssuedAtDesc(any(), any()))
                .thenReturn(new ArrayList<>(List.of(MOCK_AUTHORIZED_CLIENT)));

        Optional<AuthorizedClient> mostRecent = service.getMostRecentExclusive("client", "user");

        assertThat(mostRecent).hasValue(MOCK_AUTHORIZED_CLIENT);
        verify(mockRepository).findAllByClientRegistrationIdAndPrincipalUserIdOrderByIssuedAtDesc("client", "user");
        verify(mockRepository, times(0)).deleteAll(any());
    }

    @Test
    void getMostRecentExclusive_returnsSingleAndRemovesOthers_whenOthersPresent() {
        AuthorizedClient otherAuthorizedClient = new AuthorizedClient(MOCK_AUTHORIZED_CLIENT);
        otherAuthorizedClient.setId("id-2");

        when(mockRepository.findAllByClientRegistrationIdAndPrincipalUserIdOrderByIssuedAtDesc(any(), any()))
                .thenReturn(new ArrayList<>(List.of(MOCK_AUTHORIZED_CLIENT, otherAuthorizedClient)));

        Optional<AuthorizedClient> mostRecent = service.getMostRecentExclusive("client", "user");

        assertThat(mostRecent).hasValue(MOCK_AUTHORIZED_CLIENT);
        verify(mockRepository).findAllByClientRegistrationIdAndPrincipalUserIdOrderByIssuedAtDesc("client", "user");
        verify(mockRepository).deleteAll(List.of(otherAuthorizedClient));
    }
}
