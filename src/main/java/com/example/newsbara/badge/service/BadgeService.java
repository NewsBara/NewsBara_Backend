package com.example.newsbara.badge.service;

import com.example.newsbara.badge.dto.res.BadgeResDto;
import com.example.newsbara.badge.repository.BadgeRepository;
import com.example.newsbara.user.domain.User;
import com.example.newsbara.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Transactional
public class BadgeService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;

    public BadgeService(UserRepository userRepository, BadgeRepository badgeRepository) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
    }

    public BadgeResDto getBadgeInfo(Principal principal) {

        User user = userRepository.findByEmail(principal.getName());
    }
}
