package com.mixtape.mixtapeapi.notification;

import com.mixtape.mixtapeapi.AbstractRestController;
import com.mixtape.mixtapeapi.profile.Profile;
import com.mixtape.mixtapeapi.profile.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile/{profileId}/notification")
public class NotificationController extends AbstractRestController {

    private final NotificationService notificationService;

    protected NotificationController(ProfileService profileService, NotificationService notificationService) {
        super(profileService);
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> getAllNotificationsForProfile(@PathVariable String profileId) {
        Profile profile = resolveProfileOr404(profileId);

        return notificationService.findAllNotificationsForProfile(profile);
    }
}
