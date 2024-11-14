package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User,Long>, UserRepositoryCustom {

    Optional<User> findByEmail(String email);

    @Query("select u.password from User u where u.email = :email")
    Optional<String> findPasswordByEmail(@Param("email")String email);
}
