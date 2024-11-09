package com.investmetic.domain.user.repository;


import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 회원 정보 제공
     */
    @Override
    public Optional<UserProfileDto> findByEmailUserInfo(String email) {

        QUser user = QUser.user;

        return Optional.ofNullable(queryFactory.from(user)
                .select(Projections.fields(UserProfileDto.class,
                        user.userId, user.username, user.nickname, user.email, user.imageUrL, user.phone,
                        user.infoAgreement))
                .where(user.email.eq(email))
                .fetchOne()); // 하나의 객체 반활
    }


}
