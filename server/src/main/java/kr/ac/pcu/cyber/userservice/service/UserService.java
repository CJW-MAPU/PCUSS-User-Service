package kr.ac.pcu.cyber.userservice.service;

import kr.ac.pcu.cyber.userservice.domain.dto.ModifyRequestData;
import kr.ac.pcu.cyber.userservice.domain.entity.User;
import kr.ac.pcu.cyber.userservice.domain.repository.UserRepository;
import kr.ac.pcu.cyber.userservice.errors.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * 사용자 nickname 을 수정한다.
     *
     * @param targetId : 수정하려는 대상의 id
     * @param modifyRequestData : nickname field 만 존재하는 dto
     * @param userId : 수정을 시도하는 요청의 토큰에 담긴 userId
     * @return targetUser 수정된 사용자의 정보
     */
    public User modifyUser(Long targetId,
                           ModifyRequestData modifyRequestData,
                           String userId) {
        User targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new UserNotFoundException(targetId.toString()));

        if (!targetUser.isSameUser(userId)) {
            throw new AccessDeniedException("권한 없음");
        }

        User source = modelMapper.map(modifyRequestData, User.class);

        if(source.getProfileImage() == null) {
            targetUser.changeNicknameWith(source);
        }else if(source.getNickname() == null) {
            targetUser.changeProfileImageWith(source);
        }

        return targetUser;
    }
}
