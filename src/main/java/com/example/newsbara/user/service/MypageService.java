package com.example.newsbara.user.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.global.common.s3.AmazonS3Manager;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.domain.Uuid;
import com.example.newsbara.user.dto.req.NameReqDto;
import com.example.newsbara.user.dto.res.MypageResDto;
import com.example.newsbara.user.dto.res.NameResDto;
import com.example.newsbara.user.dto.res.ProfileResDto;
import com.example.newsbara.user.dto.res.UserInfoResDto;
import com.example.newsbara.user.repository.UserRepository;
import com.example.newsbara.user.repository.UuidRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

@Service
public class MypageService {

    private UserRepository userRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    public MypageService(UserRepository userRepository, AmazonS3Manager s3Manager, UuidRepository uuidRepository) {
        this.userRepository = userRepository;
        this.s3Manager = s3Manager;
        this.uuidRepository = uuidRepository;
    }

    @Transactional(readOnly = true)
    public MypageResDto getMypage(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return MypageResDto.fromEntity(user);

    }

    @Transactional
    public NameResDto putName(Principal principal, NameReqDto request) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (request.getName() != null && !request.getName().equals("")) {
            if (!request.getName().equals(user.getName()) &&
                    userRepository.existsByName(request.getName())) {
                throw new GeneralException(ErrorStatus.NAME_ALREADY_EXISTS);
            }

            user.setName(request.getName());
        } else {
            throw new GeneralException(ErrorStatus.NAME_IS_NULL);
        }


        return NameResDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public ProfileResDto putProfile(Principal principal, MultipartFile file) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));


        if (file != null && !file.isEmpty()) {
            try {
                if (user.getProfileImg() != null) {
                    s3Manager.deleteFile(user.getProfileImg());
                }
                Uuid imgUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
                String imgURL = s3Manager.uploadFile(s3Manager.generateProfileKeyName(imgUuid), file);
                user.setProfileImg(imgURL);
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAILED);
            }
        } else {
            throw new GeneralException(ErrorStatus.FILE_IS_NULL);
        }

        return ProfileResDto.fromEntity(user);
    }
}
