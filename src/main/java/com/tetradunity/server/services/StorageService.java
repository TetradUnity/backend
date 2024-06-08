package com.tetradunity.server.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.tetradunity.server.models.CustomMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public MultipartFile convertStringToMultipartFile(String content, String fileName) {
        byte[] contentBytes = content.getBytes();
        return new CustomMultipartFile(fileName, fileName, "text/plain", contentBytes);
    }

    public String getFileExtension(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            return "";
        }
    }

    public String determineFileType(String path) {
        return switch (getFileExtension(path)) {
            case ".pdf" -> "application/pdf";
            case ".png" -> "image/png";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".txt" -> "text/plain";
            case ".gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }

    public byte[] downloadFile(String path) {
        S3ObjectInputStream inputStream = s3Client
                .getObject(bucketName, path)
                .getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            return null;
        }
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


    public File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            return null;
        }
        return convertedFile;
    }

    private boolean checkExtensionImage(String extension) {
        return switch (extension) {
            case ".jpg", ".jpeg", ".png", ".bmp" -> true;
            default -> false;
        };
    }

    public double findRatio(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);
        if (checkExtensionImage(extension)) {
            try {
                BufferedImage image = ImageIO.read(convertMultiPartFileToFile(file));
                if (image != null) {
                    return (double) image.getWidth() / image.getHeight();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
        return 0;
    }
}