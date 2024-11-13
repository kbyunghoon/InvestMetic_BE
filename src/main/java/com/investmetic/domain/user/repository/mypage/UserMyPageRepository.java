package com.investmetic.domain.user.repository.mypage;

import com.investmetic.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMyPageRepository extends JpaRepository<User, Long>, UserMyPageRepositoryCustom {


}
