package com.example.newsbara.user.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.dto.res.MypageResDto;
import com.example.newsbara.user.dto.res.UserInfoResDto;
import com.example.newsbara.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
public class MypageService {

    private UserRepository userRepository;

    public MypageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public MypageResDto getMypage(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return MypageResDto.fromEntity(user);

    }

}
