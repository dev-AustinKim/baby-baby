package com.app.babybaby.service.admin.adminMember;

import com.app.babybaby.domain.adminDTO.AdminCompanyDetailDTO;
import com.app.babybaby.domain.adminDTO.AdminEventDTO;
import com.app.babybaby.domain.adminDTO.AdminMemberDTO;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.member.Member;
import com.app.babybaby.search.admin.AdminMemberSearch;
import com.app.babybaby.type.AcceptanceType;
import com.app.babybaby.type.GuideType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public interface AdminMemberService {


//    관리자 회원 목록
    public Page<AdminMemberDTO> getAdminMemberListWithPaging(int page, AdminMemberSearch memberSearch);

//    관리자 회원, 기업, 통솔자 삭제하기
    public void deleteAdminMember(List<String> memberIds);

//    관리자 기업 목록
    public Page<AdminMemberDTO> getAdminCompanyListWithPaging(int page, AdminMemberSearch memberSearch);

//    관리자 기업 이벤트 상세 보기
//public Page<AdminMemberDTO> getAdminCompanyEventListWithPaging(int page, Long companyId);

//    관리자 기업 이벤트 개수
    public Optional<Long> getCompanyOpenEventsCount(Long companyId);

//    관리자 통솔자 목록
    public Page<AdminMemberDTO> getAdminGuideListWithPaging(int page, AdminMemberSearch memberSearch, GuideType guideType, AcceptanceType acceptanceType);

//    관리자 통솔자 승인 or 취소
    public void acceptGuide(Long memberId, String confirm);

//    default AdminMemberDTO toMemberWithEventCountDTO(Member member, Long eventCount){
//
//
//        return AdminMemberDTO.builder()
//                .id(member.getId())
//                .memberName(member.getMemberName())
//                .memberNickname(member.getMemberNickname())
//                .memberPhone(member.getMemberPhone())
//                .memberEmail(member.getMemberEmail())
//                .memberRegisterDate(member.getMemberRegisterDate())
//                .memberAddress(member.getMemberAddress().getAddress())
//                .memberAddressDetail(member.getMemberAddress().getAddressDetail())
//                .memberAddressSubDetail(member.getMemberAddress().getAddressSubDetail())
//                .memberProfileOriginalName(member.getMemberProfileOriginalName())
//                .memberProfilePath(member.getMemberProfilePath())
//                .memberProfileUUID(member.getMemberProfileUUID())
//                .memberType(member.getMemberType())
//                .memberRole(member.getMemberRole())
//                .memberGuideStatus(member.getMemberGuideStatus())
//                .memberSleep(member.getMemberSleep())
//                .memberGuideType(member.getMemberGuideType())
//                .memberGuideInterest(member.getMemberGuideInterest())
//                .memberFileOriginalName(member.getMemberFileOriginalName())
//                .memberFilePath(member.getMemberFilePath())
//                .memberFileUUID(member.getMemberFileUUID())
//                .eventCount(eventCount)
//                .build();
//    }


    default AdminMemberDTO toMemberDTO(Member member){


        return AdminMemberDTO.builder()
                .id(member.getId())
                .memberName(member.getMemberName())
                .memberNickname(member.getMemberNickname())
                .memberPhone(member.getMemberPhone())
                .memberEmail(member.getMemberEmail())
                .memberRegisterDate(member.getMemberRegisterDate())
                .memberAddress(member.getMemberAddress().getAddress())
                .memberAddressDetail(member.getMemberAddress().getAddressDetail())
                .memberAddressSubDetail(member.getMemberAddress().getAddressSubDetail())
                .memberProfileOriginalName(member.getMemberProfileOriginalName())
                .memberProfilePath(member.getMemberProfilePath())
                .memberProfileUUID(member.getMemberProfileUUID())
                .memberType(member.getMemberType())
                .memberRole(member.getMemberRole())
                .memberGuideStatus(member.getMemberGuideStatus())
                .memberSleep(member.getMemberSleep())
                .memberGuideType(member.getMemberGuideType())
                .memberGuideInterest(member.getMemberGuideInterest())
                .memberFileOriginalName(member.getMemberFileOriginalName())
                .memberFilePath(member.getMemberFilePath())
                .memberFileUUID(member.getMemberFileUUID())
                .build();
    }




    //  공지사항 DTO로 바꾸기
    default AdminCompanyDetailDTO toCompanyDetailDTO(Event event){


        return AdminCompanyDetailDTO.builder()
                .id(event.getId())
                .boardTitle(event.getBoardTitle())
                .category(event.getCategory())
                .eventLocation(event.getEventLocation())
                .startDate(event.getCalendar().getStartDate())
                .endDate(event.getCalendar().getEndDate())
                .eventRecruitCount(event.getEventRecruitCount())
                .memberName(event.getCompany().getMemberName())
                .profileOriginalName(event.getCompany().getMemberProfileOriginalName())
                .profilePath(event.getCompany().getMemberProfilePath())
                .profileUUID(event.getCompany().getMemberProfileUUID())
                .build();
    }

    default AdminEventDTO toAdminEventDTO(Event event){
        return AdminEventDTO.builder()
                .id(event.getId())
                .memberName(event.getCompany().getMemberName())
                .category(event.getCategory())
                .boardTitle(event.getBoardTitle())
                .eventAddress(event.getEventLocation().getAddress())
                .eventAddressDetail(event.getEventLocation().getAddressDetail())
                .eventAddressSubDetail(event.getEventLocation().getAddressSubDetail())
                .startDate(event.getCalendar().getStartDate())
                .endDate(event.getCalendar().getEndDate())
                .eventRecruitCount(event.getEventRecruitCount())
                .build();
    }
}
