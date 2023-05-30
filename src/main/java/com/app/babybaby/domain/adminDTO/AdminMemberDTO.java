package com.app.babybaby.domain.adminDTO;

import com.app.babybaby.entity.board.event.Event;
import com.app.babybaby.entity.embeddable.Address;
import com.app.babybaby.type.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@NoArgsConstructor
public class AdminMemberDTO {

    private Long id;
    private String memberName;
    private String memberNickname;
    private String memberPhone;
    private String  memberEmail;
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private LocalDateTime memberRegisterDate;
    private String memberAddress;
    private String memberAddressDetail;
    private String memberAddressSubDetail;
    private String memberProfileOriginalName;
    private String memberProfileUUID;
    private String memberProfilePath;
    private MemberType memberType;
    private Role memberRole;
    private AcceptanceType memberGuideStatus;
    private SleepType memberSleep;
    private GuideType memberGuideType;
    private CategoryType memberGuideInterest;
    private String memberFilePath;
    private String memberFileUUID;
    private String memberFileOriginalName;
    private Long eventCount;
    private List<AdminEventDTO> companyEventList;

    @Builder
    public AdminMemberDTO(Long id, String memberName, String memberNickname, String memberPhone, String memberEmail, LocalDateTime memberRegisterDate, String memberAddress, String memberAddressDetail, String memberAddressSubDetail, String memberProfileOriginalName, String memberProfileUUID, String memberProfilePath, MemberType memberType, Role memberRole, AcceptanceType memberGuideStatus, SleepType memberSleep, GuideType memberGuideType, CategoryType memberGuideInterest, String memberFilePath, String memberFileUUID, String memberFileOriginalName, Long eventCount, List<AdminEventDTO> companyEventList) {
        this.id = id;
        this.memberName = memberName;
        this.memberNickname = memberNickname;
        this.memberPhone = memberPhone;
        this.memberEmail = memberEmail;
        this.memberRegisterDate = memberRegisterDate;
        this.memberAddress = memberAddress;
        this.memberAddressDetail = memberAddressDetail;
        this.memberAddressSubDetail = memberAddressSubDetail;
        this.memberProfileOriginalName = memberProfileOriginalName;
        this.memberProfileUUID = memberProfileUUID;
        this.memberProfilePath = memberProfilePath;
        this.memberType = memberType;
        this.memberRole = memberRole;
        this.memberGuideStatus = memberGuideStatus;
        this.memberSleep = memberSleep;
        this.memberGuideType = memberGuideType;
        this.memberGuideInterest = memberGuideInterest;
        this.memberFilePath = memberFilePath;
        this.memberFileUUID = memberFileUUID;
        this.memberFileOriginalName = memberFileOriginalName;
        this.eventCount = eventCount;
        this.companyEventList = companyEventList;
    }
}
