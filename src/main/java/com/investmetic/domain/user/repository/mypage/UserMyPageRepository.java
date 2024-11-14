package com.investmetic.domain.user.repository.mypage;

import com.investmetic.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMyPageRepository extends JpaRepository<User, Long>, UserMyPageRepositoryCustom {


}
