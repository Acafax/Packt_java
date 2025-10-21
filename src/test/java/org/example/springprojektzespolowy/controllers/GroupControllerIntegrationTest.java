package org.example.springprojektzespolowy.controllers;

import org.example.springprojektzespolowy.config.TestSecurityConfig;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.repositories.GroupRepository;
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
@WithMockUser(username = "test-uid-001")
@Import(TestSecurityConfig.class)
class GroupControllerIntegrationTest {

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
    private GroupRepository groupRepository;

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
        populator.addScript(new ClassPathResource("data/setUpGroupIntagrationTestEnvirament.sql"));
        populator.execute(dataSource);
    }

    @Nested
    @DisplayName("Endpoint: GET /group/all")
    class GetAllGroupsTests {

        @Test
        @DisplayName("Should return list of all groups")
        void testGetAllGroups_whenGroupsExist_shouldReturnGroupList() throws Exception {
            mockMvc.perform(get("/group/all")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(5)))
                    .andExpect(jsonPath("$[0].groupName", is("Wakacje 2025")))
                    .andExpect(jsonPath("$[0].description", is("Wspólny wyjazd na wakacje do Grecji - zwiedzanie wysp, plaże i kultura")))
                    .andExpect(jsonPath("$[0].currency", is("PLN")))
                    .andExpect(jsonPath("$[0].maxBudget", is(5000.00)))
                    .andExpect(jsonPath("$[1].groupName", is("Projekt osobisty")))
                    .andExpect(jsonPath("$[1].currency", is("PLN")))
                    .andExpect(jsonPath("$[2].groupName", is("Wycieczka górska")))
                    .andExpect(jsonPath("$[2].currency", is("EUR")))
                    .andExpect(jsonPath("$[3].groupName", is("Weekend w SPA")))
                    .andExpect(jsonPath("$[4].groupName", is("Konferencja Tech 2025")))
                    .andExpect(jsonPath("$[4].currency", is("USD")));
        }

        @Test
        @DisplayName("Should return groups ordered by ID")
        void testGetAllGroups_shouldReturnGroupsOrderedById() throws Exception {
            mockMvc.perform(get("/group/all")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(5)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].groupName", is("Wakacje 2025")))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].groupName", is("Projekt osobisty")))
                    .andExpect(jsonPath("$[2].id", is(3)))
                    .andExpect(jsonPath("$[2].groupName", is("Wycieczka górska")))
                    .andExpect(jsonPath("$[3].id", is(4)))
                    .andExpect(jsonPath("$[3].groupName", is("Weekend w SPA")))
                    .andExpect(jsonPath("$[4].id", is(5)))
                    .andExpect(jsonPath("$[4].groupName", is("Konferencja Tech 2025")));
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /group/{id}")
    class GetGroupByIdTests {

        @Test
        @DisplayName("Should return group when exists")
        void testGetGroupById_whenGroupExists_shouldReturnGroup() throws Exception {
            mockMvc.perform(get("/group/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.groupName", is("Wakacje 2025")))
                    .andExpect(jsonPath("$.description", is("Wspólny wyjazd na wakacje do Grecji - zwiedzanie wysp, plaże i kultura")))
                    .andExpect(jsonPath("$.currency", is("PLN")))
                    .andExpect(jsonPath("$.maxBudget", is(5000.00)));
        }

        @Test
        @DisplayName("Should return 404 when group does not exist")
        void testGetGroupById_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            mockMvc.perform(get("/group/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return correct data for different groups")
        void testGetGroupById_shouldReturnCorrectDataForDifferentGroups() throws Exception {
            mockMvc.perform(get("/group/{id}", 3)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(3)))
                    .andExpect(jsonPath("$.groupName", is("Wycieczka górska")))
                    .andExpect(jsonPath("$.currency", is("EUR")))
                    .andExpect(jsonPath("$.maxBudget", is(3500.00)));
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /group/all/with-users/{groupId}")
    class GetGroupByIdWithUsersTests {

        @Test
        @DisplayName("Should return group with users")
        void testGetGroupByIdWithUsers_whenGroupExists_shouldReturnGroupWithUsers() throws Exception {
            mockMvc.perform(get("/group/all/with-users/{groupId}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.groupName", is("Wakacje 2025")))
                    .andExpect(jsonPath("$.users", notNullValue()))
                    .andExpect(jsonPath("$.users", hasSize(4)));
        }

        @Test
        @DisplayName("Should return group with one user")
        void testGetGroupByIdWithUsers_whenGroupHasOneUser_shouldReturnGroupWithOneUser() throws Exception {
            mockMvc.perform(get("/group/all/with-users/{groupId}", 2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.groupName", is("Projekt osobisty")))
                    .andExpect(jsonPath("$.users", notNullValue()))
                    .andExpect(jsonPath("$.users", hasSize(1)));
        }

        @Test
        @DisplayName("Should return 404 when group does not exist")
        void testGetGroupByIdWithUsers_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            mockMvc.perform(get("/group/all/with-users/{groupId}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /group/{groupId}/details")
    class GetGroupDetailsByIdTests {

        @Test
        @DisplayName("Should return group details")
        void testGetGroupDetailsById_whenGroupExists_shouldReturnGroupDetails() throws Exception {
            mockMvc.perform(get("/group/{groupId}/details", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.groupName", is("Wakacje 2025")))
                    .andExpect(jsonPath("$.description", is("Wspólny wyjazd na wakacje do Grecji - zwiedzanie wysp, plaże i kultura")));
        }

        @Test
        @DisplayName("Should return 404 when group does not exist")
        void testGetGroupDetailsById_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            mockMvc.perform(get("/group/{groupId}/details", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Endpoint: POST /group/create")
    class CreateGroupTests {

        @BeforeEach
        void setUpForPost() {
            jdbcTemplate.execute("DELETE FROM user_group");
            jdbcTemplate.execute("DELETE FROM groups");
            jdbcTemplate.execute("DELETE FROM users");

            jdbcTemplate.execute("INSERT INTO users (id, uid, name, email, country, date_of_birth) " +
                    "VALUES (1, 'test-uid-001', 'Jan Kowalski', 'jan.kowalski@example.com', 'Poland', '1990-05-15')");
        }

        @Test
        @DisplayName("Should create new group")
        void testCreateGroup_shouldCreateGroup() throws Exception {
            String newGroupJson = """
                    {
                      "groupName": "Grupa Testowa super",
                      "description": "Grupa Testowa",
                      "currency": "PLN",
                      "startDate": "2025-12-01T10:00:00",
                      "endDate": "2026-12-31T20:00:00",
                      "maxBudget":10000,
                      "guestEmails": []
                    }""";

            mockMvc.perform(post("/group/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newGroupJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.groupName", is("Grupa Testowa super")))
                    .andExpect(jsonPath("$.description", is("Grupa Testowa")))
                    .andExpect(jsonPath("$.currency", is("PLN")))
                    .andExpect(jsonPath("$.maxBudget", is(10000)));

            Assertions.assertEquals(1, groupRepository.findAll().size());
        }

        @Test
        @DisplayName("Should return location header with new group ID")
        void testCreateGroup_shouldReturnLocationHeader() throws Exception {
            String newGroupJson = """
                    {
                      "groupName": "Grupa Testowa super",
                      "description": "Grupa Testowa",
                      "currency": "KOKO",
                      "startDate": "2025-12-01T10:00:00",
                      "endDate": "2026-12-31T20:00:00",
                      "maxBudget":10000,
                      "guestEmails": []
                    }""";

            mockMvc.perform(post("/group/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newGroupJson))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"));
        }
    }

    @Nested
    @DisplayName("Endpoint: PUT /group/update")
    class UpdateGroupTests {

        @Test
        @DisplayName("Should update group data when group exists")
        void testUpdateGroup_whenGroupExists_shouldUpdateGroupData() throws Exception {
            String updateGroupJson = """
                    {
                        "id": 1,
                        "groupName": "Wakacje 2025 - Zaktualizowane",
                        "description": "Nowy opis wakacji",
                        "currency": "EUR",
                        "maxBudget": 6000.00,
                        "startDate": "2025-07-01T00:00:00",
                        "endDate": "2025-07-20T23:59:59",
                        "groupPhotoId": null
                    }""";

            mockMvc.perform(put("/group/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateGroupJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.groupName", is("Wakacje 2025 - Zaktualizowane")))
                    .andExpect(jsonPath("$.description", is("Nowy opis wakacji")))
                    .andExpect(jsonPath("$.currency", is("EUR")))
                    .andExpect(jsonPath("$.maxBudget", is(6000.00)));

            Group group = groupRepository.findById(1L).orElse(null);
            Assertions.assertNotNull(group);
            Assertions.assertEquals("Wakacje 2025 - Zaktualizowane", group.getName());
            Assertions.assertEquals("EUR", group.getCurrency());
        }

        @Test
        @DisplayName("Should return 404 when group does not exist")
        void testUpdateGroup_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            String updateGroupJson = """
                    {
                        "id": 999,
                        "groupName": "Nieistniejąca Grupa",
                        "description": "Test",
                        "currency": "PLN",
                        "maxBudget": 1000.00,
                        "startDate": "2025-01-01T00:00:00",
                        "endDate": "2025-12-31T23:59:59",
                        "groupPhotoId": null
                    }""";

            mockMvc.perform(put("/group/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateGroupJson))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should update various group fields")
        void testUpdateGroup_shouldUpdateVariousFields() throws Exception {
            String updateGroupJson = """
                    {
                        "id": 2,
                        "groupName": "Projekt osobisty - Updated",
                        "description": "Zmieniony opis projektu",
                        "currency": "USD",
                        "maxBudget": 3000.00,
                        "startDate": "2025-01-01T00:00:00",
                        "endDate": "2025-12-31T23:59:59",
                        "groupPhotoId": null
                    }""";

            mockMvc.perform(put("/group/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateGroupJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.groupName", is("Projekt osobisty - Updated")))
                    .andExpect(jsonPath("$.currency", is("USD")))
                    .andExpect(jsonPath("$.maxBudget", is(3000.00)));
        }
    }

    @Nested
    @DisplayName("Endpoint: PATCH /group/patch/{groupId}")
    class PatchGroupTests {

        @Test
        @DisplayName("Should update partial group data when group exists")
        void testPatchGroup_whenGroupExists_shouldUpdateGroupData() throws Exception {
            String patchGroupJson = """
                    {
                        "groupName": "Wycieczka górska - PATCHED",
                        "maxBudget": 4000.00
                    }""";

            mockMvc.perform(patch("/group/patch/{groupId}", 3)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(patchGroupJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(3)))
                    .andExpect(jsonPath("$.groupName", is("Wycieczka górska - PATCHED")))
                    .andExpect(jsonPath("$.maxBudget", is(4000.00)))
                    .andExpect(jsonPath("$.currency", is("EUR")))
                    .andExpect(jsonPath("$.description", is("Wspinaczka w Tatrach - sprzęt, noclegi, przewodnicy")));

            Group group = groupRepository.findById(3L).orElse(null);
            Assertions.assertNotNull(group);
            Assertions.assertEquals("Wycieczka górska - PATCHED", group.getName());
            Assertions.assertEquals("EUR", group.getCurrency());
        }

        @Test
        @DisplayName("Should update only description")
        void testPatchGroup_shouldUpdateOnlyDescription() throws Exception {
            String patchGroupJson = """
                    {
                        "description": "Nowy opis weekendu w SPA"
                    }""";

            mockMvc.perform(patch("/group/patch/{groupId}", 4)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(patchGroupJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(4)))
                    .andExpect(jsonPath("$.groupName", is("Weekend w SPA")))
                    .andExpect(jsonPath("$.description", is("Nowy opis weekendu w SPA")))
                    .andExpect(jsonPath("$.currency", is("PLN")));
        }

        @Test
        @DisplayName("Should return 404 when group does not exist")
        void testPatchGroup_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            String patchGroupJson = """
                    {
                        "groupName": "Updated Name",
                        "maxBudget": 5000.00
                    }""";

            mockMvc.perform(patch("/group/patch/{groupId}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(patchGroupJson))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should update currency and budget")
        void testPatchGroup_shouldUpdateCurrencyAndBudget() throws Exception {
            String patchGroupJson = """
                    {
                        "currency": "GBP",
                        "maxBudget": 7500.00
                    }""";

            mockMvc.perform(patch("/group/patch/{groupId}", 5)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(patchGroupJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.currency", is("GBP")))
                    .andExpect(jsonPath("$.maxBudget", is(7500.00)))
                    .andExpect(jsonPath("$.groupName", is("Konferencja Tech 2025")));
        }
    }

    @Nested
    @DisplayName("Endpoint: DELETE /group/{id}")
    class DeleteGroupTests {

        @Test
        @DisplayName("DELETE /group/{id} - powinien poprawnie usunąć grupę")
        void testDeleteGroup_whenGroupExists_shouldDeleteGroup() throws Exception {
            Long groupIdToDelete = 2L;

            Group groupBeforeDelete = groupRepository.findById(groupIdToDelete).orElse(null);
            Assertions.assertNotNull(groupBeforeDelete);

            mockMvc.perform(delete("/group/{id}", groupIdToDelete)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(2)))
                    .andExpect(jsonPath("$.groupName", is("Projekt osobisty")))
                    .andExpect(jsonPath("$.description", is("Mój projekt startupowy - aplikacja mobilna")));

            Group groupAfterDelete = groupRepository.findById(groupIdToDelete).orElse(null);
            Assertions.assertNull(groupAfterDelete);
        }

        @Test
        @DisplayName("Should return 404 when group does not exist")
        void testDeleteGroup_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            Long nonexistentGroupId = 999L;

            mockMvc.perform(delete("/group/{id}", nonexistentGroupId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should delete group along with user_group relations")
        void testDeleteGroup_shouldDeleteGroupWithRelations() throws Exception {
            Long groupIdToDelete = 4L;

            Integer relationsBefore = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_group WHERE group_id = ?",
                    Integer.class,
                    groupIdToDelete
            );
            Assertions.assertEquals(5, relationsBefore);

            mockMvc.perform(delete("/group/{id}", groupIdToDelete))
                    .andExpect(status().isOk());

            Integer relationsAfter = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_group WHERE group_id = ?",
                    Integer.class,
                    groupIdToDelete
            );
            Assertions.assertEquals(0, relationsAfter);
        }

        @Test
        @DisplayName("Other groups should remain intact")
        void testDeleteGroup_otherGroupsShouldRemainIntact() throws Exception {
            Long groupIdToDelete = 3L;
            Assertions.assertEquals(5, groupRepository.findAll().size());

            mockMvc.perform(delete("/group/{id}", groupIdToDelete))
                    .andExpect(status().isOk());

            Assertions.assertEquals(4, groupRepository.findAll().size());

            Assertions.assertTrue(groupRepository.findById(1L).isPresent());
            Assertions.assertTrue(groupRepository.findById(2L).isPresent());
            Assertions.assertFalse(groupRepository.findById(3L).isPresent());
            Assertions.assertTrue(groupRepository.findById(4L).isPresent());
            Assertions.assertTrue(groupRepository.findById(5L).isPresent());
        }
    }

    @Nested
    @DisplayName("HTTP Status Tests for different scenarios")
    class HttpStatusTests {

        @Test
        @DisplayName("Should return 200 OK for successful GET request")
        void testSuccessfulGetRequest_shouldReturn200() throws Exception {
            mockMvc.perform(get("/group/all"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND for non-existent resource")
        void testNotFoundResource_shouldReturn404() throws Exception {
            mockMvc.perform(get("/group/{id}", 999))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 201 CREATED for successful resource creation")
        void testSuccessfulCreation_shouldReturn201() throws Exception {
            jdbcTemplate.execute("DELETE FROM user_group");
            jdbcTemplate.execute("DELETE FROM groups");
            jdbcTemplate.execute("DELETE FROM users");
            jdbcTemplate.execute("INSERT INTO users (id, uid, name, email, country, date_of_birth) " +
                    "VALUES (1, 'test-uid-001', 'Jan Kowalski', 'jan.kowalski@example.com', 'Poland', '1990-05-15')");

            String newGroupJson = """
                    {
                      "groupName": "Grupa Testowa super",
                      "description": "Grupa Testowa dgf",
                      "currency": "KOKO",
                      "startDate": "2025-12-01T10:00:00",
                      "endDate": "2026-12-31T20:00:00",
                      "maxBudget":10000,
                      "guestEmails": []
                    }""";

            mockMvc.perform(post("/group/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newGroupJson))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should return 200 OK for successful resource update")
        void testSuccessfulUpdate_shouldReturn200() throws Exception {
            String updateGroupJson = """
                    {
                        "id": 1,
                        "groupName": "Updated",
                        "description": "Test",
                        "currency": "PLN",
                        "maxBudget": 5000.00,
                        "startDate": "2025-07-01T00:00:00",
                        "endDate": "2025-07-15T23:59:59",
                        "groupPhotoId": null
                    }""";

            mockMvc.perform(put("/group/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateGroupJson))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 200 OK for successful PATCH")
        void testSuccessfulPatch_shouldReturn200() throws Exception {
            String patchGroupJson = """
                    {
                        "groupName": "Patched Name"
                    }""";

            mockMvc.perform(patch("/group/patch/{groupId}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(patchGroupJson))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 200 OK for successful DELETE")
        void testSuccessfulDelete_shouldReturn200() throws Exception {
            mockMvc.perform(delete("/group/{id}", 1))
                    .andExpect(status().isOk());
        }
    }
}
