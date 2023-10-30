package lms.webinars.domain.repo;

import lms.webinars.domain.entities.WebinarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WebinarsRepository extends JpaRepository<WebinarEntity, Long> {
    List<WebinarEntity> findAllByTheme(String theme);

    List<WebinarEntity> findAllByGroupName(String groupName);

    Optional<WebinarEntity> findFirstByDateBeforeOrderByIdDesc(LocalDate date);

    List<WebinarEntity> getAllByDateBetween(LocalDate from, LocalDate to);
}


