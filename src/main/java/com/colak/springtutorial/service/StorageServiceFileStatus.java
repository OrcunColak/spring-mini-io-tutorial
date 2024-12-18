package com.colak.springtutorial.service;

import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageServiceFileStatus {

    private final MinioClient minioClient;

    // get file info
    @SneakyThrows(Exception.class)
    public StatObjectResponse getFileStatusInfo(String bucketName, String objectName) {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

}
