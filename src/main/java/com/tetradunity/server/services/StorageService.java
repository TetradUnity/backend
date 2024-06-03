package com.tetradunity.server.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(MultipartFile file, String folderName) {
        if (!doesFolderExist(folderName)) {
            return null;
        }

        if (file.isEmpty()) {
            return null;
        }

        File fileObj = convertMultiPartFileToFile(file);
        String fileName = folderName + "/" + UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());

        try {
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        } catch (Exception e) {
            return null;
        } finally {
            fileObj.delete();
        }
        return fileName;
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean doesFolderExist(String folderName) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(folderName + "/")
                .withMaxKeys(1);

        ListObjectsV2Result result = s3Client.listObjectsV2(request);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        return !objects.isEmpty();
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            return "";
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            return null;
        }
        return convertedFile;
    }
}