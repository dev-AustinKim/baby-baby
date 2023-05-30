package com.app.babybaby.service.admin.adminEvent;

import com.app.babybaby.domain.adminDTO.AdminEventDTO;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.repository.board.event.EventRepository;
import com.app.babybaby.repository.file.eventFile.EventFileRepository;
import com.app.babybaby.repository.member.member.MemberRepository;
import com.app.babybaby.search.admin.AdminEventSearch;
import com.app.babybaby.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventFileRepository eventFileRepository;

    //  관리자 이벤트 목록
    @Override
    public Page<AdminEventDTO> getAdminEventListWithPaging(int page, AdminEventSearch eventSearch, CategoryType eventCategory, String eventStatus) {
        Page<Event> events = eventRepository.findNowKidsEvents_queryDSL(PageRequest.of(page, 5), eventSearch, eventCategory, eventStatus);
        List<AdminEventDTO> adminEventDTOS = events.getContent().stream()
                .map(this::toAdminEventDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(adminEventDTOS, events.getPageable(), events.getTotalElements());
    }


    //  관리자 이벤트 삭제
    @Override
    public void deleteAdminEvent(List<String> eventIds) {
        eventIds.stream().map(eventId -> Long.parseLong(eventId)).forEach(eventFileRepository::deleteByEventId);
        eventIds.stream().map(eventId -> Long.parseLong(eventId)).forEach(eventRepository::deleteEventByIds_queryDSL);
    }

    //    관리자 기업 이벤트 상세 보기
//    @Override
//    public Page<AdminEventDTO> getAdminCompanyEventListWithPaging(int page, Long companyId) {
//        Page<Event> nowKidsEvents = eventRepository.findNowKidsEventsList_queryDSL(PageRequest.of(page, 5), companyId);
//        List<AdminEventDTO> adminNowKidsEventDTOS = nowKidsEvents.getContent().stream()
//                .map(this::toAdminEventDTO)
//                .collect(Collectors.toList());
//        return new PageImpl<>(adminNowKidsEventDTOS, nowKidsEvents.getPageable(), nowKidsEvents.getTotalElements());
//    }
}
