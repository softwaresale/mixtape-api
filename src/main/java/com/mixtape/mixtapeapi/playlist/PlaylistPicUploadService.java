package com.mixtape.mixtapeapi.playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class PlaylistPicUploadService {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistPicUploadService.class);

    private final S3Client pictureBucketClient;
    private final PlaylistPicUrlFormatter playlistPicUrlFormatter;

    public PlaylistPicUploadService(S3Client pictureBucketClient, PlaylistPicUrlFormatter playlistPicUrlFormatter) {
        this.pictureBucketClient = pictureBucketClient;
        this.playlistPicUrlFormatter = playlistPicUrlFormatter;
    }

    public String uploadPictureForPlaylist(String playlistId, MultipartFile file) throws IOException {
        String pictureObjectId = playlistPicUrlFormatter.formatObjectId(playlistId, file.getOriginalFilename());
        logger.debug("Uploading profile pic for playlist {} called {}", playlistId, pictureObjectId);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket("playlist-pics")
                .key(pictureObjectId)
                .build();

        pictureBucketClient.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return playlistPicUrlFormatter.formatPlaylistPicURL(playlistId, file.getOriginalFilename());
    }
}
