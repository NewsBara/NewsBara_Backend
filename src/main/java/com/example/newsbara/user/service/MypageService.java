package com.example.newsbara.user.service;

import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.dto.req.NameReqDto;
import com.example.newsbara.user.dto.res.MypageResDto;
import com.example.newsbara.user.dto.res.NameResDto;
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
            user.setName(request.getName());
        } else {
            throw new GeneralException(ErrorStatus.NAME_IS_NULL);
        }


        return NameResDto.fromEntity(userRepository.save(user));
    }
}
