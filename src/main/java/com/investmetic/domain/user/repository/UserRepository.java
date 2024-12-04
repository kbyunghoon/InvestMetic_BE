package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Long countByRole(Role role);

    @Query("select u.role from User u where u.userId = :userId")
    Optional<Role> findRoleByUserUserId(@Param("userId") Long userId);

    Optional<User> findByEmail(String email);

    @Query("select u.password from User u where u.email = :email")
    Optional<String> findPasswordByEmail(@Param("email") String email);

    @Query("select u.email from User u where u.phone = :phone")
    Optional<String> findEmailByPhone(@Param("phone") String phone);

    @Query("select u from User u  where u.role = 'SUPER_ADMIN' order by u.userId limit 1")
    User findSuperAdminUser();


}
