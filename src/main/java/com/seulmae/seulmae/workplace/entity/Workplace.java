package com.seulmae.seulmae.workplace.entity;

import com.seulmae.seulmae.workplace.vo.AddressVo;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "workplace")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Workplace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workplace", updatable = false)
    private Long idWorkPlace;

    @Column(name = "workplace_code", updatable = false, unique = true)
    private String workplaceCode;

    @Column(name = "workplace_topic", unique = true)
    private String workplaceTopic;

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

    @Column(name = "is_del_workplace")
    @Builder.Default
    private Boolean isDelWorkplace = false;

    @Column(name = "del_date_workplace")
    private LocalDateTime delDateWorkplace;

    public void setWorkplaceImages(List<WorkplaceImage> workplaceImages) {
        this.workplaceImages = workplaceImages;
    }

    public void setWorkplaceTopic(String workplaceTopic) {
        this.workplaceTopic = workplaceTopic;
    }

    public void deleteWorkplace() {
        this.isDelWorkplace = true;
        this.delDateWorkplace = LocalDateTime.now();
    }
}
