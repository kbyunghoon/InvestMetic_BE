package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<UserProfileDto> findByEmailUserInfo(String email);

}
