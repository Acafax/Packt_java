package org.example.springprojektzespolowy.controllers;

import org.example.springprojektzespolowy.config.TestSecurityConfig;
import org.example.springprojektzespolowy.models.Invitation;
import org.example.springprojektzespolowy.repositories.InvitationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testUser")
@Import(TestSecurityConfig.class)
class InvitationControllerIntegrationTest {

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
    private InvitationRepository invitationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM invitation");
        jdbcTemplate.execute("DELETE FROM user_group");
        jdbcTemplate.execute("DELETE FROM groups");
        jdbcTemplate.execute("DELETE FROM users");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("data/users.sql"));
        populator.addScript(new ClassPathResource("data/group.sql"));
        populator.addScript(new ClassPathResource("data/invitations.sql"));
        populator.execute(dataSource);
    }

    @Nested
    @DisplayName("Endpoint: POST /invitation/invite/{email}/{groupId}")
    class InviteUserTests {

        @Test
        @WithMockUser(username = "test-uid-001")
        @DisplayName("Should invite user to group")
        void testInviteUser_whenUserAndGroupExist_shouldCreateInvitation() throws Exception {
            String emailToInvite = "anna.nowak@example.com";
            Long groupId = 1L;

            mockMvc.perform(post("/invitation/invite/{email}/{groupId}", emailToInvite, groupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.invitedUser.email", is(emailToInvite)))
                    .andExpect(jsonPath("$.invitedUser.userName", is("Anna Nowak")))
                    .andExpect(jsonPath("$.group.id", is(1)))
                    .andExpect(jsonPath("$.group.groupName", is("Wakacje 2025")))
                    .andExpect(jsonPath("$.inviter.UId", is("test-uid-001")))
                    .andExpect(jsonPath("$.inviter.userName", is("Jan Kowalski")));

            Invitation invitation = invitationRepository.findByUser_UIdAndGroup_Id("test-uid-002", groupId);
            Assertions.assertNotNull(invitation);
            Assertions.assertEquals("test-uid-001", invitation.getInviterUId());
        }

        @Test
        @WithMockUser(username = "test-uid-001")
        @DisplayName("Should return 404 when user does not exist")
        void testInviteUser_whenUserDoesNotExist_shouldReturn404() throws Exception {
            String nonexistentEmail = "nieistniejacy@example.com";
            Long groupId = 1L;

            mockMvc.perform(post("/invitation/invite/{email}/{groupId}", nonexistentEmail, groupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test-uid-001")
        @DisplayName("Should return 404 when group does not exist")
        void testInviteUser_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            String emailToInvite = "anna.nowak@example.com";
            Long nonexistentGroupId = 999L;

            mockMvc.perform(post("/invitation/invite/{email}/{groupId}", emailToInvite, nonexistentGroupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }


        @Test
        @WithMockUser(username = "test-uid-003")
        @DisplayName("Group 2 admin should be able to invite to their group")
        void testInviteUser_whenAdminOfGroup2InvitesUser_shouldCreateInvitation() throws Exception {
            String emailToInvite = "jan.kowalski@example.com";
            Long groupId = 2L;

            mockMvc.perform(post("/invitation/invite/{email}/{groupId}", emailToInvite, groupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.invitedUser.email", is(emailToInvite)))
                    .andExpect(jsonPath("$.group.id", is(2)))
                    .andExpect(jsonPath("$.group.groupName", is("Projekt osobisty")))
                    .andExpect(jsonPath("$.inviter.UId", is("test-uid-003")));

            Invitation invitation = invitationRepository.findByUser_UIdAndGroup_Id("test-uid-001", groupId);
            Assertions.assertNotNull(invitation);
            Assertions.assertEquals("test-uid-003", invitation.getInviterUId());
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /invitation/{UId}")
    class ShowInvitationsTests {

        @Test
        @WithMockUser(username = "test-uid-002")
        @DisplayName("Should return list of user invitations")
        void testShowInvitations_whenInvitationsExist_shouldReturnInvitationList() throws Exception {
            String uid = "test-uid-002";

            mockMvc.perform(get("/invitation/{Uid}", uid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].invitedUser.UId", is("test-uid-002")))
                    .andExpect(jsonPath("$[0].invitedUser.userName", is("Anna Nowak")))
                    .andExpect(jsonPath("$[0].group.id", is(2)))
                    .andExpect(jsonPath("$[0].group.groupName", is("Projekt osobisty")))
                    .andExpect(jsonPath("$[0].inviter.UId", is("test-uid-003")))
                    .andExpect(jsonPath("$[0].inviter.userName", is("Piotr Wiśniewski")));
        }

        @Test
        @WithMockUser(username = "test-uid-003")
        @DisplayName("Should return invitation for user 3")
        void testShowInvitations_whenUser3HasInvitation_shouldReturnInvitation() throws Exception {
            String uid = "test-uid-003";

            mockMvc.perform(get("/invitation/{Uid}", uid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].invitedUser.UId", is("test-uid-003")))
                    .andExpect(jsonPath("$[0].invitedUser.userName", is("Piotr Wiśniewski")))
                    .andExpect(jsonPath("$[0].group.id", is(1)))
                    .andExpect(jsonPath("$[0].group.groupName", is("Wakacje 2025")))
                    .andExpect(jsonPath("$[0].inviter.UId", is("test-uid-001")))
                    .andExpect(jsonPath("$[0].inviter.userName", is("Jan Kowalski")));
        }

        @Test
        @WithMockUser(username = "test-uid-001")
        @DisplayName("Should return empty list when user has no invitations")
        void testShowInvitations_whenNoInvitations_shouldReturnEmptyList() throws Exception {
            String uid = "test-uid-001";

            mockMvc.perform(get("/invitation/{Uid}", uid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Endpoint: DELETE /invitation/{UId}/{groupId}")
    class DeleteInvitationTests {

        @Test
        @WithMockUser(username = "test-uid-002")
        @DisplayName("Should delete invitation")
        void testDeleteInvitation_whenInvitationExists_shouldDeleteInvitation() throws Exception {
            String uid = "test-uid-002";
            Long groupId = 2L;

            Invitation invitationBefore = invitationRepository.findByUser_UIdAndGroup_Id(uid, groupId);
            Assertions.assertNotNull(invitationBefore);

            mockMvc.perform(delete("/invitation/{UId}/{groupId}", uid, groupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.user.UId", is(uid)))
                    .andExpect(jsonPath("$.user.userName", is("Anna Nowak")))
                    .andExpect(jsonPath("$.group.id", is(2)))
                    .andExpect(jsonPath("$.group.groupName", is("Projekt osobisty")));

            Invitation invitationAfter = invitationRepository.findByUser_UIdAndGroup_Id(uid, groupId);
            Assertions.assertNull(invitationAfter);
        }

        @Test
        @WithMockUser(username = "test-uid-002")
        @DisplayName("Should return 404 when invitation does not exist")
        void testDeleteInvitation_whenInvitationDoesNotExist_shouldReturn404() throws Exception {
            String uid = "test-uid-002";
            Long groupId = 1L;

            mockMvc.perform(delete("/invitation/{UId}/{groupId}", uid, groupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }


        @Test
        @WithMockUser(username = "test-uid-003")
        @DisplayName("Should successfully delete invitation for user 3")
        void testDeleteInvitation_whenUser3DeletesOwnInvitation_shouldDeleteSuccessfully() throws Exception {
            String uid = "test-uid-003";
            Long groupId = 1L;

            Invitation invitationBefore = invitationRepository.findByUser_UIdAndGroup_Id(uid, groupId);
            Assertions.assertNotNull(invitationBefore);

            mockMvc.perform(delete("/invitation/{UId}/{groupId}", uid, groupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.user.UId", is(uid)))
                    .andExpect(jsonPath("$.user.userName", is("Piotr Wiśniewski")))
                    .andExpect(jsonPath("$.group.id", is(1)))
                    .andExpect(jsonPath("$.group.groupName", is("Wakacje 2025")));

            Invitation invitationAfter = invitationRepository.findByUser_UIdAndGroup_Id(uid, groupId);
            Assertions.assertNull(invitationAfter);
        }

        @Test
        @WithMockUser(username = "test-uid-002")
        @DisplayName("Should return 404 for non-existent group")
        void testDeleteInvitation_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            String uid = "test-uid-002";
            Long nonexistentGroupId = 999L;

            mockMvc.perform(delete("/invitation/{UId}/{groupId}", uid, nonexistentGroupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("HTTP Status Tests")
    class HttpStatusTests {

        @Test
        @WithMockUser(username = "test-uid-001")
        @DisplayName("Should return 200 status for successful invitation")
        void testInviteUser_shouldReturnStatus200() throws Exception {
            mockMvc.perform(post("/invitation/invite/{email}/{groupId}",
                            "anna.nowak@example.com", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test-uid-002")
        @DisplayName("Should return 200 status for valid request")
        void testShowInvitations_shouldReturnStatus200() throws Exception {
            mockMvc.perform(get("/invitation/{Uid}", "test-uid-002")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test-uid-002")
        @DisplayName("Should return 200 status for successful deletion")
        void testDeleteInvitation_shouldReturnStatus200() throws Exception {
            mockMvc.perform(delete("/invitation/{UId}/{groupId}", "test-uid-002", 2L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test-uid-001")
        @DisplayName("Should return 404 status for non-existent user")
        void testInviteUser_shouldReturnStatus404() throws Exception {
            mockMvc.perform(post("/invitation/invite/{email}/{groupId}",
                            "nieistniejacy@example.com", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Multiple Invitations Tests")
    class MultipleInvitationsTests {

        @Test
        @WithMockUser(username = "test-uid-001")
        @DisplayName("User with multiple invitations - should return all")
        void testShowInvitations_whenUserHasMultipleInvitations_shouldReturnAll() throws Exception {
            jdbcTemplate.execute("INSERT INTO invitation (user_id, group_id, inviter_uid) VALUES (1, 2, 'test-uid-003')");

            mockMvc.perform(get("/invitation/{Uid}", "test-uid-001")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].group.id", is(2)));
        }

        @Test
        @WithMockUser(username = "test-uid-001")
        @DisplayName("Admin can invite multiple users to same group")
        void testInviteUser_adminCanInviteMultipleUsersToSameGroup() throws Exception {
            String email1 = "anna.nowak@example.com";
            String email2 = "piotr.wisniewski@example.com";
            Long groupId = 1L;

            mockMvc.perform(post("/invitation/invite/{email}/{groupId}", email1, groupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.invitedUser.email", is(email1)));

        }
    }
}

