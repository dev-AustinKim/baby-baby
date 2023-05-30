package com.app.babybaby.service.admin.adminMember;

import com.app.babybaby.domain.adminDTO.AdminEventDTO;
import com.app.babybaby.domain.adminDTO.AdminKidDTO;
import com.app.babybaby.domain.adminDTO.AdminMemberDTO;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.member.Kid;
import com.app.babybaby.entity.member.Member;
import com.app.babybaby.repository.board.event.EventRepository;
import com.app.babybaby.repository.member.member.MemberRepository;
import com.app.babybaby.search.admin.AdminMemberSearch;
import com.app.babybaby.type.AcceptanceType;
import com.app.babybaby.type.GuideType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminMemberServiceImpl implements AdminMemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

//  관리자 회원 목록
    @Override
    public Page<AdminMemberDTO> getAdminMemberListWithPaging(int page, AdminMemberSearch memberSearch) {
        Page<Member> members = memberRepository.findAllMemberWithSearch_queryDSL(PageRequest.of(page, 5), memberSearch);
        List<AdminMemberDTO> adminMemberDTOS = members.getContent().stream()
                .map(this::toMemberDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(adminMemberDTOS, members.getPageable(), members.getTotalElements());
    }

//  관리자 기업 목록
    @Override
    public Page<AdminMemberDTO> getAdminCompanyListWithPaging(int page, AdminMemberSearch memberSearch) {
        Page<Member> members = memberRepository.findAllCompanyWithSearch_queryDSL(PageRequest.of(page, 5), memberSearch);
        List<AdminMemberDTO> adminCompanyDTOS = members.getContent().stream()
                .map(this::toMemberDTO)
                .collect(Collectors.toList());

        List<Member> memberList = members.getContent(); // 현재 페이지의 Member 객체 목록 가져오기
        List<Long> eventCountList = new ArrayList<>();

        for (Member member : memberList) {
            Long memberId = member.getId(); // 각 Member 객체의 id 필드에 접근
            Optional<Long> eventCountOptional  = memberRepository.findCompanyOpenEventsCount_QueryDsl(memberId);
            Long eventCount = eventCountOptional.orElse(0L); // Optional이 null일 경우 기본값 0으로 설정
            eventCountList.add(eventCount);

            List<Event>companyEventList = eventRepository.findNowKidsEventsList_queryDSL(memberId);

            List<AdminEventDTO> events = companyEventList.stream()
                    .map(this::toAdminEventDTO)
                    .collect(Collectors.toList());
            adminCompanyDTOS.stream().forEach(adminCompanyDTO -> adminCompanyDTO.setCompanyEventList(events));
        }

        adminCompanyDTOS.stream()
                .forEach(adminCompanyDTO -> adminCompanyDTO.setEventCount(eventCountList.remove(0)));

        return new PageImpl<>(adminCompanyDTOS, members.getPageable(), members.getTotalElements());
    }

//  관리자 기업 상세
//    @Override
//    public List<AdminCompanyDetailDTO> getAdminCompanyDetail(Long companyId) {
//        List<Event> announcementDetail = memberRepository.findEventInfoBycompanyId_QueryDsl(companyId);
//        List<AdminCompanyDetailDTO> adminAnnouncementDetailDTOS = announcementDetail.stream()
//                .map(this::toCompanyDetailDTO)
//                .collect(Collectors.toList());
//        return adminAnnouncementDetailDTOS;
//    }

//  관리자 회원, 기업, 통솔자 삭제
    @Override
    public void deleteAdminMember(List<String> memberIds) {
        memberIds.stream().map(memberId -> Long.parseLong(memberId)).forEach(memberRepository::disableMembersByIds_queryDSL);
    }

//  관리자 기업 목록
//    @Override
//    public Page<AdminMemberDTO> getAdminCompanyListWithPaging(int page, AdminMemberSearch memberSearch) {
//        Page<Member> companys = memberRepository.findAllCompanyWithSearch_queryDSL(PageRequest.of(page, 5), memberSearch);
//        List<AdminMemberDTO> adminMemberDTOS = companys.getContent().stream()
//                .map(this::toMemberDTO)
//                .collect(Collectors.toList());
//        return new PageImpl<>(adminMemberDTOS, companys.getPageable(), companys.getTotalElements());
//    }

//    @Override
//    public Page<AdminMemberDTO> getAdminCompanyListWithPaging(int page, AdminMemberSearch adminMemberSearch) {
//        Page<Member> members = memberRepository.findAllCompanyWithSearch_queryDSL(PageRequest.of(page, 5), adminMemberSearch);
//
//        List<Member> memberList = members.getContent(); // 현재 페이지의 Member 객체 목록 가져오기
//        List<Long> eventCountList = new ArrayList<>();
//
//        for (Member member : memberList) {
//            Long memberId = member.getId(); // 각 Member 객체의 id 필드에 접근
//            Optional<Long> eventCountOptional  = memberRepository.findCompanyOpenEventsCount_QueryDsl(memberId);
//            Long eventCount = eventCountOptional.orElse(0L); // Optional이 null일 경우 기본값 0으로 설정
//            eventCountList.add(eventCount);
//        }
//
//        List<AdminMemberDTO> adminMemberDTOS = new ArrayList<>();
//        for (int i = 0; i < members.getContent().size(); i++) {
//            Member member = members.getContent().get(i);
//            AdminMemberDTO adminMemberDTO = new AdminMemberDTO();
//            adminMemberDTO.setId(member.getId());
//            adminMemberDTO.setMemberName(member.getMemberName());
//            adminMemberDTO.setMemberPhone(member.getMemberPhone());
//            adminMemberDTO.setMemberNickname(member.getMemberNickname());
//            adminMemberDTO.setMemberEmail(member.getMemberEmail());
//            if (i < eventCountList.size()) {
//                Long eventCount = eventCountList.get(i);
//                adminMemberDTO.setEventCount(eventCount);
//            }
//            adminMemberDTOS.add(adminMemberDTO);
//        }
//
//        return new PageImpl<>(adminMemberDTOS, members.getPageable(), members.getTotalElements());
//    }



//  관리자 기업 이벤트 개수
    @Override
    public Optional<Long> getCompanyOpenEventsCount(Long companyId) {
        return memberRepository.findCompanyOpenEventsCount_QueryDsl(companyId);
    }

//  통솔자 목록
    @Override
    public Page<AdminMemberDTO> getAdminGuideListWithPaging(int page, AdminMemberSearch memberSearch, GuideType guideType, AcceptanceType acceptanceType) {
        Page<Member> members = memberRepository.findAllGuideWithSearch_queryDSL(PageRequest.of(page, 5), memberSearch, guideType, acceptanceType);
        List<AdminMemberDTO> adminMemberDTOS = members.getContent().stream()
                .map(this::toMemberDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(adminMemberDTOS, members.getPageable(), members.getTotalElements());
    }

//  관리자 통솔자 승인 or 취소
    @Override
    public void acceptGuide(Long memberId, String confirm) {
        memberRepository.updateGuideStatusById_queryDSL(memberId, confirm);
    }

}
