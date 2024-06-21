package com.seulmae.seulmae.workplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Table(name = "workplace_image")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }
}
