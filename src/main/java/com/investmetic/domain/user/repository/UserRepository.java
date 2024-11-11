package com.investmetic.domain.user.repository;

import com.investmetic.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long>, UserRepositoryCustom {
}
