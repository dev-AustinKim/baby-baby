package com.app.babybaby.controller.adminController;

import com.app.babybaby.domain.adminDTO.*;
import com.app.babybaby.domain.fileDTO.announcementFileDTO.AnnouncementFileDTO;
import com.app.babybaby.entity.board.announcement.Announcement;
import com.app.babybaby.entity.board.ask.Ask;
import com.app.babybaby.entity.board.ask.AskAnswer;
import com.app.babybaby.entity.member.Member;
import com.app.babybaby.repository.board.announcement.AnnouncementRepository;
import com.app.babybaby.repository.board.ask.AskAnswerRepository;
import com.app.babybaby.repository.board.ask.AskRepository;
import com.app.babybaby.repository.board.nowKids.NowKidsRepository;
import com.app.babybaby.repository.member.member.MemberRepository;
import com.app.babybaby.search.admin.*;
import com.app.babybaby.service.admin.adminAnnouncement.AdminAnnouncementService;
import com.app.babybaby.service.admin.adminAskService.AdminAskService;
import com.app.babybaby.service.admin.adminEvent.AdminEventService;
import com.app.babybaby.service.admin.adminKidService.AdminKidService;
import com.app.babybaby.service.admin.adminMember.AdminMemberService;
import com.app.babybaby.service.admin.adminNowKids.AdminNowKidsService;
import com.app.babybaby.service.admin.adminParentsBoard.AdminParentsBoardService;
import com.app.babybaby.service.admin.adminParentsBoardReply.AdminParentsBoardReplyService;
import com.app.babybaby.service.admin.adminReviewBoard.AdminReviewBoardSerivce;
import com.app.babybaby.service.admin.adminReviewBoardReply.AdminReviewBoardReplyService;
import com.app.babybaby.service.file.announcementFile.AnnouncementFileService;
import com.app.babybaby.type.AcceptanceType;
import com.app.babybaby.type.CategoryType;
import com.app.babybaby.type.GuideType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/*")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminMemberService adminMemberService;
    private final AdminParentsBoardReplyService adminParentsBoardReplyService;
    private final AdminReviewBoardReplyService adminReviewBoardReplyService;
    private final AdminAnnouncementService adminAnnouncementService;
    private final AdminAskService adminAskService;
    private final AnnouncementFileService announcementFileService;
    private final AdminNowKidsService adminNowKidsService;
    private final AdminParentsBoardService adminParentsBoardService;
    private final AdminReviewBoardSerivce adminReviewBoardSerivce;
    private final AdminEventService adminEventService;
    private final AdminKidService adminKidService;
    private final MemberRepository memberRepository;
    private final AskRepository askRepository;
    private final AskAnswerRepository askAnswerRepository;
    private final AnnouncementRepository announcementRepository;
    private final NowKidsRepository nowKidsRepository;


    //    회원 목록
    @GetMapping("memberList/{page}")
    @ResponseBody
    public Page<AdminMemberDTO> getMember(@PathVariable("page") int page, AdminMemberSearch adminMemberSearch){
        Page<AdminMemberDTO> adminMembers = adminMemberService.getAdminMemberListWithPaging(page -1, adminMemberSearch);
        return adminMembers;
    }

    //    회원 삭제
    @PatchMapping("member/delete")
    @ResponseBody
    public void memberDelete(@RequestParam("checkedIds[]") List<String> checkIds){
        adminMemberService.deleteAdminMember(checkIds);
    }

    //    기업 목록
    @GetMapping("companyList/{page}")
    @ResponseBody
    public Page<AdminMemberDTO> getCompany(@PathVariable("page") int page, AdminMemberSearch adminMemberSearch){
        Page<AdminMemberDTO> adminCompanies = adminMemberService.getAdminCompanyListWithPaging(page -1, adminMemberSearch);
        return adminCompanies;
    }

    //    기업 이벤트 목록
//    @GetMapping("company-event-List/{page}")
//    @ResponseBody
//    public Page<AdminEventDTO> getCompanyEvents(@PathVariable("page") int page, Long companyId){
//        Page<AdminEventDTO> adminCompanyEvents = adminEventService.getAdminCompanyEventListWithPaging(page -1, companyId);
//        return adminCompanyEvents;
//    }


    //    관리자 기업 상세보기
//    @GetMapping("announcementDetail")
//    @ResponseBody
//    public List<AdminCompanyDetailDTO> getCompanyDetail(){
//        List<AdminCompanyDetailDTO> adminCompanyDetails = adminAnnouncementService.getAdminAnnouncementDetail();
//        return adminAnnouncementDetails;
//    }

    //    지금우리아이들은 목록
    @GetMapping("nowKidsList/{page}")
    @ResponseBody
    public Page<AdminNowKidsDTO> getNowKids(@PathVariable("page") int page, AdminEventSearch adminEventSearch, CategoryType categoryType, String eventStatus){
        Page<AdminNowKidsDTO> adminNowKids = adminNowKidsService.getAdminEventListWithPaging(page -1, adminEventSearch, categoryType, eventStatus);
        return adminNowKids;
    }

    //  지금우리아이들은 아이들 명단
    @GetMapping("kid-list")
    @ResponseBody
    public  List<AdminKidDTO> getAllKids(Long guideId, Long eventId) {
//        log.info("eventID는 : " + eventId.toString());
        log.info("Kids들은" + nowKidsRepository.findAllKidsByEventIdAndGuideId_QueryDsl(guideId, eventId).toString());

        List<AdminKidDTO> kids = adminKidService.findAllKidsByGuideIdAndEventId(guideId, eventId);

        return kids;
    }

    //    부모님마당 목록
    @GetMapping("parentsYardList/{page}")
    @ResponseBody
    public Page<AdminParentsBoardDTO> getParentsBoard(@PathVariable("page") int page, AdminParentsBoardSearch adminParentsBoardSearch ){
        Page<AdminParentsBoardDTO> adminParentsBoards = adminParentsBoardService.getAdminParentsBoardListWithPaging(page -1, adminParentsBoardSearch);
        return adminParentsBoards;
    }

    //    부모님마당 삭제
    @DeleteMapping("parentsBoard/delete")
    @ResponseBody
    public void parentsBoardDelete(@RequestParam("checkedIds[]") List<String> checkIds){
        adminParentsBoardService.deleteAdminParentsBoard(checkIds);
    }
    //    후기 목록, 상세
    @GetMapping("reviewList/{page}")
    @ResponseBody
    public Page<AdminReviewDTO> getEventBoard(@PathVariable("page") int page, AdminReviewSearch adminReviewSearch, CategoryType categoryType){
        Page<AdminReviewDTO> adminReviewBoards = adminReviewBoardSerivce.getAdminReviewListWithPaging(page -1, adminReviewSearch,categoryType);
        return adminReviewBoards;
    }

    @DeleteMapping("reviewBoard/delete")
    @ResponseBody
    public void reviewBoardDelete(@RequestParam("checkedIds[]") List<String> checkIds){
        adminReviewBoardSerivce.deleteAdminReview(checkIds);
    }

    //    이벤트 목록, 상세
    @GetMapping("eventList/{page}")
    @ResponseBody
    public Page<AdminEventDTO> getEventBoard(@PathVariable("page") int page, AdminEventSearch adminEventSearch, CategoryType categoryType, String eventStatus){
        Page<AdminEventDTO> adminEventBoards = adminEventService.getAdminEventListWithPaging(page -1, adminEventSearch, categoryType, eventStatus);
        return adminEventBoards;
    }

    //    이벤트 삭제
    @DeleteMapping("event/delete")
    @ResponseBody
    public void eventDelete(@RequestParam("checkedIds[]") List<String> checkIds){
        adminEventService.deleteAdminEvent(checkIds);
    }


    //    부모님마당 댓글 목록
    @GetMapping("parentsBoardReplyList/{page}")
    @ResponseBody
    public Page<AdminParentsBoardReplyDTO> getParentsBoardReplies(@PathVariable("page") int page, AdminParentsBoardReplySearch adminParentsBoardReplySearch){
        Page<AdminParentsBoardReplyDTO> adminParentsBoardReplies = adminParentsBoardReplyService.getAdminParentsBoardReplyListWithPaging(page -1, adminParentsBoardReplySearch);
        return adminParentsBoardReplies;
    }

    //    부모님마당 댓글 삭제
    @DeleteMapping("parentsBoardReply/delete")
    @ResponseBody
    public void parentsBoardReplyDelete(@RequestParam("checkedIds[]") List<String> checkIds){
        adminParentsBoardReplyService.deleteAdminParentsBoardReply(checkIds);
    }


    //    후기 댓글 목록
    @GetMapping("reviewReplyList/{page}")
    @ResponseBody
    public Page<AdminReviewReplyDTO> getReviewReplies(@PathVariable("page") int page, AdminReviewReplySearch adminReviewReplySearch){
        Page<AdminReviewReplyDTO> adminReviewReplies = adminReviewBoardReplyService.getAdminReviewReplyListWithPaging(page -1, adminReviewReplySearch);
        return adminReviewReplies;
    }

    //    후기 댓글 삭제
    @DeleteMapping("reviewBoardReply/delete")
    @ResponseBody
    public void reviewBoardReplyDelete(@RequestParam("checkedIds[]") List<String> checkIds){
        adminReviewBoardReplyService.deleteAdminReviewReply(checkIds);
    }


    //    관리자 공지사항 추가
    @PostMapping("announcement/insert")
    @ResponseBody
    public String getAnnouncementInsert(@RequestBody AdminAnnouncementDTO adminAnnouncementDTO){
        Long sessionId = 1L;
        Member member = memberRepository.findById(sessionId).get();
        Announcement announcement = new Announcement(adminAnnouncementDTO.getAnnouncementTitle(), adminAnnouncementDTO.getAnnouncementContent(), member);
        Announcement announcementToSave = announcementRepository.save(announcement);
        announcementFileService.saveAnnouncementAllFiles(adminAnnouncementDTO.getAnnouncementFileDTOS(), announcementToSave.getId());
        adminAnnouncementDTO.getAnnouncementFileDTOS().stream().map(AnnouncementFileDTO::toString).forEach(log::info);
        return "공지사항 입력 완료";
    }

    //    관리자 공지사항 수정

    //    관리자 공지사항 삭제
    @DeleteMapping("announcement/delete")
    @ResponseBody
    public void announcementDelete(@RequestParam("checkedIds[]") List<String> checkIds){
        adminAnnouncementService.deleteAdminAnnouncemnet(checkIds);
    }
    //    관리자 공지사항 목록
    @GetMapping("announcementList/{page}")
    @ResponseBody
    public Page<AdminAnnouncementDTO> getAnnouncement(@PathVariable("page") int page, AdminAnnouncementSearch adminAnnouncementSearch){
        Page<AdminAnnouncementDTO> adminAnnouncements = adminAnnouncementService.getAdminAnnouncementListWithPaging(page -1, adminAnnouncementSearch);
        return adminAnnouncements;
    }

    //    관리자 공지사항 상세보기
    @GetMapping("announcementDetail")
    @ResponseBody
    public List<AdminAnnouncementDTO> getAnnouncementDetail(){
        List<AdminAnnouncementDTO> adminAnnouncementDetails = adminAnnouncementService.getAdminAnnouncementDetail();
        return adminAnnouncementDetails;
    }

    //  관리자 문의 목록
    @GetMapping("askList/{page}")
    @ResponseBody
    public Page<AdminAskDTO> getAsk(@PathVariable("page") int page, AdminAskSearch adminAskSearch, String askStatus){
        Page<AdminAskDTO> adminAsks = adminAskService.getAdminAskListWithPaging(page -1, adminAskSearch, askStatus);
        return adminAsks;
    }


    //  관리자 문의 상세보기
    @GetMapping("askDetail")
    @ResponseBody
    public List<AdminAskDTO> geAskDetail(){
        List<AdminAskDTO> adminAskDetails = adminAskService.getAdminAskDetail();
        return adminAskDetails;
    }

    //  관리자 문의 답변
    @PostMapping("ask/answer")
    @ResponseBody
    public Long saveAskAnswer(@RequestBody AnswerDataDTO answerDataDTO){
        Long sessionId = 1L;
        Member member = memberRepository.findById(sessionId).get();
        Long changedTypeAskId =  Long.parseLong(answerDataDTO.getAskId());
        Ask ask = askRepository.findAskById_queryDSL(changedTypeAskId);
        AskAnswer askAnswer = new AskAnswer(answerDataDTO.getAnswerTitle(), answerDataDTO.getAnswerContent(), ask);
        AskAnswer AskAnswerToSave = askAnswerRepository.save(askAnswer);
        adminAskService.changeAskStataus(changedTypeAskId);
        return ask.getId();
    }

    //  관리자 문의 삭제
    @PatchMapping("ask/delete")
    @ResponseBody
    public void askDelete(@RequestParam("checkedIds[]") List<String> checkIds){
        adminAskService.deleteAdminAsk(checkIds);
    }

    //  통솔자 목록
    @GetMapping("guideList/{page}")
    @ResponseBody
    public Page<AdminMemberDTO> getGuide(@PathVariable("page") int page, AdminMemberSearch adminMemberSearch, GuideType guideType, AcceptanceType acceptanceType){
        Page<AdminMemberDTO> adminGuides= adminMemberService.getAdminGuideListWithPaging(page -1, adminMemberSearch, guideType, acceptanceType);
        return adminGuides;
    }

    //  관리자 통솔자 승인 or 취소
    @GetMapping("guide/change-status")
    @ResponseBody
    public void guideAcceptOrDeny(@RequestParam Long memberId, String confirm){
        adminMemberService.acceptGuide(memberId, confirm);
    }


}
