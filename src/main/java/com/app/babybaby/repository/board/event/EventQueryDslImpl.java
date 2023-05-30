package com.app.babybaby.repository.board.event;

import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.board.event.QEvent;
import com.app.babybaby.entity.board.parentsBoard.ParentsBoard;
import com.app.babybaby.entity.guideSchedule.GuideSchedule;
import com.app.babybaby.entity.guideSchedule.QGuideSchedule;
import com.app.babybaby.entity.like.eventLike.QEventLike;
import com.app.babybaby.entity.member.Kid;
import com.app.babybaby.entity.member.Member;
import com.app.babybaby.entity.member.QMember;
import com.app.babybaby.entity.purchase.coupon.QCoupon;
import com.app.babybaby.entity.purchase.purchase.Purchase;
import com.app.babybaby.entity.purchase.purchase.QPurchase;
import com.app.babybaby.search.admin.AdminEventSearch;
import com.app.babybaby.search.board.parentsBoard.EventBoardSearch;
import com.app.babybaby.type.CategoryType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.app.babybaby.entity.board.event.QEvent.event;
import static com.app.babybaby.entity.board.nowKids.QNowKids.nowKids;
import static com.app.babybaby.entity.board.parentsBoard.QParentsBoard.parentsBoard;
import static com.app.babybaby.entity.guideSchedule.QGuideSchedule.guideSchedule;
import static com.app.babybaby.entity.like.eventLike.QEventLike.eventLike;
import static com.app.babybaby.entity.member.QMember.member;
import static com.app.babybaby.entity.purchase.coupon.QCoupon.coupon;
import static com.app.babybaby.entity.purchase.purchase.QPurchase.purchase;

@RequiredArgsConstructor
public class EventQueryDslImpl implements EventQueryDsl {
    private final JPAQueryFactory query;


    //    이벤트 게시판 목록
    //    이벤트 게시판 검색 페이징
    @Override
    public Slice<Event> findEventListWithPaging_QueryDSL(EventBoardSearch eventBoardSearch,Pageable pageable) {
        LocalDateTime currentTime = LocalDateTime.now();
        if(eventBoardSearch.getCategoryType() == CategoryType.ALL){
            eventBoardSearch.setCategoryType(null);
        }

        BooleanExpression eventTitleContains = eventBoardSearch.getBoardTitle() == null ? null : event.boardTitle.contains(eventBoardSearch.getBoardTitle());
        BooleanExpression eventContentContains = eventBoardSearch.getBoardContent() == null ? null : event.boardContent.contains(eventBoardSearch.getBoardContent());
        BooleanExpression eventCategoryContains = eventBoardSearch.getCategoryType() == null ? null : event.category.eq(eventBoardSearch.getCategoryType());

        JPAQuery<Event> events = query.select(event)
                .from(event)
                .join(event.company).fetchJoin()
                .leftJoin(event.eventFiles).fetchJoin();

        if (eventTitleContains != null) {
            events.where(eventTitleContains);
        }

        if (eventContentContains != null) {
            events.where(eventContentContains);
        }

        if (eventCategoryContains != null) {
            events.where(eventCategoryContains);
        }

        List<Event> result = events.orderBy(event.calendar.startDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(
                        (event.calendar.startDate.before(currentTime))
                        .and(event.calendar.endDate.after(currentTime)))
                .fetch();

        boolean hasNext = false;
        if (result.size() > pageable.getPageSize()) {
            hasNext = true;
            result.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }

//    내가쓴 이벤트 게시글
    @Override
    public Slice<Event> findMemberIdByEventListWithPaging_QueryDSL(Long memberId,Pageable pageable) {
        List<Event> events = query.selectDistinct(event)
                .from(event)
                .where(event.company.id.eq(memberId))
                .join(event.company)
                .join(event.eventFiles)
                .orderBy(event.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        boolean hasNext = false;
        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (events.size() > pageable.getPageSize()) {
            hasNext = true;
            events.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(events, pageable, hasNext);
    }


    //    일단 이벤트 게시판 상세
    @Override
    public Optional<Event> findEventById_QueryDSL(Long eventId) {
        return Optional.ofNullable(
                query.select(event)
                        .from(event)
                        .join(event.company)
                        .fetchJoin()
                        .where(event.id.eq(eventId))
                        .fetchOne());
    }


    //    결제 상세페이지
    public Optional<Event> findEventPayById_QueryDSL(Long memberId, Long eventId) {
        return Optional.ofNullable(
                query.select(event)
                        .from(event)
                        .join(event.company)
                        .fetchJoin()
                        .join(coupon)
                        .fetchJoin()
                        .on(coupon.member.id.eq(memberId))
                        .where(event.id.eq(eventId), event.company.id.eq(memberId))
                        .fetchOne()
        );
    }

    //    내 스케쥴 ㅋ
    @Override
    public Slice<Event> findEventScheduleByMemberId_QueryDSL(Pageable pageable, Long memberId,LocalDateTime pickUpDate /*startDate*/) {
//        List<Event> events = query.selectDistinct(guideSchedule.event)
//                .from(guideSchedule)
//                .join(guideSchedule.event)
//                .join(guideSchedule.calendar)
////                .leftJoin(event.eventFiles)
//                .where(
//                        guideSchedule.member.id.eq(memberId).and(guideSchedule.calendar.startDate.gt(pickUpDate).and(guideSchedule.calendar.endDate.lt(pickUpDate)))
//                )
//                .orderBy(guideSchedule.id.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
        List<GuideSchedule> guideSchedules = query.selectDistinct(guideSchedule)
                .from(guideSchedule)
                .join(guideSchedule.event).fetchJoin()
                .join(guideSchedule.calendar).fetchJoin()
                .where(
                        guideSchedule.member.id.eq(memberId)
                )
                .orderBy(guideSchedule.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Event> events = guideSchedules.stream()
                .map(GuideSchedule::getEvent)
                .collect(Collectors.toList());

        boolean hasNext = false;
        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (events.size() > pageable.getPageSize()) {
            hasNext = true;
            events.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(events, pageable, hasNext);
    }

    // 이벤트 게시판 eventId로 맴버 정보조회
    public Member findMemberInfoByEventId_QueryDSL(Long eventId){
        return query.select(event.company)
                .from(event)
                .where(event.id.eq(eventId))
                .fetchOne();
    }
    
    
//    내가 쓴 이벤트 게시판 (기업!)
    @Override
    public Page<Event> findEventListByCompanyId_QueryDSL(Pageable pageable, Long companyId) {
            List<Event> events = query.select(event)
                    .from(event)
                    .join(event.company).fetchJoin()
                    .join(event.eventFiles).fetchJoin()
                    .where(event.company.id.eq(companyId))
                    .orderBy(event.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            Long count = query.select(event.count()).from(event).where(event.company.id.eq(companyId)).fetchOne();


            return new PageImpl<>(events, pageable, count);
        }

//        마이페이지 내가 참여한 이벤트 게시판
    @Override
    public Page<Purchase> findEventListByMemberId_QueryDSL(Pageable pageable, Long memberId, LocalDateTime startDate) {
        List<Purchase> purchases = query.selectDistinct(purchase)
                .from(purchase)
                .join(purchase.event).fetchJoin()
                .leftJoin(purchase.event.eventFiles)
                .leftJoin(purchase.event.calendar).fetchJoin()
                .where(purchase.member.id.eq(memberId).and(purchase.event.calendar.startDate.eq(startDate)))
                .orderBy(purchase.event.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(purchase.event.count()).from(purchase).where(purchase.member.id.eq(memberId)).fetchOne();


        return new PageImpl<>(purchases, pageable, count);
    }
    /* -------------------------회원 상세------------------------------------------------------ */
    public List<Event> findAllUpcommingEvents_QueryDSL(Long memberId, Pageable pageable) {
        LocalDateTime currentTime = LocalDateTime.now();

        return query.selectFrom(event)
                .where(event.company.id.eq(memberId)
                        .and(event.calendar.startDate.after(currentTime))
                        .and(event.calendar.endDate.after(currentTime)))
                .orderBy(event.calendar.endDate.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }
//  [부모님 마당]진행 예정인 이벤트 들고오기
    public List<Event> findAllUpcommingEvents_QueryDSL(Long memberId) {
        LocalDateTime currentTime = LocalDateTime.now();

        return query.selectFrom(event)
                .where(event.company.id.eq(memberId)
                        .and(event.calendar.startDate.after(currentTime))
                        .and(event.calendar.endDate.after(currentTime)))
                .orderBy(event.calendar.endDate.asc())
                .fetch();
    }
    
    
    
    /* 이 맴버가 쓴 지나간 이벤트 가져오기 */
    public List<Event> findAllFinishedEvents_QueryDSL(Long memberId, Pageable pageable) {
        LocalDateTime currentTime = LocalDateTime.now();

        return query.selectFrom(event)
                .where(event.company.id.eq(memberId)
                        .and(event.calendar.startDate.before(currentTime))
                        .and(event.calendar.endDate.before(currentTime)))
                .orderBy(event.calendar.endDate.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }
    
    /* 이 맴버가 쓴 진행중인 이벤트 가져오기 */
    public List<Event> findAllNowEvents_QueryDSL(Long memberId, Pageable pageable) {
        LocalDateTime currentTime = LocalDateTime.now();

        return query.selectFrom(event)
                .where(event.company.id.eq(memberId)
                        .and(event.calendar.startDate.before(currentTime))
                        .and(event.calendar.endDate.after(currentTime)))
                .orderBy(event.calendar.endDate.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public Long findAllFinishedEventsCount(Long memberId) {
        LocalDateTime currentTime = LocalDateTime.now();

        return query.select(event.count())
                .from(event)
                .where(event.company.id.eq(memberId)
                        .and(event.calendar.endDate.before(currentTime)))
                .fetchOne();
    }

    public Boolean hasMoreEvents(Long companyId) {
        Long count = query.selectFrom(event)
                .where(event.company.id.eq(companyId))
                .fetch()
                .stream()
                .count();

        return count > 0L;
    }

    /* -------------------------회원 상세------------------------------------------------------ */
    



    /*--------------------------------------리뷰 --------------------------------------------- */
//    [리뷰] 내가 참석한 이벤트 목록가져오기
    public List<Event> findAllPurchasedEvents(Long memberId) {
        return query.select(event)
            .from(purchase)
            .innerJoin(purchase.event, event)
            .innerJoin(purchase.member)
            .innerJoin(purchase.event.calendar)
            .where(purchase.member.id.eq(memberId))
            .fetch();
    }

    /*--------------------------------------리뷰 --------------------------------------------- */


    // 상세보기 카테고리 최신글 5개 가져오기
    @Override
    public List<Event> find5RecentDesc(CategoryType category) {

        BooleanExpression getCategory = category == CategoryType.ALL ? null : event.category.eq(category);

        List<Event> events = query.selectDistinct(event)
                .from(event)
                .where(getCategory)
                .orderBy(event.id.desc())
                .limit(5)
                .fetch();

        return events;
    }

//    ---------------------------------------- 관리자 -------------------------------------------
//    [관리자] 놀러가요 카테고리 및 상태별 목록
    @Override
    public Page<Event> findNowKidsEvents_queryDSL(Pageable pageable, AdminEventSearch adminEventSearch, CategoryType eventCategory, String eventStatus) {
        BooleanExpression eventNameEq = adminEventSearch.getEventTitle() == null ? null : event.boardTitle.like("%" + adminEventSearch.getEventTitle() + "%");
        LocalDateTime now = LocalDateTime.now();
        QEvent event = QEvent.event;

        List<Event> foundEvent =  query.select(event)
                .from(event)
                .join(event.calendar)
                .fetchJoin()
                .leftJoin(event.eventFiles)
                .fetchJoin()
                .where(
                        eventCategory != null
                                ? event.category.eq(eventCategory)
                                .and(eventStatus.equals("전체")
                                        ? event.calendar.startDate.isNotNull()
                                        : eventStatus.equals("대기")
                                        ? event.calendar.startDate.after(now)
                                        : eventStatus.equals("진행중")
                                        ? (event.calendar.startDate.before(now)
                                        .or(event.calendar.startDate.eq(now)))
                                        .and((event.calendar.endDate.after(now))
                                                .or(event.calendar.endDate.eq(now)))
                                        : eventStatus.equals("종료")
                                        ? event.calendar.endDate.before(now)
                                        : event.category.isNotNull())
                                : event.category.isNotNull()
                                .and(eventStatus.equals("전체")
                                        ? event.calendar.startDate.isNotNull()
                                        : eventStatus.equals("대기")
                                        ? event.calendar.startDate.after(now)
                                        : eventStatus.equals("진행중")
                                        ? (event.calendar.startDate.before(now)
                                        .or(event.calendar.startDate.eq(now)))
                                        .and((event.calendar.endDate.after(now))
                                                .or(event.calendar.endDate.eq(now)))
                                        : eventStatus.equals("종료")
                                        ? event.calendar.endDate.before(now)
                                        : event.category.isNotNull())
                                .and(eventNameEq)
                )
                .orderBy(event.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(event.count())
                .from(event)
                .where(
                        eventCategory != null
                                ? event.category.eq(eventCategory)
                                .and(eventStatus.equals("전체")
                                        ? event.calendar.startDate.isNotNull()
                                        : eventStatus.equals("대기")
                                        ? event.calendar.startDate.after(now)
                                        : eventStatus.equals("진행중")
                                        ? (event.calendar.startDate.after(now)
                                        .or(event.calendar.startDate.eq(now)))
                                        .and((event.calendar.endDate.before(now))
                                        .or(event.calendar.endDate.eq(now)))
                                        : eventStatus.equals("종료")
                                        ? event.calendar.endDate.before(now)
                                        : event.category.isNotNull())
                                : event.category.isNotNull()
                                .and(eventStatus.equals("전체")
                                        ? event.calendar.startDate.isNotNull()
                                        : eventStatus.equals("대기")
                                        ? event.calendar.startDate.after(now)
                                        : eventStatus.equals("진행중")
                                        ? (event.calendar.startDate.after(now)
                                        .or(event.calendar.startDate.eq(now)))
                                        .and((event.calendar.endDate.before(now))
                                                .or(event.calendar.endDate.eq(now)))
                                        : eventStatus.equals("종료")
                                        ? event.calendar.endDate.before(now)
                                        : event.category.isNotNull())
                                .and(eventNameEq)
                )
                .fetchOne();

        return new PageImpl<>(foundEvent, pageable, count);


    }

//    [관리자] 놀러가요 상세

    @Override
    public Optional<Event> findForAdminEventById_queryDSL(Long eventId) {
        return Optional.ofNullable(
                query.select(event)
                        .from(event)
                        .join(event.eventFiles)
                        .fetchJoin()
                        .where(event.id.eq(eventId))
                        .fetchJoin()
                        .fetchOne()
        );
    }
//    [관리자] 놀러가요 삭제

    @Override
    public void deleteEventByIds_queryDSL(Long eventId) {
        query.delete(event)
                .where(event.id.in(eventId))
                .execute();
    }

    @Override
    public List<Event> findNowKidsEventsList_queryDSL(Long companyId) {
        QEvent event = QEvent.event;

        return query.select(event)
                .from(event)
                .join(event.company)
                .fetchJoin()
                .where(event.company.id.eq(companyId))
                .orderBy(event.id.asc())
                .fetch();
    }

}


