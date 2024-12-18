package com.colak.springtutorial.controller;

import com.colak.springtutorial.config.MinioConfig;
import com.colak.springtutorial.service.StorageService;
import com.colak.springtutorial.service.StorageServiceDownload;
import com.colak.springtutorial.service.StorageServiceFileStatus;
import com.colak.springtutorial.service.StorageServicePresignedUrl;
import io.minio.StatObjectResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@RequestMapping("/oss")

@RequiredArgsConstructor
@Slf4j
public class OSSController {

    private final StorageService storageService;

    private final StorageServiceDownload storageServiceDownload;

    private final StorageServiceFileStatus storageServiceFileStatus;

    private final StorageServicePresignedUrl storageServicePresignedUrl;

    private final MinioConfig minioConfig;

    // file upload
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            // file name
            String fileName = file.getOriginalFilename();
            String newFileName = System.currentTimeMillis() + "." + StringUtils.substringAfterLast(fileName, ".");
            // type
            String contentType = file.getContentType();
            storageService.uploadFile(minioConfig.getBucketName(), file, newFileName, contentType);
            return "upload success";
        } catch (Exception exception) {
            log.error("upload fail", exception);
            return "upload fail";
        }
    }

    // delete
    @DeleteMapping("/")
    public void delete(@RequestParam("fileName") String fileName) {
        storageService.removeFile(minioConfig.getBucketName(), fileName);
    }

    // get file info
    @GetMapping("/info")
    public String getFileStatusInfo(@RequestParam("fileName") String fileName) {
        StatObjectResponse statObject = storageServiceFileStatus.getFileStatusInfo(minioConfig.getBucketName(), fileName);
        return statObject.toString();
    }

    // http://localhost:8088/oss/url?fileName=testfile.txt
    @GetMapping("/url")
    public String getPresignedObjectUrl(@RequestParam("fileName") String fileName) {
        return storageServicePresignedUrl.getPresignedObjectUrl(minioConfig.getBucketName(), fileName);
    }

    // file download
    @GetMapping("/download")
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            // Set Response Header
            StatObjectResponse statObject = storageServiceFileStatus.getFileStatusInfo(minioConfig.getBucketName(), fileName);
            response.setContentType(statObject.contentType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);

            // Download File
            InputStream fileInputStream = storageServiceDownload.getObject(minioConfig.getBucketName(), fileName);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            IOUtils.copyLarge(fileInputStream, response.getOutputStream());

        } catch (Exception e) {
            log.error("download failed");
        }
    }

    @GetMapping("/downloadZip")
    public void downloadMultipleFiles(List<String> fileIds, HttpServletResponse response) throws IOException {
        // Set Response Header
        response.setContentType("application/zip");
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"");

        // Create Zip File and write to Output Stream
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (String fileId : fileIds) {
                try (InputStream inputStream = storageServiceDownload.getObject(minioConfig.getBucketName(), fileId);) {
                    ZipEntry zipEntry = new ZipEntry(fileId);
                    zipOut.putNextEntry(zipEntry);
                    // StreamUtils.copy(inputStream, zipOut);
                    IOUtils.copyLarge(inputStream, zipOut);
                    zipOut.closeEntry();
                }
            }
            zipOut.finish();
        }
    }

}
