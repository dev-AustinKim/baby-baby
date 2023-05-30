package com.app.babybaby.service.admin.adminNowKids;

import com.app.babybaby.domain.adminDTO.AdminKidDTO;
import com.app.babybaby.domain.adminDTO.AdminNowKidsDTO;
import com.app.babybaby.entity.board.nowKids.NowKids;
import com.app.babybaby.entity.member.Kid;
import com.app.babybaby.repository.board.event.EventRepository;
import com.app.babybaby.repository.board.nowKids.NowKidsRepository;
import com.app.babybaby.repository.file.nowKidsFile.NowKidsFileRepository;
import com.app.babybaby.repository.member.member.MemberRepository;
import com.app.babybaby.search.admin.AdminEventSearch;
import com.app.babybaby.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNowKidsServiceImpl implements AdminNowKidsService {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private NowKidsRepository nowKidsRepository;

    @Autowired
    private NowKidsFileRepository nowKidsFileRepository;


    @Override
    public Page<AdminNowKidsDTO> getAdminEventListWithPaging(int page, AdminEventSearch eventSearch, CategoryType categoryType, String eventStatus) {
        Page<NowKids> nowKids = nowKidsRepository.findNowKidsEvents_queryDSL(PageRequest.of(page, 5), eventSearch,categoryType, eventStatus);
        List<AdminNowKidsDTO> adminNowKidsDTOS = nowKids.getContent().stream()
                .map(this::toAdminNowKidsDTO)
                .collect(Collectors.toList());

        List<NowKids> nowKidsList = nowKids.getContent(); // 현재 페이지의 Member 객체 목록 가져오기
        List<Kid> kidList = new ArrayList<>();

        for (NowKids nowkids : nowKidsList) {
            Long guidId = nowkids.getGuide().getId(); // 각 nowkids 객체의 guidId 필드에 접근
            Long eventId = nowkids.getEvent().getId(); // 각 nowkids 객체의 eventId 필드에 접근

            log.info(guidId.toString());
            log.info(eventId.toString());
            List<Kid>kidListOfList  = nowKidsRepository.findAllKidsByEventIdAndGuideId_QueryDsl(guidId, eventId);
            List<AdminKidDTO> adminKidDTOS = kidListOfList.stream()
                    .map(this::toAdminKidDTO)
                    .collect(Collectors.toList());
            adminNowKidsDTOS.stream().forEach(adminNowKidsDTO -> adminNowKidsDTO.setAdminKidDTOS(adminKidDTOS));

        }
        log.info(adminNowKidsDTOS.toString());
        return new PageImpl<>(adminNowKidsDTOS, nowKids.getPageable(), nowKids.getTotalElements());
    }

//    @Override
//    public AdminNowKidsDTO getAdminNowKidsById(Long eventId) {
//
//        AdminNowKidsDTO adminNowKidsDTO = null;
//        if (event.isPresent() && member.isPresent()) {
//            adminNowKidsDTO = toAdminNowKidsDTO(event.get(), member.get());
//        }
//
//        return adminNowKidsDTO;
//    }

    @Override
    public void deleteAdminNowKids(List<String> nowKidsIds) {
        nowKidsIds.stream().map(nowKidsId -> Long.parseLong(nowKidsId)).forEach(nowKidsFileRepository::deleteByNowKidsId);
        nowKidsIds.stream().map(nowKidsId -> Long.parseLong(nowKidsId)).forEach(nowKidsRepository::deleteNowKidsByIds_queryDSL);
//        boardIds.stream().map(boardId -> Long.parseLong(boardId)).forEach(parentsBoardRepository::deleteAdminParentsBoardByIds_queryDSL);
    }

}
