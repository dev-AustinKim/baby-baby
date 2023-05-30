package com.app.babybaby.domain.adminDTO;

import com.app.babybaby.type.GenderType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminKidDTO {
    private Long id;
    private String kidName;
    private Integer kidAge;
    private String eventTitle;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private String parentNickName;
    private GenderType kidGender;
    private LocalDate participateDate;

}
