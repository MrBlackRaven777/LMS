package lms.webinars.domain.repo;

import lms.webinars.domain.entities.FileEntity;
import lms.webinars.dto.FileDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<FileEntity, Long> {
}


