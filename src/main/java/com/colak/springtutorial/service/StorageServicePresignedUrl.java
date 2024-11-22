package com.colak.springtutorial.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StorageServicePresignedUrl {

    private final MinioClient minioClient;

    // get file url
    @SneakyThrows(Exception.class)
    public String getPresignedObjectUrl(String bucketName, String objectName, Integer expiryInSeconds) {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .expiry(expiryInSeconds)
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build();
        return minioClient.getPresignedObjectUrl(args);
    }

    @SneakyThrows(Exception.class)
    public String getPresignedObjectUrl(String bucketName, String objectName, int duration, TimeUnit timeUnit) {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .expiry(duration, timeUnit)
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build();
        return minioClient.getPresignedObjectUrl(args);
    }

    // get file url
    @SneakyThrows(Exception.class)
    public String getPresignedObjectUrl(String bucketName, String objectName) {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build();
        return minioClient.getPresignedObjectUrl(args);
    }

}
