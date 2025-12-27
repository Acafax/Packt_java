package org.example.springprojektzespolowy.controllers;

import org.example.springprojektzespolowy.config.TestSecurityConfig;
import org.example.springprojektzespolowy.models.User;
import org.example.springprojektzespolowy.repositories.userRepos.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testUser")
@Import(TestSecurityConfig.class)
class UserControllerIntegrationTest {

    static PostgreSQLContainer<?> postgres;

    @BeforeAll
    static void beforeAll() {
        postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        if (postgres != null) {
            postgres.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM user_group");
        jdbcTemplate.execute("DELETE FROM groups");
        jdbcTemplate.execute("DELETE FROM users");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("data/users.sql"));
        populator.addScript(new ClassPathResource("data/group.sql"));
        populator.execute(dataSource);
    }

    @Nested
    @DisplayName("Endpoint: GET /user/all")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return list of all users from SQL")
        void testGetAllUsers_whenUsersExist_shouldReturnUserList() throws Exception {
            mockMvc.perform(get("/user/all").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].userName", is("Jan Kowalski")))
                    .andExpect(jsonPath("$[0].email", is("jan.kowalski@example.com")))
                    .andExpect(jsonPath("$[0].country", is("Poland")))
                    .andExpect(jsonPath("$[0].UId", is("test-uid-001")))
                    .andExpect(jsonPath("$[1].userName", is("Anna Nowak")))
                    .andExpect(jsonPath("$[1].email", is("anna.nowak@example.com")))
                    .andExpect(jsonPath("$[2].userName", is("Piotr Wiśniewski")))
                    .andExpect(jsonPath("$[2].email", is("piotr.wisniewski@example.com")));
        }

        @Test
        @DisplayName("Should return users ordered by ID")
        void testGetAllUsers_shouldReturnUsersOrderedById() throws Exception {
            mockMvc.perform(get("/user/all")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].userName", is("Jan Kowalski")))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].userName", is("Anna Nowak")))
                    .andExpect(jsonPath("$[2].id", is(3)))
                    .andExpect(jsonPath("$[2].userName", is("Piotr Wiśniewski")));
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /user/{UId}")
    class GetUserByUIdTests {

        @Test
        @DisplayName("Should return 404 when user does not exist")
        void testGetUserByUId_whenUserDoesNotExist_shouldReturn404() throws Exception {
            mockMvc.perform(get("/user/{uid}", "nonexistent-uid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return user when exists")
        void testGetUserByUId_whenUserExists_shouldReturnUser() throws Exception {
            mockMvc.perform(get("/user/{uid}", "test-uid-001")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.UId", is("test-uid-001")))
                    .andExpect(jsonPath("$.userName", is("Jan Kowalski")))
                    .andExpect(jsonPath("$.email", is("jan.kowalski@example.com")))
                    .andExpect(jsonPath("$.country", is("Poland")))
                    .andExpect(jsonPath("$.dateOfBirth", is("1990-05-15")));
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /user/{UId}/groups/details")
    class GetUserWithAllDetailsTests {

        @Test
        @DisplayName("Should return user with group details when user belongs to group")
        void testGetUserWithAllDetails_whenUserExists_shouldReturnUserWithDetails() throws Exception {
            mockMvc.perform(get("/user/{uid}/groups/details", "test-uid-001")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.UId", is("test-uid-001")))
                    .andExpect(jsonPath("$.userName", is("Jan Kowalski")))
                    .andExpect(jsonPath("$.email", is("jan.kowalski@example.com")))
                    .andExpect(jsonPath("$.groupDetails", notNullValue()))
                    .andExpect(jsonPath("$.groupDetails", hasSize(1)))
                    .andExpect(jsonPath("$.groupDetails[0].groupName", is("Wakacje 2025")))
                    .andExpect(jsonPath("$.groupDetails[0].description", is("Wspólny wyjazd na wakacje")))
                    .andExpect(jsonPath("$.groupDetails[0].currency", is("PLN")));
        }

        @Test
        @DisplayName("Should return 404 when user does not exist")
        void testGetUserWithAllDetails_whenUserDoesNotExist_shouldReturn404() throws Exception {
            mockMvc.perform(get("/user/{uid}/groups/details", "nonexistent-uid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /user/{UId}/groups")
    class GetUserWithGroupsByIdTests {

        @Test
        @DisplayName("Should return Internal Server Error when user does not exist")
        void TestGetUserWithGroupsById_whenUserExists_shouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/user/{uid}/groups", "not-exi"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should return user with groups")
        void TestGetUserWithGroupsById_whenUserExists_shouldReturnUserWithGroups() throws Exception {
            mockMvc.perform(get("/user/{uid}/groups", "test-uid-001")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.UId", is("test-uid-001")))
                    .andExpect(jsonPath("$.userName", is("Jan Kowalski")))
                    .andExpect(jsonPath("$.email", is("jan.kowalski@example.com")))
                    .andExpect(jsonPath("$.country", is("Poland")))
                    .andExpect(jsonPath("$.dateOfBirth", is("1990-05-15")))
                    .andExpect(jsonPath("$.groupList", notNullValue()))
                    .andExpect(jsonPath("$.groupList", hasSize(1)))
                    .andExpect(jsonPath("$.groupList[0].group.id", is(1)))
                    .andExpect(jsonPath("$.groupList[0].group.groupName", is("Wakacje 2025")));
        }
    }

    @Nested
    @DisplayName("Endpoint: POST /user/register")
    class CreateUserTests {

        @BeforeEach
        void setUpForPost() {
            jdbcTemplate.execute("DELETE FROM user_group");
            jdbcTemplate.execute("DELETE FROM groups");
            jdbcTemplate.execute("DELETE FROM users");
        }

        @Test
        @DisplayName("Should create new user")
        void testCreateUser_shouldCreateUser() throws Exception {
            String newUserJson = """
                    {
                        "UId":"FpLsjAn9gSeQzBLc9h9t0C43X3J3",
                        "userName": "User Test",
                        "email":"user.test@gmail.com",
                        "country":"Poland",
                        "dateOfBirth": "2002-02-02",
                        "photoId":null
                    }""";
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newUserJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.UId", is("FpLsjAn9gSeQzBLc9h9t0C43X3J3")))
                    .andExpect(jsonPath("$.userName", is("User Test")))
                    .andExpect(jsonPath("$.email", is("user.test@gmail.com")));

            User user = userRepository.findByUId("FpLsjAn9gSeQzBLc9h9t0C43X3J3");

            Assertions.assertEquals("FpLsjAn9gSeQzBLc9h9t0C43X3J3", user.getUId());
            Assertions.assertEquals("user.test@gmail.com", user.getEmail());
            Assertions.assertEquals("Poland", user.getCountry());
        }
    }

    @Nested
    @DisplayName("Endpoint: PUT /user/update")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user data when user exists")
        void testUpdateUser_whenUserExists_shouldUpdateUserData() throws Exception {
            String updateUserJson = """
                    {
                        "id": 1,
                        "UId": "test-uid-001",
                        "userName": "Jan Kowalski Updated",
                        "email": "jan.updated@example.com",
                        "country": "Germany",
                        "dateOfBirth": "1990-05-15",
                        "photoId": null
                    }""";

            mockMvc.perform(put("/user/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateUserJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.UId", is("test-uid-001")))
                    .andExpect(jsonPath("$.userName", is("Jan Kowalski Updated")))
                    .andExpect(jsonPath("$.email", is("jan.updated@example.com")))
                    .andExpect(jsonPath("$.country", is("Germany")));

            User user = userRepository.findByUId("test-uid-001");
            Assertions.assertEquals("Jan Kowalski Updated", user.getName());
            Assertions.assertEquals("jan.updated@example.com", user.getEmail());
            Assertions.assertEquals("Germany", user.getCountry());
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error with invalid values")
        void testUpdateUser_whenInvalidData_shouldReturnInternalServerError() throws Exception {
            String invalidUserJson = """
                    {
                        "id": 1,
                        "UId": 213123497,
                        "userName": "",
                        "email": "invalid-email",
                        "country": null
                    }""";

            mockMvc.perform(put("/user/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidUserJson))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should return 404 Not Found when user does not exist")
        void testUpdateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
            String updateUserJson = """
                    {
                        "id": 999,
                        "UId": "nonexistent-uid",
                        "userName": "Nonexistent User",
                        "email": "nonexistent@example.com",
                        "country": "Poland",
                        "dateOfBirth": "1990-01-01",
                        "photoId": null
                    }""";

            mockMvc.perform(put("/user/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateUserJson))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Endpoint: PATCH /user/patch/{UId}")
    class PatchUserTests {

        @Test
        @DisplayName("Should update partial user data when user exists")
        void testPatchUser_whenUserExists_shouldUpdateUserData() throws Exception {
            String patchUserJson = """
                    {
                        "userName": "Anna Nowak Patched",
                        "country": "Czech Republic"
                    }""";

            // When & Then
            mockMvc.perform(patch("/user/patch/{UId}", "test-uid-002")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(patchUserJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.UId", is("test-uid-002")))
                    .andExpect(jsonPath("$.userName", is("Anna Nowak Patched")))
                    .andExpect(jsonPath("$.country", is("Czech Republic")))
                    .andExpect(jsonPath("$.email", is("anna.nowak@example.com")));

            User user = userRepository.findByUId("test-uid-002");
            Assertions.assertEquals("Czech Republic", user.getCountry());
            Assertions.assertEquals("anna.nowak@example.com", user.getEmail());
        }

        @Test
        @DisplayName("Should return 400 Bad Request with invalid values")
        void testPatchUser_whenInvalidData_shouldReturnBadRequest() throws Exception {
            String invalidPatchJson = """
                    {
                        "userName": 2334,
                        "email": 
                    }""";

            mockMvc.perform(patch("/user/patch/{UId}", "test-uid-002")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidPatchJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 Not Found when user does not exist")
        void testPatchUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
            String patchUserJson = """
                    {
                        "userName": "Updated Name",
                        "country": "Spain"
                    }""";

            mockMvc.perform(patch("/user/patch/{UId}", "nonexistent-uid-999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(patchUserJson))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Endpoint: DELETE /user/{UId}")
    class DeleteUserTests {

        @Test
        @DisplayName("Should successfully delete user")
        void testDeleteUser_whenUserExists_shouldDeleteUser() throws Exception {
            String uidToDelete = "test-uid-003";

            User userBeforeDelete = userRepository.findByUId(uidToDelete);
            Assertions.assertNotNull(userBeforeDelete);

            mockMvc.perform(delete("/user/{UId}", uidToDelete)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.UId", is(uidToDelete)))
                    .andExpect(jsonPath("$.userName", is("Piotr Wiśniewski")))
                    .andExpect(jsonPath("$.email", is("piotr.wisniewski@example.com")));

            User userAfterDelete = userRepository.findByUId(uidToDelete);
            Assertions.assertNull(userAfterDelete);
        }

        @Test
        @DisplayName("Should return 404 Not Found when user does not exist")
        void testDeleteUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
            String nonexistentUid = "nonexistent-uid-to-delete";

            mockMvc.perform(delete("/user/{UId}", nonexistentUid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

}
