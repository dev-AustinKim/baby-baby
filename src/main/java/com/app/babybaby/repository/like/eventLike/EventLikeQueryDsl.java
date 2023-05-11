package com.app.babybaby.repository.like.eventLike;

import com.app.babybaby.entity.board.event.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface EventLikeQueryDsl {
    //    회원이 좋아요 누른 이벤트 게시글 조회
    public Slice<Event> findAllByMemberLikesWithPaging_QueryDSL(Pageable pageable, Long memberId);
}
