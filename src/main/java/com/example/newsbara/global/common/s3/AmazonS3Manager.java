package com.example.newsbara.global.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.global.common.config.AmazonConfig;
import com.example.newsbara.user.domain.Uuid;
import com.example.newsbara.user.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;


    public String generateProfileKeyName(Uuid uuid) {
        return amazonConfig.getProfilePath() + '/' + uuid.getUuid();
    }


    public String uploadFile(String keyName, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED);
        }

        System.out.println(keyName);
        ObjectMetadata metadata = createObjectMetadata(file);


        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_DELETE_FAILED);
        }

        try {
            String fileKey = extractFileKeyFromUrl(fileUrl);
            amazonS3.deleteObject(new DeleteObjectRequest(amazonConfig.getBucket(), fileKey));
        } catch (Exception e) {
            log.error("Failed to delete file: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.FILE_DELETE_FAILED);
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType() != null ?
                file.getContentType() :
                "application/octet-stream");
        return metadata;
    }

    private String extractFileKeyFromUrl(String fileUrl) {
        String encodedFileKey = fileUrl.replace(
                "https://" + amazonConfig.getBucket() + ".s3." +
                        amazonConfig.getRegion() + ".amazonaws.com/",
                ""
        );
        return URLDecoder.decode(encodedFileKey, StandardCharsets.UTF_8);
    }
}