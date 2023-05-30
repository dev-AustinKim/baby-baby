package com.app.babybaby.repository.board.nowKids;

import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.board.event.QEvent;
import com.app.babybaby.entity.board.nowKids.NowKids;
import com.app.babybaby.entity.board.nowKids.QNowKids;
import com.app.babybaby.entity.calendar.QCalendar;
import com.app.babybaby.entity.file.nowKidsFile.NowKidsFile;
import com.app.babybaby.entity.member.*;
import com.app.babybaby.search.admin.AdminEventSearch;
import com.app.babybaby.type.CategoryType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.app.babybaby.entity.board.QBoardInfo.boardInfo;
import static com.app.babybaby.entity.board.event.QEvent.event;
import static com.app.babybaby.entity.board.nowKids.QNowKids.nowKids;
import static com.app.babybaby.entity.board.parentsBoard.QParentsBoard.parentsBoard;
import static com.app.babybaby.entity.calendar.QCalendar.calendar;
import static com.app.babybaby.entity.file.nowKidsFile.QNowKidsFile.nowKidsFile;
import static com.app.babybaby.entity.member.QCrew.crew;
import static com.app.babybaby.entity.member.QGuide.guide;
import static com.app.babybaby.entity.member.QKid.kid;

@RequiredArgsConstructor
public class NowKidsQueryDslImpl implements NowKidsQueryDsl {
    private final JPAQueryFactory query;
    

    /* 통솔자의 아이디로 통솔자(User)의 모든 정보 가져오기 */
    public NowKids findNowKidsByGuideId_QueryDsl(Long guideId) {
        return query
                .selectFrom(nowKids)
                .join(nowKids.guide)
                .where(nowKids.guide.id.eq(guideId))
                .fetchOne();
    }

    /* GeneralGuide의 아이디로 그 사람이 통솔중인 이벤트 정보 가져오기 (통솔중인것은 가져오면 안됨), (통솔완료인 것도 가져오면 안됨) */
    public List<Tuple> findEventAndCalendarInfoByGuideId_QueryDsl(Long generalGuideId) {
        return query.select(event, calendar)
                .from(guide)
                .join(guide.event, event)
                .join(event.calendar, calendar)
                .where((guide.generalGuide.id.eq(generalGuideId)).or(guide.adminGuide.id.eq(generalGuideId)))
                .where()
                .fetch();
    }


    /* AdminGuide 아이디로 그 사람이 통솔중인 이벤트 정보 가져오기 (통솔중인것은 가져오면 안됨), (통솔완료인 것도 가져오면 안됨) */
    public List<Event> findEventInfoByAdminGuideId_QueryDsl(Long adminGuideId) {
        return query.select(guide.event)
                .from(guide)
                .join(guide.event)
                .where(guide.adminGuide.id.eq(adminGuideId))
                .fetch();
    }

    /* 세션에 있는 아이디로 참여자 목록 가져오기 */
    public List<Kid> findAllKidsByGeneralGuideId_QueryDsl(Long sessionId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(guide.generalGuide.id.eq(sessionId));
        builder.or(guide.adminGuide.id.eq(sessionId));

        return query.select(kid)
                .from(guide)
                .join(guide.crews, crew)
                .join(crew.kid, kid)
                .where(builder)
                .fetch();
    }


    /* 통솔자의 모든 정보(User)만 뿌려주기 */
    public List<Member> findGuideBoard_QueryDsl() {
        return query.select(nowKids)
                .from(nowKids)
                .join(nowKids.guide)
                .fetchJoin()
                .fetch()
                .stream()
                .map(NowKids::getGuide)
                .collect(Collectors.toList());
    }

    /* pageNo는 0부터 시작 */
    public List<Member> findGuideBoardWithPaging_QueryDsl(int pageNo, int pageSize) {
        return query.selectFrom(nowKids)
                .orderBy(nowKids.id.desc())
                .join(nowKids.guide)
                .fetchJoin()
                .offset(pageNo * pageSize)
                .limit(pageSize)
                .fetch()
                .stream()
                .map(NowKids::getGuide)
                .collect(Collectors.toList());
    }

    /* 해당 보드의 모든 파일 가져오기  수정필요*/
    public List<NowKidsFile> findAllFileNowKidsById_QueryDsl(Long nowKidsId) {
        return query.selectFrom(nowKidsFile)
                .where(nowKidsFile.nowKids.id.eq(nowKidsId))
                .orderBy(nowKidsFile.id.desc())
                .fetch();
    }


    public List<Kid> findAllKidsByEventIdAndGuideId_QueryDsl(Long guideId, Long eventId) {
        return query.select(crew.kid)
                .from(crew)
                .join(crew.guide)
                .join(crew.kid)
                .where(crew.guide.generalGuide.id.eq(guideId).or(crew.guide.adminGuide.id.eq(guideId))
                        .and(crew.guide.event.id.eq(eventId)))
                .fetch();
    }

    public LocalDateTime findUpdateTime_QueryDsl(Long nowKidsId){
        return query.select(boardInfo.updateDate)
                .from(boardInfo)
                .where(boardInfo.id.eq(nowKidsId))
                .fetchOne();
    }

    /* list페이지를 위한 최근 올린 8명 가져오기 */
    public List<Member> find8AuthorDesc() {
        QNowKids subNowKids = new QNowKids("subNowKids");
        List<Member> subQueryResult = query.select(subNowKids.guide)
                .from(subNowKids)
                .orderBy(subNowKids.id.desc())
                .limit(8)
                .fetch();
        return query.selectDistinct(nowKids.guide)
                .from(nowKids)
                .where(nowKids.guide.in(subQueryResult))
                .fetch();
    }

    @Override
    public List<NowKids> findNowKidsGuideByMemberId(Long memberId) {
        return null;
    }



    @Override
    public List<NowKids> find5RecentDesc() {
        List<NowKids> nowKidz =
                query.select(nowKids)
                        .from(nowKids)
                        .join(nowKids.event)
                        .join(nowKids.nowKidsFiles)
                        .fetchJoin()
                        .orderBy(nowKids.id.desc())
                        .limit(5)
                        .fetch();

        return nowKidz;
    }

//    --------------------------------관리자 ---------------------------------------
// [관리자페이지] 지금 우리 아이들은 카테고리별 전체 목록 조회
@Override
public Page<NowKids> findNowKidsEvents_queryDSL(Pageable pageable, AdminEventSearch adminEventSearch, CategoryType eventCategory, String eventStatus) {
    BooleanExpression eventNameEq = adminEventSearch.getEventTitle() == null ? null : nowKids.event.boardTitle.like("%" + adminEventSearch.getEventTitle() + "%");

    LocalDateTime now = LocalDateTime.now();
    QNowKids qNowKids = nowKids;
    QEvent qEvent = event;
    QGuide qGuide = guide;
    QCalendar qCalendar = calendar;
    QKid qkid = kid;

    List<NowKids> foundNowKIds = query.select(nowKids)
            .from(nowKids)
            .join(nowKids.event)
            .join(nowKids.guide)
            .join(nowKids.event.calendar)
            .leftJoin(nowKids.nowKidsFiles)
            .fetchJoin()
            .where(
                    eventCategory != null
                            ? nowKids.event.category.eq(eventCategory)
                            .and(eventStatus.equals("전체")
                                    ? nowKids.event.calendar.startDate.isNotNull()
                                    : eventStatus.equals("대기")
                                    ? nowKids.event.calendar.startDate.after(now)
                                    : eventStatus.equals("진행중")
                                    ? (nowKids.event.calendar.startDate.before(now)
                                    .or(nowKids.event.calendar.startDate.eq(now)))
                                    .and((nowKids.event.calendar.endDate.after(now))
                                            .or(nowKids.event.calendar.endDate.eq(now)))
                                    : eventStatus.equals("종료")
                                    ? nowKids.event.calendar.endDate.before(now)
                                    : nowKids.event.category.isNotNull())
                            : nowKids.event.category.isNotNull()
                            .and(eventStatus.equals("전체")
                                    ? nowKids.event.calendar.startDate.isNotNull()
                                    : eventStatus.equals("대기")
                                    ? nowKids.event.calendar.startDate.after(now)
                                    : eventStatus.equals("진행중")
                                    ? (nowKids.event.calendar.startDate.before(now)
                                    .or(nowKids.event.calendar.startDate.eq(now)))
                                    .and((nowKids.event.calendar.endDate.after(now))
                                            .or(nowKids.event.calendar.endDate.eq(now)))
                                    : eventStatus.equals("종료")
                                    ? nowKids.event.calendar.endDate.before(now)
                                    : nowKids.event.category.isNotNull())
                            .and(eventNameEq)
            )
            .orderBy(nowKids.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Long count = query.select(nowKids.count())
            .from(nowKids)
            .where(
                    eventCategory != null
                            ? nowKids.event.category.eq(eventCategory)
                            .and(eventStatus.equals("전체")
                                    ? nowKids.event.calendar.startDate.isNotNull()
                                    : eventStatus.equals("대기")
                                    ? nowKids.event.calendar.startDate.after(now)
                                    : eventStatus.equals("진행중")
                                    ? (nowKids.event.calendar.startDate.before(now)
                                    .or(nowKids.event.calendar.startDate.eq(now)))
                                    .and((nowKids.event.calendar.endDate.after(now))
                                            .or(nowKids.event.calendar.endDate.eq(now)))
                                    : eventStatus.equals("종료")
                                    ? nowKids.event.calendar.endDate.before(now)
                                    : nowKids.event.category.isNotNull())
                            : nowKids.event.category.isNotNull()
                            .and(eventStatus.equals("전체")
                                    ? nowKids.event.calendar.startDate.isNotNull()
                                    : eventStatus.equals("대기")
                                    ? nowKids.event.calendar.startDate.after(now)
                                    : eventStatus.equals("진행중")
                                    ? (nowKids.event.calendar.startDate.before(now)
                                    .or(nowKids.event.calendar.startDate.eq(now)))
                                    .and((nowKids.event.calendar.endDate.after(now))
                                            .or(nowKids.event.calendar.endDate.eq(now)))
                                    : eventStatus.equals("종료")
                                    ? nowKids.event.calendar.endDate.before(now)
                                    : nowKids.event.category.isNotNull())
                            .and(eventNameEq)
            )
            .fetchOne();

    return new PageImpl<>(foundNowKIds, pageable, count);
}

    //  지금 우리 아이들은 상세
    @Override
    public Optional<NowKids> findNowKidsById_queryDSL(Long nowKidsId) {
        return Optional.ofNullable(
                query.select(nowKids)
                        .from(nowKids)
                        .join(nowKids.event)
                        .join(nowKids.nowKidsFiles)
                        .join(nowKids.guide)
                        .join(parentsBoard.parentsBoardReplies)
                        .where(nowKids.id.eq(nowKidsId))
                        .fetchJoin()
                        .orderBy(parentsBoard.event.id.asc())
                        .fetchOne()
        );
    }

    //  지금 우리 아이들은 삭제
    @Override
    public void deleteNowKidsByIds_queryDSL(Long nowkidsIds) {
        query.delete(nowKids)
                .where(nowKids.id.in(nowkidsIds))
                .execute();
    }


    @Override
    public LocalDate findParticipateDate_queryDSL(Long kidId) {
        return query.select(crew.eventRegisterDate)
                .from(crew)
                .join(crew.kid)
                .where(crew.kid.id.eq(kidId))
                .fetchOne();
    }

}
