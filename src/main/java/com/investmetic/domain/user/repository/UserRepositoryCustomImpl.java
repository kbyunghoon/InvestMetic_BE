package com.investmetic.domain.user.repository;

import static com.investmetic.domain.user.model.entity.QUser.user;

import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.QUser;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    /**
     * 회원 정보 제공
     */
    @Override
    public Optional<UserProfileDto> findByEmailUserInfo(String email) {

        QUser user = QUser.user;

        return Optional.ofNullable(queryFactory.from(user)
                .select(Projections.fields(UserProfileDto.class,
                        user.userId, user.userName, user.nickname, user.email, user.imageUrl, user.phone,
                        user.infoAgreement))
                .where(user.email.eq(email))
                .fetchOne());
    }

    /**
     * 관리자 회원 관리 페이지 회원 목록 페이지네이션.
     * <pre>
     *  condition과 keyword에 null값 입력 가능, role에는 null값 입력 불가.
     *  SUPER_ADMIN은 모든 조회 탭에서 제외됩니다.
     *  condition은 있는데 keyword가 없는 경우(null, “”, “ “) keyword 없이 그냥 모든 회원 조회됩니다.
     *  condition없는데 keyword가 있는경우 keyword 없이 모든 회원 조회됩니다.
     *  따라서 condition이나 keyword 둘 중 하나라도 없으면 모든 회원 조회됩니다.
     *  condition이나 role에 잘못된 값이 들어가는 경우 exception 처리 됩니다.
     * </pre>
     * */
    @Override
    public Page<UserProfileDto> getAdminUsersPage(UserAdminPageRequestDto requestDto, Pageable pageable){

        QUser user = QUser.user;

        // where절에 들어갈 조건들 담는 List<Predicate>.
        List<Predicate> condition = new ArrayList<>();

        // 예외 먼저 검출될 수 있도록, Predicate로 묶어서 전달.
        condition.add(keywordCondition(requestDto.getCondition(), requestDto.getKeyword()));
        condition.add(roleCondition(requestDto.getRole()));

        Optional<List<UserProfileDto>> content = Optional.ofNullable(queryFactory
                .select(Projections.fields(UserProfileDto.class,
                        user.userId,
                        user.userName,
                        user.email,
                        user.imageUrl,
                        user.nickname,
                        user.phone,
                        user.infoAgreement,
                        user.role))
                .from(user)
                .where(condition.toArray(new Predicate[0]))
                .orderBy(orderByLatest())
                .offset(pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .fetch());

        //null시 바로 던짐.
        content.orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        // PageableExecutionUtils 이용. count쿼리 최소화.
        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .where(condition.toArray(new Predicate[0]))
                .from(user);

        return PageableExecutionUtils.getPage(content.get(), pageable, countQuery::fetchOne);
    }


    private BooleanExpression keywordCondition(String condition , String keyword) {

        // condition, keyword가 null, "", 빈 문자열이면  null 반환
        // 기본 페이지에서는 condition이 null로 설정 되도록.
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(keyword)) {
            return null;
        }

        // condition에 따라 조건 컬럼 검색.
        return switch (condition) {
            case "NICKNAME" -> user.nickname.contains(keyword);
            case "EMAIL" -> user.email.contains(keyword);
            case "PHONE" -> user.phone.contains(keyword);
            case "NAME" -> user.userName.contains(keyword);

            default -> throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
        };
    }

    //회원 등급 조건 조회.
    private BooleanExpression roleCondition(String role){

        return switch (role) {

            //모든 회원을 보여줄 경우 SUPER_ADMIN은 빼고 보여줌.
            case "ALL" -> user.role.ne(Role.SUPER_ADMIN);
            case "ADMIN" -> user.role.eq(Role.TRADER_ADMIN).or(user.role.eq(Role.INVESTOR_ADMIN));
            case "TRADER" -> user.role.eq(Role.TRADER_ADMIN).or(user.role.eq(Role.TRADER));
            case "INVESTOR" -> user.role.eq(Role.INVESTOR_ADMIN).or(user.role.eq(Role.INVESTOR));

            // 아무런 값에도 맞지않는 값이 들어왔을 경우 error 보내기. - 회원 정보 보호.
            default -> throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
        };
    }


    //최신 생성순.
    private OrderSpecifier<?> orderByLatest(){

        //userId 는 시간순으로 정해짐. 인덱스
        return new OrderSpecifier<>(Order.DESC, QUser.user.userId);
    }

    //전화번호 중복검사
    @Override
    public boolean existsByPhone(String phone) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .where(user.phone.eq(phone))
                .fetchFirst() == null;
    }

    //닉네임 중복검사
    @Override
    public boolean existsByNickname(String nickname) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .where(user.nickname.eq(nickname))
                .fetchFirst() == null;
    }

    //이메일 중복검사
    @Override
    public boolean existsByEmail(String email) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .where(user.email.eq(email))
                .fetchFirst() == null;
    }

    @Override
    public Optional<UserProfileDto> findByPhoneUserInfo(String phone) {
        QUser user = QUser.user;

        return Optional.ofNullable(queryFactory.from(user)
                .select(Projections.fields(UserProfileDto.class,
                        user.userId, user.userName, user.nickname, user.email, user.imageUrl, user.phone,
                        user.infoAgreement))
                .where(user.phone.eq(phone))
                .fetchOne());
    }

    @Override
    public Optional<UserProfileDto> findByNicknameUserInfo(String nickname) {
        QUser user = QUser.user;

        return Optional.ofNullable(queryFactory.from(user)
                .select(Projections.fields(UserProfileDto.class,
                        user.userId, user.userName, user.nickname, user.email, user.imageUrl, user.phone,
                        user.infoAgreement))
                .where(user.nickname.eq(nickname))
                .fetchOne());
    }
}