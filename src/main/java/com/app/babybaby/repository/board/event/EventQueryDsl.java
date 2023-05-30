package com.app.babybaby.repository.board.event;

import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.member.Kid;
import com.app.babybaby.entity.member.Member;
import com.app.babybaby.entity.purchase.purchase.Purchase;
import com.app.babybaby.search.admin.AdminEventSearch;
import com.app.babybaby.search.board.parentsBoard.EventBoardSearch;
import com.app.babybaby.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventQueryDsl {
    //    이벤트 게시판 목록(무한스크롤)
    public Slice<Event> findEventListWithPaging_QueryDSL(EventBoardSearch eventBoardSearch,Pageable pageable);
//    내가쓴 이벤트 게시판 [기업]
    public Slice<Event> findMemberIdByEventListWithPaging_QueryDSL(Long memberId, Pageable pageable);
    //    이벤트 게시판 상세 페이지
    public Optional<Event> findEventById_QueryDSL(Long id);
    //     결제 상세 페이지
    public Optional<Event> findEventPayById_QueryDSL(Long memberId, Long eventId);
    //     내 스케쥴 ㅋ
    public Slice<Event> findEventScheduleByMemberId_QueryDSL(Pageable pageable, Long memberId, LocalDateTime startDate);
    // 이벤트 게시판 eventId로 맴버 정보조회
    public Member findMemberInfoByEventId_QueryDSL(Long eventId);
//    기업 이벤트 게시글 조회
    public Page<Event> findEventListByCompanyId_QueryDSL(Pageable pageable, Long companyId);

//    내가 참여한 이벤트 게시판
    public Page<Purchase> findEventListByMemberId_QueryDSL(Pageable pageable, Long memberId , LocalDateTime startDate);

    /* ---------------------------------회원 상세--------------------------------------------------- */
    /* 회사의 진행 예정 이벤트 */
    public List<Event> findAllUpcommingEvents_QueryDSL(Long memberId, Pageable pageable);

    public List<Event> findAllUpcommingEvents_QueryDSL(Long memberId);
    
    /* 회사의 진행 진행종료 이벤트 */
    public List<Event> findAllFinishedEvents_QueryDSL(Long memberId, Pageable pageable);

    /* 회사의 진행 중 이벤트 */
    public List<Event> findAllNowEvents_QueryDSL(Long memberId, Pageable pageable);

    /* 회사의 진행 끝 이벤트 총 갯수 */
    public Long findAllFinishedEventsCount(Long memberId);

    /* 회사가 쓴 진행 끝 이벤트가 더 있는지 */
    public Boolean hasMoreEvents(Long companyId);

    /* ---------------------------------회원 상세--------------------------------------------------- */
//    [리뷰] 내가 결제한 이벤트 정보들
    public List<Event> findAllPurchasedEvents(Long memberId);

    /* ---------------------------------메인-------------------------------------------------- */

    //    상세보기 카테고리 최신글 5개 가져오기
    public List<Event> find5RecentDesc(CategoryType category);

//    --------------------------------- 관리자 ----------------------------------------------------
//   [관리자] 놀러가요 카테고리 및 상태별 게시글 목록 조회
    public Page<Event> findNowKidsEvents_queryDSL(Pageable pageable, AdminEventSearch adminEventSearch, CategoryType eventCategory, String eventStatus);
    //   [관리자] 놀러가요 상세보기
    public Optional<Event> findForAdminEventById_queryDSL(Long eventId);
    //   [관리자] 놀러가요 삭제하기
    public void deleteEventByIds_queryDSL(Long eventId);

    //   [관리자] 기업상세 이벤트 페이징 목록
    public List<Event> findNowKidsEventsList_queryDSL(Long companyId);

}
