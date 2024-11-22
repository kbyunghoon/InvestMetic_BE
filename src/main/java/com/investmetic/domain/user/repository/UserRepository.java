package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Boolean existsByemail(String email);

    Optional<User> findByEmail(String email);
    ;
}
