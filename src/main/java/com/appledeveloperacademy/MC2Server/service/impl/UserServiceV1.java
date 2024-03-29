package com.appledeveloperacademy.MC2Server.service.impl;

import com.appledeveloperacademy.MC2Server.domain.HealthTag;
import com.appledeveloperacademy.MC2Server.domain.Invitation;
import com.appledeveloperacademy.MC2Server.domain.Member;
import com.appledeveloperacademy.MC2Server.dto.request.CreateHealthTagReq;
import com.appledeveloperacademy.MC2Server.dto.request.CreateUserReq;
import com.appledeveloperacademy.MC2Server.exception.CustomException;
import com.appledeveloperacademy.MC2Server.exception.ErrorCode;
import com.appledeveloperacademy.MC2Server.repository.RoomRepository;
import com.appledeveloperacademy.MC2Server.repository.UserRepository;
import com.appledeveloperacademy.MC2Server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Qualifier(value = "userServiceV1")
public class UserServiceV1 implements UserService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Override
    public Member findUserByUserCode(String usercode) {
        List<Member> members = userRepository.findByUsercode(usercode);

        if (members.size() > 1) {
            throw new CustomException(ErrorCode.USERCODE_DUPLICATED);
        } else if (members.size() == 0) {
            throw new CustomException(ErrorCode.UNAUTHENTICATED_MEMBER);
        }
        return userRepository.findByUsercode(usercode).get(0);
    }

    @Override
    public List<HealthTag> findHealthTagsByUserId(Long userId) {
        return userRepository.listHealthTagsById(userId);
    }

    @Override
    @Transactional
    public Member createUser(CreateUserReq createUserReq) {
        String usercode;

        do {
            usercode = UUID.randomUUID().toString();
        } while (userRepository.findByUsercode(usercode).size() > 0);

        Member member = new Member(createUserReq.getUsername(), usercode);

        userRepository.save(member);

        return member;
    }

    @Override
    @Transactional
    public Long createHealthTag(Long userId, CreateHealthTagReq tagReq) {
        Member member = userRepository.findById(userId);

        // tag 중복검사
        List<HealthTag> tags = userRepository.listHealthTagsByTagContent(userId, tagReq.getTag());
        System.out.println("tags.size() = " + tags.size());
        if (tags.size() > 0) {
            System.out.println("씨발 이래도?");
            throw new CustomException(ErrorCode.TAG_DUPLICATED);
        }

        HealthTag tag = tagReq.build();
        member.addHealthTags(tag);

        userRepository.flush();

        return tag.getId();
    }

    @Override
    public Long verifyInvitation(String code) {
        log.info(code + "코드는 ");
        // invitation code가 있는지 확인
        List<Invitation> invitationList = roomRepository.getInvitationByCode(code);

        if (invitationList.size() == 0) {
            throw new CustomException(ErrorCode.INVITATION_NOT_FOUND);
        }

        // 없으면 -> 초대코드 없음 에러
        Invitation invitation = invitationList.get(0);
        if (invitation.isExpired()) {
            throw new CustomException(ErrorCode.INVITATION_EXPIRED);
        }

        return invitation.getId();
    }
}
