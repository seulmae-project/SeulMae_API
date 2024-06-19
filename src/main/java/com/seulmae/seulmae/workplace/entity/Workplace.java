package com.seulmae.seulmae.workplace.entity;

import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceModifyDto;
import com.seulmae.seulmae.workplace.vo.AddressVo;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "workplace")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Workplace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workplace", updatable = false)
    private Long idWorkPlace;

    @Column(name = "workplace_code", updatable = false, unique = true)
    private String workplaceCode;

    @Column(name = "workplace_name")
    private String workplaceName;

    @Embedded
    private AddressVo addressVo;

    @Column(name = "workplace_tel")
    private String workplaceTel;

    @OneToMany(mappedBy = "workplace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkplaceImage> workplaceImages = new ArrayList<>();

    @CreatedDate
    @Column(name = "reg_date_workplace")
    private LocalDateTime regDateWorkplace;

    @Column(name = "is_del_user")
    private Boolean isDelWorkplace = false;

    @Column(name = "del_date_user")
    private LocalDateTime delDateWorkplace;

    public Workplace(WorkplaceAddDto workplaceAddDto) {
        this.workplaceCode = UUID.randomUUID().toString();
        this.workplaceName = workplaceAddDto.getWorkplaceName();
        this.addressVo = new AddressVo(
                workplaceAddDto.getMainAddress(),
                workplaceAddDto.getSubAddress()
        );
        this.workplaceTel = workplaceAddDto.getWorkplaceTel();
        this.regDateWorkplace = LocalDateTime.now();
        this.delDateWorkplace = null;
    }


    public void setWorkplaceImages(List<WorkplaceImage> workplaceImages) {
        this.workplaceImages = workplaceImages;
    }

    public void deleteWorkplace() {
        this.isDelWorkplace = true;
        this.delDateWorkplace = LocalDateTime.now();
    }
}
