package com.seulmae.seulmae.workplace.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "workplace_image")
@EntityListeners(AuditingEntityListener.class)
public class WorkplaceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_workplace_image", updatable = false)
    private Long idWorkPlaceImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

    @Column(name = "workplace_image_name")
    private String workplaceImageName;

    @Column(name = "workplace_image_path")
    private String workplaceImagePath;

    @Column(name = "workplace_image_extension")
    private String workplaceImageExtension;

    @CreatedDate
    @Column(name = "reg_date_workplace_image")
    private LocalDateTime regDateWorkplaceImage;

    @Column(name = "sequence")
    private Integer sequence;

    public WorkplaceImage(Workplace workplace, String workplaceImageName, String workplaceImagePath, String workplaceImageExtension, Integer sequence) {
        this.workplace = workplace;
        this.workplaceImageName = workplaceImageName;
        this.workplaceImagePath = workplaceImagePath;
        this.workplaceImageExtension = workplaceImageExtension;
        this.regDateWorkplaceImage = LocalDateTime.now();
        this.sequence = sequence;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }
}
