package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;


@Service
@Slf4j
public class AwsS3Service {
    private final String BUCKET_NAME = "fashion-shop-management";

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            String s3FileName = file.getOriginalFilename();

            // Tạo credentials cho AWS S3
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
            S3Client s3Client = S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .region(Region.of("ap-southeast-1"))
                    .build();

            // Set metadata for the object
            String contentType = "image/jpeg"; // Default to jpeg
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                if (originalFilename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (originalFilename.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (originalFilename.endsWith(".bmp")) {
                    contentType = "image/bmp";
                } else if (originalFilename.endsWith(".tiff")) {
                    contentType = "image/tiff";
                } else if (originalFilename.endsWith(".webp")) {
                    contentType = "image/webp";
                } else if (originalFilename.endsWith(".jpg")) {
                    contentType = "image/jpg";

                }
            }

            // Create a put request to upload the image to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(s3FileName)
                    .contentType(contentType)
                    .build();

            // Upload file lên S3
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            log.info("File uploaded successfully: {}", s3FileName);

            // Trả về URL file trên S3
            return "https://" + BUCKET_NAME + ".s3.ap-southeast-1.amazonaws.com/" + s3FileName;
        } catch (S3Exception e) {
            log.error("AWS S3 Error: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error while uploading image to S3: " + e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            log.error("I/O Error while uploading image: {}", e.getMessage());
            throw new RuntimeException("Error while saving image to S3");
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while uploading image to S3");
        }

    }
}
