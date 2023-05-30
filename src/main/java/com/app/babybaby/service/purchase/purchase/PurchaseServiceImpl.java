package com.app.babybaby.service.purchase.purchase;

import com.app.babybaby.domain.boardDTO.eventDTO.EventDTO;
import com.app.babybaby.domain.boardDTO.parentsBoardDTO.ParentsBoardDTO;
import com.app.babybaby.domain.memberDTO.MemberDTO;
import com.app.babybaby.domain.purchaseDTO.purchaseDTO.PurchaseDTO;
import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.member.Crew;
import com.app.babybaby.entity.member.Guide;
import com.app.babybaby.entity.member.Kid;
import com.app.babybaby.entity.member.Member;
import com.app.babybaby.entity.purchase.coupon.Coupon;
import com.app.babybaby.entity.purchase.purchase.Purchase;
import com.app.babybaby.repository.board.event.EventRepository;
import com.app.babybaby.repository.member.crew.CrewRepository;
import com.app.babybaby.repository.member.guide.GuidRepository;
import com.app.babybaby.repository.member.kid.KidRepository;
import com.app.babybaby.repository.member.member.MemberRepository;
import com.app.babybaby.repository.purchase.coupon.CouponRepository;
import com.app.babybaby.repository.purchase.purchase.PurchaseRepository;
import com.app.babybaby.search.board.parentsBoard.ParentsBoardSearch;
import com.app.babybaby.service.member.member.MemberService;
import com.app.babybaby.type.GuideAvailableType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Qualifier("purchase") @Primary
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;

    private final CouponRepository couponRepository;

    private final EventRepository eventRepository;

    private final MemberRepository memberRepository;

    private final GuidRepository guidRepository;

    private final CrewRepository crewRepository;

    private final KidRepository kidRepository;

    private final MemberService memberService;

    //    구매내역
    @Override
    public Page<PurchaseDTO> findAllByMemberPaymentWithPage(Pageable pageable, Long memberId) {
        Page<Purchase> purchases = purchaseRepository.findAllByMemberPaymentWithPage_QueryDSL(pageable, memberId);
        List<PurchaseDTO> purchaseDTOS = purchases.stream().map(purchase -> PurchaseToDTO(purchase)).collect(Collectors.toList());

        return new PageImpl<>(purchaseDTOS, pageable, purchases.getTotalElements());
    }

    //    내가 참여한 이벤트요 ....
    @Override
    public Page<PurchaseDTO> findAllByEventWithPage(Pageable pageable, Long memberId, LocalDateTime startDate) {
        Page<Purchase> purchases = purchaseRepository.findAllByEventWithPage_QueryDSL(pageable, memberId, startDate);
        List<PurchaseDTO> purchaseDTOS = purchases.stream().map(purchase -> PurchaseToDTO(purchase)).collect(Collectors.toList());

        return new PageImpl<>(purchaseDTOS, pageable, purchases.getTotalElements());
    }

    //    구매 상세
    @Override
    public PurchaseDTO findMemberIdByPaymentDetail(Long purchaseId) {
        Purchase purchase = purchaseRepository.findMemberIdByPaymentDetail_QueryDSL(purchaseId);
        PurchaseDTO purchaseDTO = PurchaseToDTO(purchase);
        return purchaseDTO;
    }

    @Override
    public EventDTO findAllInfoForPayment(Long memberId, Long eventId) {
        Member member = memberRepository.findById(memberId).get();
        Event event = eventRepository.findById(eventId).get();
        EventDTO eventDTO = this.eventToDTO(event);
        List<Coupon> coupons = couponRepository.findAllUnusedCoupon(memberId);
        eventDTO.setCoupons(coupons.stream().map(this::CouponToDTO).collect(Collectors.toList()));
        eventDTO.setTotalUnusedCouponCount(couponRepository.totalUnusedCouponCount(memberId));
        eventDTO.setTotalCouponCount(couponRepository.totalCouponCount(memberId));

        return eventDTO;
    }

//    public void processPayment(Long memberId, Long eventId, List<Kid> kids) {
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));
//        Member company = null;
//        if (memberId != null) {
//            company = memberRepository.findById(memberId).orElse(null);
//        }
//
//        // 가장 최근 가이드 하나 가져오기
//        Guide guide = guidRepository.findFirstByEventIdAndAvailableTypeOrderById(eventId, GuideAvailableType.AVAILABLE);
//
//        log.info("=============", guide);
//
//        // 가이드가 없다면 새로운 가이드 생성
//        if (guide == null) {
//            guide = new Guide(event, company);
//            guide = guidRepository.save(guide);
//        }
//        log.info("===========here111===========", guide);
//
//        // 크루가 없다면 크루 생성
//        if (guide.getCrews() == null) {
//            log.info("===========here===========");
//            final Guide finalGuide = guide;
//            kids.forEach(kid -> {
//                Crew crew = new Crew(kid, finalGuide);
//                crew.setEventRegisterDate(LocalDate.now());
//                crewRepository.save(crew);
//            });
//
//            // 현재 들어온 아이들 수와 남아있는 가이드의 아이들 수를 합친 결과가 10보다 크거나 같다면
//            if (guide.getCrews().size() + kids.size() >= 10) {
//                // 가이드의 상태를 불가능으로 변경
//                guide.setAvailableType(GuideAvailableType.UNAVAILABLE);
//
//                // 새로운 가이드 가져오기
//                Guide newGuide = guidRepository.findFirstByEventIdAndAvailableTypeOrderById(eventId, GuideAvailableType.AVAILABLE);
//                if (newGuide != null) {
//                    Guide guide1 = new Guide(event, company);
//                    Guide savedGuide = guidRepository.save(guide1);
//                    kids.forEach(kid -> {
//                        Crew crew = new Crew(kid, savedGuide);
//                        crew.setEventRegisterDate(LocalDate.now());
//                        crewRepository.save(crew);
//                    });
//                }
//            }
//        }
//    }

    @Override
    public void saveAll(Long memberId, Long eventId, PurchaseDTO purchaseDTO) {
        //        Purchase purchase = this.dtoToPurchaseEntity(purchaseDTO);
        //        이 맴버는 현재 세션에 있는 맴버아이디
        Member member = memberRepository.findById(memberId).get();
        Event event = eventRepository.findById(eventId).get();
        Purchase purchase1 = new Purchase(purchaseDTO.getPurchaseCount(), purchaseDTO.getPurchasePrice(), event, member);
        purchaseRepository.save(purchase1);
        List<Kid> kids = purchaseDTO.getKids().stream()
                .map(kidDTO -> kidRepository.findById(kidDTO.getId()).get())
                .collect(Collectors.toList());
        log.info("purchaseDTO 입니다 ~~~~~~~~~~~~~ : " + purchaseDTO);
        log.info("Kids들 입니다 ~~~~~~~~~~~~ : " + kids);
        processPayment(memberId, eventId, kids, purchaseDTO.getEventRegisterDate());
    }



    public void processPayment(Long memberId, Long eventId, List<Kid> kids, String eventRegisterDate) {
//        이벤트와 컴퍼니는 없을수가없다.
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));
        Member company = memberRepository.findById(memberId).get();
        LocalDate localDate = LocalDate.parse(eventRegisterDate);

        // 가장 최근 가이드 하나 가져오기
        Guide guide = guidRepository.findFirstByEventIdAndAvailableTypeOrderById(eventId, GuideAvailableType.AVAILABLE);
        // 가이드가 없다면 새로운 가이드 생성
        if (guide == null) {
            guide = new Guide(event, company);
            guide = guidRepository.save(guide);
            log.info("가이드가 없을 때 생기는 guide: " + guide);
        }
        
        if (guide.getCrews() == null) {
            guide.setCrews(new ArrayList<>());
        }
        log.info("내가 가져온 가이드의 크루들: " + guide.getCrews().size());
        log.info("크루 생성전 가이드 : " + guide);
        log.info("내가 가져온 Kids" + kids);
        // 크루가 없다면 크루 생성
//        if (guide.getCrews() == null || guide.getCrews().isEmpty()) {
//            final Guide finalGuide = guide;
//            kids.forEach(kid -> {
//                Crew crew = new Crew(kid, finalGuide);
//                crew.setEventRegisterDate(localDate);
//                Crew savedCrew = crewRepository.save(crew);
//                log.info("크루가 없을 때 크루가 세이브 되는 곳: " + savedCrew);
//            });
//        }

        if (guide.getCrews().size() + kids.size() < 10) {
            final Guide finalGuide = guide;
            kids.forEach(kid -> {
                Crew crew = new Crew(kid, finalGuide);
                crew.setEventRegisterDate(localDate);
                Crew savedCrew = crewRepository.save(crew);
                log.info("크루가 없을 때 크루가 세이브 되는 곳: " + savedCrew);
            });
        }

        // 현재 들어온 아이들 수와 남아있는 가이드의 아이들 수를 합친 결과가 10보다 크거나 같다면
        log.info("가이드가 가지고있는 크루의 사이즈는 : " + guide.getCrews().size());
        log.info("지금 신청한 애들의 사이즈는  : " + guide.getCrews().size());
        if (guide.getCrews().size() + kids.size() > 10) {
            // 새로운 가이드 가져오기
            Guide newGuide = guidRepository.findFirstByEventIdAndAvailableTypeOrderById(eventId, GuideAvailableType.AVAILABLE);
            log.info("10명이 넘어갔을떄 새로운 가이드를 가져오는 겁니다" + newGuide);
            if (newGuide == null) {
                Guide guide1 = new Guide(event, company);
                Guide savedGuide = guidRepository.save(guide1);
                kids.forEach(kid -> {
                    Crew crew = new Crew(kid, savedGuide);
                    crew.setEventRegisterDate(LocalDate.now());
                    crewRepository.save(crew);
                });
            } else{
                final Guide finalGuide = guide;
                kids.forEach(kid -> {
                    Crew crew = new Crew(kid, finalGuide);
                    crew.setEventRegisterDate(LocalDate.now());
                    crewRepository.save(crew);
                });
            }
        }












    }


    @Override
    public Long findMemberByIdWithCount(Long memberId) {
        Long count = purchaseRepository.findMemberByIdWithCount(memberId);
        return count;
    }
}