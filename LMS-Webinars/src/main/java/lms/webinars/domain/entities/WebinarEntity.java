package lms.webinars.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "webinars")
public class WebinarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Long id;
    String name;
    String label;
    String theme;
    @Column(name = "group_name")
    String groupName;
    LocalDate date;
    String link;
    String record;
    @OneToMany(mappedBy = "webinar", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    List<FileEntity> files;
}

