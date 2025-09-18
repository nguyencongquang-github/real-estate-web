package com.example.demo.service.aws;

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

import java.io.IOException;

@Service
@Slf4j
public class AwsS3ServiceImpl {

    private final String BUCKET_NAME = "fashion-shop-management";

    @Value("${aws.s3.accessKey}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secretKey}")
    private String awsS3SecretKey;

    public String saveImageToS3(MultipartFile photo) {
        try {
            // Get original file name
            String s3FileName = photo.getOriginalFilename();

            // Create AWS credentials using access key and secret key
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsS3AccessKey, awsS3SecretKey);

            // Create S3 client with config credentials and region
            S3Client s3Client = S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .region(Region.AP_SOUTHEAST_1)
                    .build();

            // Set metadata for the object
            String contentType = "image/jpeg"; // Default to jpeg
            String originalFilename = photo.getOriginalFilename();
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
                }
            }

            // Create a put request to upload the image to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(s3FileName)
                    .contentType(contentType)
                    .build();

            // Upload the image
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(photo.getBytes()));

            // Return the S3 file URL
            return "https://" + BUCKET_NAME + ".s3.ap-southeast-1.amazonaws.com/" + s3FileName;

        } catch (IOException e) {
            log.error("Error while uploading image to S3: {}", e.getMessage());
            throw new RuntimeException("Error while saving image to S3");
        }
    }
}
