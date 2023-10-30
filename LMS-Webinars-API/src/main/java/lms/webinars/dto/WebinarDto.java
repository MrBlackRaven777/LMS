package lms.webinars.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WebinarDto {
    Long id;
    String name;
    String label;
    String theme;
    String groupName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate date;
    String link;
    String record;
    List<FileDto> files;

    public WebinarDto(long id) {
        this.id = id;
    }
}


