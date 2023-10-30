package lms.webinars.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Random;

@AllArgsConstructor
@Data
@ToString
public class WebinarRequest {
    Long rqId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public LocalDateTime dateTime;
    String params;
    WebinarDto data;

    public WebinarRequest() {
        rqId = new Random().nextLong(1, Long.MAX_VALUE);
        dateTime = LocalDateTime.now();
        params = null;
        data = null;// new WebinarDto();
    }

    public WebinarRequest(WebinarDto data) {
        this();
        this.data = data;
    }

    public void setParams(String... params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            sb.append(String.format("%s:%s;", params[i], params[i + 1]));
        }
        this.params = sb.toString();
    }
}
