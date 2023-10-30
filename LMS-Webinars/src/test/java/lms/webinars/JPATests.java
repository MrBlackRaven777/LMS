package lms.webinars;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lms.webinars.dto.WebinarDto;
import lms.webinars.dto.WebinarRequest;
import lms.webinars.service.WebinarsJPA;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static lms.webinars.fixtures.WebinarsData.NEW_WEBINAR_REQUEST;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {LmsWebinarsApplication.class})
@Slf4j
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/webinars.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class JPATests {


    @Autowired
    private WebinarsJPA webinarsJPA;

    @Autowired
    private MockMvc mvc;

    static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void init() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void initTest() {
        assertNotNull(webinarsJPA);
        assertNotNull(mvc);
    }

    @Test
    public void jpa_addWebinarTest() {
        WebinarDto webinar = null;
        try {
            webinar = mapper.readValue(NEW_WEBINAR_REQUEST, WebinarRequest.class).getData();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertNotNull(webinar);
        assertNull(webinar.getId());

        WebinarDto savedWebinar = webinarsJPA.addNewWebinar(webinar);
        assertNotNull(savedWebinar);
        assertNotNull(savedWebinar.getId());
        assertEquals(webinar.getName(), savedWebinar.getName());
    }

    @Test
    public void jpa_findAllByThemeTest() {
        List<WebinarDto> webinars = webinarsJPA.getAllByTheme("first");
        assertEquals(2, webinars.size());
        assertTrue(webinars.stream().allMatch(w -> w.getTheme().equals("first")));
        assertTrue(webinars.stream().anyMatch(w -> w.getName().equals("1_First")));
        assertTrue(webinars.stream().allMatch(w -> w.getGroupName().equals("Group_1") || w.getGroupName().equals("Group_2")));
        webinars = webinarsJPA.getAllByTheme("not exist");
        assertTrue(webinars.isEmpty());
    }

    @Test
    public void jpa_getAllByGroupTest() {
        List<WebinarDto> webinars = webinarsJPA.getAllByGroup("Group_2");
        assertEquals(2, webinars.size());
        assertTrue(webinars.stream().allMatch(w -> w.getGroupName().equals("Group_2")));
    }

    @Test
    public void jpa_getLastWebinarTest() {
        WebinarDto webinar = webinarsJPA.getLastWebinar();
        assertEquals(5L, webinar.getId());
        assertEquals("5_Fifth", webinar.getName());
        assertEquals("Group_4", webinar.getGroupName());
        assertEquals(LocalDate.of(2023, 1, 1), webinar.getDate());
    }
}
