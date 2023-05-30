package com.app.babybaby.service.admin.adminEvent;

import com.app.babybaby.domain.adminDTO.AdminEventDTO;
import com.app.babybaby.domain.fileDTO.eventFileDTO.EventFileDTO;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.file.eventFile.EventFile;
import com.app.babybaby.search.admin.AdminEventSearch;
import com.app.babybaby.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public interface AdminEventService {
    //    관리자 이벤트 목록
    public Page<AdminEventDTO> getAdminEventListWithPaging(int page, AdminEventSearch eventSearch, CategoryType eventCategory, String eventStatus);

    //    관리자 이벤트 삭제하기
    public void deleteAdminEvent(List<String> eventIds);

    //    관리자 기업 이벤트 상세 보기
//    public Page<AdminEventDTO> getAdminCompanyEventListWithPaging(int page, Long companyId);

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
                .eventFileDTOS(event.getEventFiles().stream().map(eventFile -> eventFileToDTO(eventFile)).collect(Collectors.toList()))
                .build();
    }

    default EventFileDTO eventFileToDTO(EventFile eventFile){
        return EventFileDTO.builder()
                .fileOriginalName(eventFile.getFileOriginalName())
                .filePath(eventFile.getFilePath())
                .fileStatus(eventFile.getFileStatus())
                .fileUUID(eventFile.getFileUUID())
                .build();
    }
}
