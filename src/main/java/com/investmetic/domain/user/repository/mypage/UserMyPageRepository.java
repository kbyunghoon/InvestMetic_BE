package com.investmetic.domain.user.repository.mypage;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMyPageRepository extends JpaRepository<User, Long>, UserMyPageRepositoryCustom {

    Optional<UserProfileDto> findByEmailUserInfo(String email);

}
