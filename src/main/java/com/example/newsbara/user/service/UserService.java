package com.example.newsbara.user.service;

import com.example.newsbara.badge.repository.BadgeRepository;
import com.example.newsbara.badge.service.BadgeService;
import com.example.newsbara.global.common.apiPayload.code.status.ErrorStatus;
import com.example.newsbara.global.common.apiPayload.exception.GeneralException;
import com.example.newsbara.global.common.security.TokenProvider;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.dto.req.PointReqDto;
import com.example.newsbara.user.dto.req.UserLoginReqDto;
import com.example.newsbara.user.dto.req.UserSignupReqDto;
import com.example.newsbara.user.dto.res.PointResDto;
import com.example.newsbara.user.dto.res.TokenDto;
import com.example.newsbara.user.dto.res.UserInfoResDto;
import com.example.newsbara.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.time.Duration;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BadgeService badgeService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public UserService(UserRepository userRepository, BadgeService badgeService, PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.badgeService = badgeService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public UserInfoResDto signUp(UserSignupReqDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        User user = request.toEntity();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return UserInfoResDto.fromEntity(user);
    }

    @Transactional
    public TokenDto login(UserLoginReqDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        String accessToken = tokenProvider.createAccessToken(user);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    public void logout(Principal principal, HttpServletRequest request) {
        if (principal == null) {
            throw new GeneralException(ErrorStatus.USER_NOT_AUTHENTICATED);
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String token = tokenProvider.resolveToken(request);
        long expirationTime = tokenProvider.getTokenExpiration(token);
        redisTemplate.opsForValue().set(token, "blacklisted", Duration.ofMillis(expirationTime));
    }

    @Transactional
    public void deleteUser(Principal principal, HttpServletRequest request) {
        if (principal == null) {
            throw new GeneralException(ErrorStatus.USER_NOT_AUTHENTICATED);
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String token = tokenProvider.resolveToken(request);
        redisTemplate.delete(token);

        userRepository.delete(user);
    }


    @Transactional
    public PointResDto addPoint(Principal principal, PointReqDto request) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (request.getIsCorrect()) {
            // 맞으면 +20
            user.setPoint(user.getPoint()+20);
        } else {
            // 틀리면 +10
            user.setPoint(user.getPoint()+10);
        }

        badgeService.updateUserBadge(user);
        User savedUser = userRepository.save(user);

        return PointResDto.fromEntity(savedUser);
    }
}