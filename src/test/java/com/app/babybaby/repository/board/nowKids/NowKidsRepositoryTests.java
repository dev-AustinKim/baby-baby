package com.app.babybaby.repository.board.nowKids;

import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.board.nowKids.NowKids;
import com.app.babybaby.entity.calendar.Calendar;
import com.app.babybaby.entity.embeddable.Address;
import com.app.babybaby.entity.file.nowKidsFile.NowKidsFile;
import com.app.babybaby.entity.guideSchedule.GuideSchedule;
import com.app.babybaby.entity.user.Crew;
import com.app.babybaby.entity.user.Guide;
import com.app.babybaby.entity.user.Kid;
import com.app.babybaby.entity.user.User;
import com.app.babybaby.repository.board.event.EventRepository;
import com.app.babybaby.repository.board.nowKids.NowKidsRepository;
import com.app.babybaby.repository.calendar.CalendarRepository;
import com.app.babybaby.repository.file.nowKidsFile.NowKidsFileFileRepository;
import com.app.babybaby.repository.guideSchedule.GuideScheduleRepository;
import com.app.babybaby.repository.user.crew.CrewRepository;
import com.app.babybaby.repository.user.guide.GuidRepository;
import com.app.babybaby.repository.user.kid.KidRepository;
import com.app.babybaby.repository.user.user.UserRepository;
import com.app.babybaby.type.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@Slf4j
@Transactional
@Rollback(false)
public class NowKidsRepositoryTests {
    @Autowired
    NowKidsRepository nowKidsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    NowKidsFileFileRepository nowKidsFileFileRepository;

    @Autowired
    KidRepository kidRepository;

    @Autowired
    CalendarRepository calendarRepository;

    @Autowired
    GuidRepository guidRepository;

    @Autowired
    GuideScheduleRepository guideScheduleRepository;

    @Autowired
    CrewRepository crewRepository;

    @Test
    public void save50NowKids() {
        Address address = new Address();
        address.setAddress("분당");
        address.setAddressDetail("d");
        address.setAddressSubDetail("dfa");
        address.setPostcode("12342132");
        for (int i = 0; i < 50; i++) {
            Calendar calendar = new Calendar("이벤트1", CategoryType.AGRICULTURE, LocalDateTime.now(), LocalDateTime.now());
            Event event = new Event("Test" + (i + 1), "test", 10L, address, 10000L, "TEST", "TEst", CategoryType.MUSEUM, calendar);
            String uniqueNickname = "Bool" + i;
            User user = new User("you" + i + "@naver.com", "정유찬", "1234", uniqueNickname, "안녕하세요",
                    "0101234123" + i, address, LocalDateTime.now(), UserType.COMPANY, AcceptanceType.ACCEPTED, SleepType.AWAKE, GuideType.NON_DISABLED, CategoryType.AGRICULTURE);
            userRepository.save(user);
            eventRepository.save(event);
            NowKids nowKids = new NowKids(event, user);
            nowKidsRepository.save(nowKids);
        }
    }

    @Test
    public void nowKidsFileSaveTest(){
        Optional<NowKids> nowKids = nowKidsRepository.findById(455L);
        NowKidsFile nowKidsFile = new NowKidsFile("Mainfdsafdsa23", "Mainfdsafdsa", "123213", FileType.MAIN, nowKids.get());
        NowKidsFile nowKidsFile2 = new NowKidsFile("Subfdsafdsa32", "Subfdsafdsa1", "123213", FileType.SUBS, nowKids.get());
        NowKidsFile nowKidsFile3 = new NowKidsFile("Subfdsafdsa2", "Subfdsafdsa2", "123213", FileType.SUBS, nowKids.get());

        nowKidsFileFileRepository.save(nowKidsFile);
        nowKidsFileFileRepository.save(nowKidsFile2);
        nowKidsFileFileRepository.save(nowKidsFile3);
    }

    @Test
    public void kidsKidsSaveTest(){
        Optional<User> parent = userRepository.findById(452L);
        for (int i = 0; i < 10; i++){
            Kid kid = new Kid("김동한" + i, 4L + i, GenderType.MAN, parent.get());
            kidRepository.save(kid);
        }
    }

    @Test
    public void  guideInsertTest(){
        User guide = userRepository.findById(456L).get();
        User adminGuide = userRepository.findById(460L).get();
        Optional<Calendar> calendar = calendarRepository.findById(454L);
        Optional<Event> event = eventRepository.findById(453L);
        GuideSchedule guideSchedule = new GuideSchedule(calendar.get(), event.get(), guide);
        List<Crew> crews = crewRepository.findAll();
        Guide guide1 = new Guide(event.get(), guideSchedule, guide ,adminGuide, crews);
        guideScheduleRepository.save(guideSchedule);
        guidRepository.save(guide1);
    }
    
//    나의 생각 정리
    /* 
    모든 정보는 화면에서 DTO로 뿌린다
    먼저 NowKids의 모든 정보를 가져온다.
    NowKids에는 Event와 User(parent)가 있음으로
    NowKidsDTO에서 Event와 user의 정보를 모두 다 조회해서 담아준다.
    근데 여기서 파일은 여러개이다.
    따라서 NowKidsFileDTO를 하나 더 만들어서 그 NowKidsDTO에 담아준다.
     */

//  여기서 필요한 것
    /* NowKids를 모두 가져오는 메소드 */
    /* NowKids의 아이디로 해당 이벤트의 정보를 조회하는 메소드? Callsuper=true라서 굳이 안불러도 되나? 왜냐하면 어차피 내가 필요한건 BoardTitle밖에 없기에
    * 무조건 Event를 가져와야한다 왜냐하면 내가 조회한 BoardTitle은 NowKids의 필드이기때문에, Event의 필드가 아니다. 따라서 나는 Event를 조회해야하기에
    * Event를 가져와야한다.
    * */
    @Test
    public void findAllTest() {
       log.info(nowKidsRepository.findAll().toString());
    }

    @Test 
    public void findNowKidsByGuideId_QueryDslTest(){
        log.info(nowKidsRepository.findNowKidsByGuideId_QueryDsl(452L).toString());
    }

    @Test
    public void findByIdTest2(){
        log.info(nowKidsRepository.findById(1L).toString());
    }

    @Test
    public void findAll(){
        log.info(nowKidsRepository.findGuideBoard_QueryDsl().toString());
    }

    @Test
    public void findGuideBoardWithPagingTest(){
        log.info(nowKidsRepository.findGuideBoardWithPaging_QueryDsl(0, 10).toString());
    }

    @Test
    public void findEventInfoByGuideIdTest(){
        log.info(nowKidsRepository.findEventInfoByGuideId_QueryDsl(456L).toString());
    }

    @Test
    public void findAllKidsByGeneralGuideId_QueryDslTest(){
        log.info(nowKidsRepository.findAllKidsByGeneralGuideId_QueryDsl(456L).toString());
    }

}
