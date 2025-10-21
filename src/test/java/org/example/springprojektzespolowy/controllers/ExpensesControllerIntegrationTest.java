package org.example.springprojektzespolowy.controllers;

import org.example.springprojektzespolowy.config.TestSecurityConfig;
import org.example.springprojektzespolowy.models.Expense;
import org.example.springprojektzespolowy.repositories.expenseRepos.ExpensesRepository;
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

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "test-uid-001")
@Import(TestSecurityConfig.class)
class ExpensesControllerIntegrationTest {

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
    private ExpensesRepository expensesRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM expenses_user");
        jdbcTemplate.execute("DELETE FROM expenses_document");
        jdbcTemplate.execute("DELETE FROM expenses_event");
        jdbcTemplate.execute("DELETE FROM expense");
        jdbcTemplate.execute("DELETE FROM user_group");
        jdbcTemplate.execute("DELETE FROM groups");
        jdbcTemplate.execute("DELETE FROM users");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("data/users.sql"));
        populator.addScript(new ClassPathResource("data/group.sql"));
        populator.addScript(new ClassPathResource("data/expenses.sql"));
        populator.execute(dataSource);
    }

    @Nested
    @DisplayName("Endpoint: GET /budget/{groupId}")
    class GetExpensesTests {

        @Test
        @DisplayName("Should return list of expenses for group")
        void testGetExpenses_whenGroupExists_shouldReturnExpenseList() throws Exception {
            mockMvc.perform(get("/budget/{groupId}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].name", is("Zakupy spożywcze")))
                    .andExpect(jsonPath("$[0].category", is("Żywność")))
                    .andExpect(jsonPath("$[0].price", is(250.50)))
                    .andExpect(jsonPath("$[0].creator", is("test-uid-001")))
                    .andExpect(jsonPath("$[1].name", is("Bilety lotnicze")))
                    .andExpect(jsonPath("$[1].category", is("Transport")))
                    .andExpect(jsonPath("$[1].price", is(1500.00)))
                    .andExpect(jsonPath("$[2].name", is("Hotel")))
                    .andExpect(jsonPath("$[2].category", is("Zakwaterowanie")))
                    .andExpect(jsonPath("$[2].price", is(2000.00)));
        }

        @Test
        @DisplayName("Should return 404 when group does not exist")
        void testGetExpenses_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            mockMvc.perform(get("/budget/{groupId}", 999L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return empty list when group has no expenses")
        void testGetExpenses_whenGroupHasNoExpenses_shouldReturnEmptyList() throws Exception {
            mockMvc.perform(get("/budget/{groupId}", 2L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /budget/{groupId}/{expId}")
    class GetExpenseByIdTests {

        @Test
        @DisplayName("Should return expense when exists")
        void testGetExpenseById_whenExpenseExists_shouldReturnExpense() throws Exception {
            mockMvc.perform(get("/budget/{groupId}/{expId}", 1L, 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Zakupy spożywcze")))
                    .andExpect(jsonPath("$.description", is("Zakupy w supermarkecie")))
                    .andExpect(jsonPath("$.category", is("Żywność")))
                    .andExpect(jsonPath("$.price", is(250.50)))
                    .andExpect(jsonPath("$.creator", is("test-uid-001")));
        }

        @Test
        @DisplayName("Should return 404 when expense does not exist")
        void testGetExpenseById_whenExpenseDoesNotExist_shouldReturn404() throws Exception {
            mockMvc.perform(get("/budget/{groupId}/{expId}", 1L, 999L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Endpoint: POST /budget/{groupId}")
    class CreateExpenseTests {

        @Test
        @DisplayName("Should create new expense")
        void testCreateExpense_shouldCreateExpense() throws Exception {
            String newExpenseJson = """
                    {
                        "name": "Restauracja",
                        "description": "Wspólny obiad",
                        "category": "Jedzenie",
                        "price": 350.00,
                        "dateOfExpense": "2025-01-15T18:00:00",
                        "participants": ["test-uid-001", "test-uid-002"]
                    }""";

            mockMvc.perform(post("/budget/{groupId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newExpenseJson))
                    .andExpect(status().isOk());

            Optional<Expense> expenseOpt = expensesRepository.findById(4L);
            Expense expense = expenseOpt.orElse(null);
            Assertions.assertNotNull(expense);
            Assertions.assertEquals("Wspólny obiad", expense.getDescription());
            Assertions.assertEquals("Jedzenie", expense.getCategory());
        }

        @Test
        @DisplayName("Should return 404 when group does not exist")
        void testCreateExpense_whenGroupDoesNotExist_shouldReturn404() throws Exception {
            String newExpenseJson = """
                    {
                        "name": "Test Expense",
                        "description": "Test",
                        "category": "Test",
                        "price": 100.00,
                        "dateOfExpense": "2025-01-15T18:00:00",
                        "participants": ["test-uid-001"]
                    }""";

            mockMvc.perform(post("/budget/{groupId}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newExpenseJson))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 with invalid data")
        void testCreateExpense_whenInvalidData_shouldReturn400() throws Exception {
            String invalidExpenseJson = """
                    {
                        "name": "",
                        "description": null,
                        "category": "",
                        "price": -100.00,
                        "dateOfExpense": null,
                        "participants": []
                    }""";

            mockMvc.perform(post("/budget/{groupId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidExpenseJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Endpoint: PUT /budget")
    class UpdateExpenseTests {

        @Test
        @DisplayName("Should update expense")
        void testUpdateExpense_whenExpenseExists_shouldUpdateExpense() throws Exception {
            String updateExpenseJson = """
                    {
                        "id": 1,
                        "name": "Zakupy spożywcze zaktualizowane",
                        "description": "Zaktualizowany opis",
                        "category": "Żywność",
                        "price": 300.00,
                        "dateOfExpense": "2025-01-10T10:00:00",
                        "creator": "test-uid-001",
                        "participants": ["test-uid-001", "test-uid-002"]
                    }""";

            mockMvc.perform(put("/budget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateExpenseJson))
                    .andExpect(status().isAccepted())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Zakupy spożywcze zaktualizowane")))
                    .andExpect(jsonPath("$.description", is("Zaktualizowany opis")))
                    .andExpect(jsonPath("$.price", is(300.00)));

            Expense expense = expensesRepository.findById(1L).orElse(null);
            Assertions.assertNotNull(expense);
            Assertions.assertEquals("Zakupy spożywcze zaktualizowane", expense.getName());
            Assertions.assertEquals("Zaktualizowany opis", expense.getDescription());
        }

        @Test
        @DisplayName("Should return 404 when expense does not exist")
        void testUpdateExpense_whenExpenseDoesNotExist_shouldReturn404() throws Exception {
            String updateExpenseJson = """
                    {
                        "id": 999,
                        "name": "Nieistniejący wydatek",
                        "description": "Test",
                        "category": "Test",
                        "price": 100.00,
                        "dateOfExpense": "2025-01-10T10:00:00",
                        "creator": "test-uid-001",
                        "participants": ["test-uid-001"]
                    }""";

            mockMvc.perform(put("/budget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateExpenseJson))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 with invalid data")
        void testUpdateExpense_whenInvalidData_shouldReturn400() throws Exception {
            String invalidExpenseJson = """
                    {
                        "id": null,
                        "name": "",
                        "description": null,
                        "category": "",
                        "price": -100.00,
                        "dateOfExpense": null,
                        "creator": "",
                        "participants": []
                    }""";

            mockMvc.perform(put("/budget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidExpenseJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Endpoint: DELETE /budget/{expId}")
    class DeleteExpenseTests {

        @Test
        @DisplayName("Should successfully delete expense")
        void testDeleteExpense_whenExpenseExists_shouldDeleteExpense() throws Exception {
            Long expenseIdToDelete = 1L;

            Expense expenseBeforeDelete = expensesRepository.findById(expenseIdToDelete).orElse(null);
            Assertions.assertNotNull(expenseBeforeDelete);

            mockMvc.perform(delete("/budget/{expId}", expenseIdToDelete)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Zakupy spożywcze")))
                    .andExpect(jsonPath("$.description", is("Zakupy w supermarkecie")))
                    .andExpect(jsonPath("$.price", is(250.50)));

            Expense expenseAfterDelete = expensesRepository.findById(expenseIdToDelete).orElse(null);
            Assertions.assertNull(expenseAfterDelete);
        }

        @Test
        @DisplayName("Should return 404 when expense does not exist")
        void testDeleteExpense_whenExpenseDoesNotExist_shouldReturn404() throws Exception {
            Long nonexistentExpenseId = 999L;

            mockMvc.perform(delete("/budget/{expId}", nonexistentExpenseId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should delete expense and related data")
        void testDeleteExpense_shouldDeleteExpenseAndRelatedData() throws Exception {
            Long expenseIdToDelete = 2L;

            mockMvc.perform(delete("/budget/{expId}", expenseIdToDelete)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2)))
                    .andExpect(jsonPath("$.name", is("Bilety lotnicze")));

            Expense expenseAfterDelete = expensesRepository.findById(expenseIdToDelete).orElse(null);
            Assertions.assertNull(expenseAfterDelete);
        }
    }

    @Nested
    @DisplayName("HTTP Status Tests")
    class HttpStatusTests {

        @Test
        @DisplayName("Should return 200 OK status")
        void testGetExpenses_shouldReturnStatus200() throws Exception {
            mockMvc.perform(get("/budget/{groupId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 200 OK status for successful creation")
        void testCreateExpense_shouldReturnStatus200() throws Exception {
            String newExpenseJson = """
                    {
                        "name": "Nowy wydatek",
                        "description": "Test",
                        "category": "Test",
                        "price": 100.00,
                        "dateOfExpense": "2025-01-15T18:00:00",
                        "participants": ["test-uid-002"]
                    }""";

            mockMvc.perform(post("/budget/{groupId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newExpenseJson))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 202 ACCEPTED status for successful update")
        void testUpdateExpense_shouldReturnStatus202() throws Exception {
            String updateExpenseJson = """
                    {
                        "id": 1,
                        "name": "Zaktualizowany wydatek",
                        "description": "Test",
                        "category": "Test",
                        "price": 100.00,
                        "dateOfExpense": "2025-01-10T10:00:00",
                        "creator": "test-uid-001",
                        "participants": ["test-uid-001"]
                    }""";

            mockMvc.perform(put("/budget")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateExpenseJson))
                    .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Should return 200 OK status for successful deletion")
        void testDeleteExpense_shouldReturnStatus200() throws Exception {
            mockMvc.perform(delete("/budget/{expId}", 3L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}
