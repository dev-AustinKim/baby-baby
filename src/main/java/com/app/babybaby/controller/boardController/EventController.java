package com.app.babybaby.controller.boardController;

import com.app.babybaby.domain.boardDTO.eventDTO.EventDTO;
import com.app.babybaby.domain.boardDTO.eventDTO.PageRequestDTO;
import com.app.babybaby.domain.memberDTO.MemberDTO;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.calendar.Calendar;
import com.app.babybaby.entity.embeddable.Address;
import com.app.babybaby.entity.member.Kid;
import com.app.babybaby.search.board.parentsBoard.EventBoardSearch;
import com.app.babybaby.service.board.event.EventService;
import com.app.babybaby.service.calendar.CalendarService;
import com.app.babybaby.service.member.member.MemberService;
import com.app.babybaby.type.MemberType;
import com.google.gson.JsonArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/event/*")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;
    private final MemberService memberService;

    private final CalendarService calendarService;


//    /*test 용 컨트롤러*/
//    @ResponseBody
//    @GetMapping("lists")
//    public Slice<Event> getEventList(@RequestParam(required = false) EventBoardSearch eventBoardSearch,
//                                     @PageableDefault(size = 10) Pageable pageable) {
//        return eventService.findEventListWithPaging(eventBoardSearch, pageable);
//    }
    @GetMapping("api")
    public String getEventApi(){
        return "play/event-open-api";
    }
    /*test 용 */


    /* 학부모회원의 이벤트 신청하기 */
    @GetMapping("applyEvent/{eventId}")
    public String applyEvent(@PathVariable Long eventId , Model model, HttpSession session){
        MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
        Long sessionId = memberDTO.getId();
        EventDTO eventDTO = eventService.getAllEventInfo(sessionId, eventId);
        List<Kid> kidsList = eventService.findKidsBySessionId(sessionId);
        model.addAttribute("eventDTO", eventDTO);
        model.addAttribute("kidsList", kidsList);
        return "play/event-join-write";
    }



    /* 기업회원 글쓰기 게시글 내용 작성 */
    @GetMapping("writeFirst")
    public String goFirstWrite(Model model, HttpSession session){
        MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
        model.addAttribute("memberNickname", memberDTO.getMemberNickname());
        return "play/play-write-field";
    }



    /* 기업회원 글쓰기 - 대표사진 등록 */
    @GetMapping("writeSecond")
    public String goSecondWrite(Model model, HttpSession session){
        MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
        model.addAttribute("memberNickname", memberDTO.getMemberNickname());
        return "play/play-write-single";
    }

    /* 기업회원 글쓰기 게시글 내용사진 등록*/
    @GetMapping("writeThird")
    public String goThirdWrite(Model model, HttpSession session){
        MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
        model.addAttribute("memberNickname", memberDTO.getMemberNickname());
        return "play/play-write-multi";
    }

    @GetMapping("save")
    public RedirectView goSaveWithData(EventDTO eventDTO, @RequestParam String stringStartDate, @RequestParam String stringEndDate, @RequestParam String address, HttpSession session){
        MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
        Long sessionId = memberDTO.getId();
        LocalDate localStartDate = LocalDate.parse(stringStartDate, DateTimeFormatter.ISO_DATE);
        LocalDate localEndDate = LocalDate.parse(stringEndDate, DateTimeFormatter.ISO_DATE);

        LocalDateTime startDate = localStartDate.atStartOfDay();
        LocalDateTime endDate = localEndDate.atTime(LocalTime.MAX);
        eventDTO.setEventLocation(new Address(address));
        Calendar calendar = new Calendar("123", eventDTO.getCategory(), startDate, endDate);
        eventService.saveAll(sessionId, eventDTO, calendar);
        log.info("First에서 받아오는 시작하는 날짜 " + startDate +"  First에서 받아오는 끝나는 날짜 " +  endDate);
        log.info(eventDTO.toString());

        log.info("마지막에 가져온 eventDTO는 : " + eventDTO.toString());
        log.info("마지막에 가져온 startDate는 : " + startDate);
        log.info("마지막에 가져온 endDate는 : " + endDate);

        return new RedirectView("/event/list");
    }

    @GetMapping("list")
    public String getList(Model model,@RequestParam(required = false) EventBoardSearch eventBoardSearch, @PageableDefault(size = 10) Pageable pageable){
//        model.addAttribute("eventList",eventService.findEventListWithPaging(eventBoardSearch, PageRequest.of(0,10)));
        return "play/event-category-list";
    }


    @ResponseBody
    @PostMapping("list")
    public String getEvents(@RequestBody EventBoardSearch eventBoardSearch, PageRequestDTO pageRequestDTO, HttpSession session){
        MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
        Long sessionId = null;
        if(memberDTO != null){
            sessionId = memberDTO.getId();
        }

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage(), 8);
        Slice<EventDTO> eventListDTO = eventService.findEventListWithPaging(sessionId, eventBoardSearch,pageable);
        List<EventDTO> eventDTOList = eventListDTO.stream().collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        for (EventDTO eventDTO : eventListDTO) {
            JSONObject jsonObject = new JSONObject(eventDTO);
            jsonArray.put(jsonObject);
        }

        log.info("지금 내가 뽑은 eventListDTO : " + eventListDTO.getContent());
        log.info(pageable.toString());
        log.info(eventBoardSearch.toString());

        return jsonArray.toString();
    }


    @GetMapping("detail/{eventId}")
    public String goEventDetail(@PathVariable Long eventId, Model model, HttpSession session){
        MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
        Long sessionId = null;

        if(memberDTO != null){
             sessionId = memberDTO.getId();
        }
        EventDTO eventDTO = eventService.getAllEventInfo(sessionId, eventId);
        model.addAttribute("eventDTO", eventDTO);
        return "play/event-board-detail";
    }



//    @GetMapping("goWrite")
//    public RedirectView test(HttpSession session){
//        MemberDTO memberDTO = (MemberDTO)session.getAttribute("member");
//        Long sessionId = null;
//        String url = null;
//
//        if(memberDTO == null){
//            return  new RedirectView("/member/login");
//        }
//
//        log.info(memberDTO.toString());
//        return memberDTO.getMemberType() == MemberType.COMPANY ? new RedirectView("/event/writeFirst") : new RedirectView("/event/list");
//    }


//
//    /*test 용 */
//    @ResponseBody
//    @GetMapping("list")
//    public Slice<EventDTO> getList(@RequestParam(required = false) EventBoardSearch eventBoardSearch,
//                                   @PageableDefault(size = 10) Pageable pageable, Model model){
//        model.addAttribute("eventList",eventService.findEventListWithPaging(eventBoardSearch, PageRequest.of(0,10)));
//        return eventService.findEventListWithPaging(eventBoardSearch, pageable);
//    }


}
