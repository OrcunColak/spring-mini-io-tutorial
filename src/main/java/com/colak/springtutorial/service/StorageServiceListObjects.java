package com.colak.springtutorial.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceListObjects {

    private final MinioClient minioClient;

    // Query files based on file prefix
    @SneakyThrows(Exception.class)
    public List<Item> getAllObjectsByPrefix(String bucketName,
                                            String prefix,
                                            boolean recursive) {
        List<Item> list = new ArrayList<>();
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .recursive(recursive)
                .build();

        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(listObjectsArgs);
        if (objectsIterator != null) {
            for (Result<Item> result : objectsIterator) {
                Item item = result.get();
                list.add(item);
            }
        }
        return list;
    }

    // Get the list of files under the path
    public Iterable<Result<Item>> listObjects(String bucketName, String prefix, boolean recursive) {
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .recursive(recursive)
                .build();
        return minioClient.listObjects(listObjectsArgs);
    }

}
