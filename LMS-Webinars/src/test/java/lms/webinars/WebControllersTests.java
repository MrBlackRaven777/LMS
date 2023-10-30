package lms.webinars;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lms.webinars.service.WebinarsJPA;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static lms.webinars.mappings.Mappings.*;
import static lms.webinars.fixtures.WebinarsData.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {LmsWebinarsApplication.class})
@Slf4j
@Sql(value = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/webinars.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class WebControllersTests {

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
    public void web_getWebinarById() throws Exception {
        mvc.perform(get(GET_WEBINAR_BY_ID_PATH, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].name", is("1_First")));
    }

    @Test
    public void web_getWebinarById_NotExists() throws Exception {
        mvc.perform(get(GET_WEBINAR_BY_ID_PATH, 11))
                .andExpect(status().isNotFound());
    }

    @Test
    public void web_getLastWebinar() throws Exception {
        mvc.perform(get(GET_LAST_WEBINAR_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id", is(5)))
                .andExpect(jsonPath("$.data[0].name", is("5_Fifth")));
    }

    @Test
    @Sql("/reset.sql")
    public void web_getLastWebinar_NotExists() throws Exception {
        mvc.perform(get(GET_LAST_WEBINAR_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void web_getGroupWebinars() throws Exception {
        mvc.perform(get(GET_GROUP_WEBINARS_PATH, "Group_1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(2)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].name", is("1_First")))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].name", is("2_Second")));
    }

    @Test
    public void web_getGroupWebinars_NotExists() throws Exception {
        mvc.perform(get(GET_GROUP_WEBINARS_PATH, "Group_42"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(0)));
    }

    @Test
    public void web_postAddNewWebinar() throws Exception {
        mvc.perform(post(POST_ADD_NEW_WEBINAR_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEW_WEBINAR_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(6)))
                .andExpect(jsonPath("$.data[0].name", is("6_Sixth")))
                .andExpect(jsonPath("$.data[0].files.size()", is(2)));
    }

    @Test
    public void web_postAddNewWebinar_AlreadyExists() throws Exception {
        mvc.perform(post(POST_ADD_NEW_WEBINAR_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EXISTING_WEBINAR_REQUEST))
                .andExpect(status().isConflict());
    }

    @Test
    public void web_putUpdate1FieldWebinar() throws Exception {
        mvc.perform(put(PUT_UPDATE_WEBINAR_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_1_FIELD_WEBINAR_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("9_Ninth")))
                .andExpect(jsonPath("$.data[0].label", is("2. Second")));
    }

    @Test
    public void web_putUpdateAllFieldsWebinar() throws Exception {
        mvc.perform(put(PUT_UPDATE_WEBINAR_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_ALL_FIELDS_WEBINAR_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(3)))
                .andExpect(jsonPath("$.data[0].name", is("10_Tenth")))
                .andExpect(jsonPath("$.data[0].label", is("10. Tenth")))
                .andExpect(jsonPath("$.data[0].groupName", is("Group_23")))
                .andExpect(jsonPath("$.data[0].files.size()", is(2)))
                .andExpect(jsonPath("$.data[0].files[*].id", containsInAnyOrder(6, 9)))
                .andExpect(jsonPath("$.data[0].files[*].url", containsInAnyOrder("https://files.org/file6.ext", "https://files.link/file108.ext")))
                .andExpect(jsonPath("$.data[0].files[*].description", containsInAnyOrder("Webinar 3 file", "Source low code")));
    }

    @Test
    public void web_putUpdateWebinar_existedFiles() throws Exception {
        mvc.perform(put(PUT_UPDATE_WEBINAR_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_WEBINAR_EXISTED_FILES_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("2_Second")))
                .andExpect(jsonPath("$.data[0].files.size()", is(3)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(3)))
                .andExpect(jsonPath("$.data[0].files[0].url", endsWith("UPDATED")))
                .andExpect(jsonPath("$.data[0].files[0].description", endsWith("UPDATED")))
                .andExpect(jsonPath("$.data[0].files[1].id", is(4)))
                .andExpect(jsonPath("$.data[0].files[1].url", endsWith("UPDATED")))
                .andExpect(jsonPath("$.data[0].files[1].description", endsWith("UPDATED")))
                .andExpect(jsonPath("$.data[0].files[2].id", is(5)))
                .andExpect(jsonPath("$.data[0].files[2].url", is("https://files.org/file5.ext")))
                .andExpect(jsonPath("$.data[0].files[2].description", is("Webinar 2 file")));
    }

    @Test
    public void web_putUpdateWebinar_newFiles() throws Exception {
        mvc.perform(put(PUT_UPDATE_WEBINAR_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_WEBINAR_NEW_FILES_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("2_Second")))
                .andExpect(jsonPath("$.data[0].files.size()", is(5)))
                .andExpect(jsonPath("$.data[0].files[*].id", containsInAnyOrder(3,4,5, 9,10)))
                .andExpect(jsonPath("$.data[0].files[0].url", endsWith("NEW")))
                .andExpect(jsonPath("$.data[0].files[0].description", endsWith("NEW")))
                .andExpect(jsonPath("$.data[0].files[1].id", is(10)))
                .andExpect(jsonPath("$.data[0].files[1].url", endsWith("NEW")))
                .andExpect(jsonPath("$.data[0].files[1].description", endsWith("NEW")));
    }

    @Test
    public void web_putUpdateWebinar_nonExistedFiles() throws Exception {
        mvc.perform(put(PUT_UPDATE_WEBINAR_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_WEBINAR_NON_EXISTED_FILES_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("2_Second")))
                .andExpect(jsonPath("$.data[0].files.size()", is(3)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(3)))
                .andExpect(jsonPath("$.data[0].files[0].url", is("https://files.org/file3.ext")))
                .andExpect(jsonPath("$.data[0].files[0].description", is("Webinar 2 file")))
                .andExpect(jsonPath("$.data[0].files[1].id", is(4)))
                .andExpect(jsonPath("$.data[0].files[1].url", is("https://files.org/file4.ext")))
                .andExpect(jsonPath("$.data[0].files[1].description", is("Webinar 2 file")));
    }

    @Test
    public void web_deleteWebinarAndFiles() throws Exception {
        mvc.perform(delete(DELETE_DELETE_WEBINAR_PATH, 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)));

        mvc.perform(delete(DELETE_DELETE_WEBINAR_PATH, 5))
                .andExpect(status().isNotFound());

        mvc.perform(get(GET_WEBINAR_BY_ID_PATH, 5))
                .andExpect(status().isNotFound());

        mvc.perform(get(GET_FILE_BY_ID_PATH, 7))
                .andExpect(status().isNotFound());

        mvc.perform(get(GET_FILE_BY_ID_PATH, 8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void web_getFile() throws Exception {
        mvc.perform(get(GET_FILE_BY_ID_PATH, 7))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(5)))
                .andExpect(jsonPath("$.data[0].name", is("5_Fifth")))
                .andExpect(jsonPath("$.data[0].files.size()", is(1)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(7)))
                .andExpect(jsonPath("$.data[0].files[0].url", is("https://files.org/file10.ext")))
                .andExpect(jsonPath("$.data[0].files[0].description", is(nullValue())));
    }

    @Test
    public void web_getFile_NotExists() throws Exception {
        mvc.perform(get(GET_FILE_BY_ID_PATH, 17))
                .andExpect(status().isNotFound());
    }

    @Test
    public void web_addNewFiles() throws Exception {
        mvc.perform(post(POST_ADD_NEW_FILES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_NEW_FILES_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(4)))
                .andExpect(jsonPath("$.data[0].name", is("2_Second")))
                .andExpect(jsonPath("$.data[0].groupName", is("Group_2")))
                .andExpect(jsonPath("$.data[0].files.size()", is(2)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(9)))
                .andExpect(jsonPath("$.data[0].files[0].url", endsWith("NEW")))
                .andExpect(jsonPath("$.data[0].files[0].description", endsWith("NEW")))
                .andExpect(jsonPath("$.data[0].files[1].id", is(10)))
                .andExpect(jsonPath("$.data[0].files[1].url", endsWith("NEW")))
                .andExpect(jsonPath("$.data[0].files[1].description", endsWith("NEW")));
    }

    @Test
    public void web_deleteFile() throws Exception {

        mvc.perform(get(GET_WEBINAR_BY_ID_PATH, 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id", is(5)))
                .andExpect(jsonPath("$.data[0].files.size()", is(2)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(7)))
                .andExpect(jsonPath("$.data[0].files[1].id", is(8)));

        mvc.perform(get(GET_FILE_BY_ID_PATH, 7))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(5)))
                .andExpect(jsonPath("$.data[0].files.size()", is(1)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(7)));


        mvc.perform(delete(DELETE_DELETE_FILE_PATH, 7))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)));


        mvc.perform(get(GET_WEBINAR_BY_ID_PATH, 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id", is(5)))
                .andExpect(jsonPath("$.data[0].files.size()", is(1)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(8)));

        mvc.perform(get(GET_FILE_BY_ID_PATH, 7))
                .andExpect(status().isNotFound());


        mvc.perform(delete(DELETE_DELETE_FILE_PATH, 8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)));


        mvc.perform(get(GET_WEBINAR_BY_ID_PATH, 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id", is(5)))
                .andExpect(jsonPath("$.data[0].files.size()", is(0)));

        mvc.perform(get(GET_FILE_BY_ID_PATH, 8))
                .andExpect(status().isNotFound());

    }

    @Test
    public void web_putFiles_existedFiles() throws Exception {
        mvc.perform(put(PUT_UPDATE_FILES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_FILES_EXISTED_FILES_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("2_Second")))
                .andExpect(jsonPath("$.data[0].files.size()", is(3)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(3)))
                .andExpect(jsonPath("$.data[0].files[0].url", endsWith("UPDATED")))
                .andExpect(jsonPath("$.data[0].files[0].description", endsWith("UPDATED")))
                .andExpect(jsonPath("$.data[0].files[1].id", is(4)))
                .andExpect(jsonPath("$.data[0].files[1].url", endsWith("UPDATED")))
                .andExpect(jsonPath("$.data[0].files[1].description", endsWith("UPDATED")))
                .andExpect(jsonPath("$.data[0].files[2].id", is(5)))
                .andExpect(jsonPath("$.data[0].files[2].url", is("https://files.org/file5.ext")))
                .andExpect(jsonPath("$.data[0].files[2].description", is("Webinar 2 file")));
    }

    @Test
    public void web_putUpdateFiles_nonExistedFiles() throws Exception {
        mvc.perform(put(PUT_UPDATE_FILES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_FILES_NON_EXISTED_FILES_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("2_Second")))
                .andExpect(jsonPath("$.data[0].files.size()", is(3)))
                .andExpect(jsonPath("$.data[0].files[0].id", is(3)))
                .andExpect(jsonPath("$.data[0].files[0].url", is("https://files.org/file3.ext")))
                .andExpect(jsonPath("$.data[0].files[0].description", is("Webinar 2 file")))
                .andExpect(jsonPath("$.data[0].files[1].id", is(4)))
                .andExpect(jsonPath("$.data[0].files[1].url", is("https://files.org/file4.ext")))
                .andExpect(jsonPath("$.data[0].files[1].description", is("Webinar 2 file")))
                .andExpect(jsonPath("$.data[0].files[2].id", is(5)))
                .andExpect(jsonPath("$.data[0].files[2].url", is("https://files.org/file5.ext")))
                .andExpect(jsonPath("$.data[0].files[2].description", is("Webinar 2 file")));
    }

}
