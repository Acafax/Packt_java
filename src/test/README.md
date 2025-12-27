# Integration Test Environment

##Overview

The test environment is configured for testing Spring Boot applications using:
- **Testcontainers** - runs a PostgreSQL database in a Docker container
- **Spring Boot Test** - full Spring context integration
- **Disabled Security** - all endpoints are accessible without authentication
- **Automatic data initialization** - SQL scripts loaded before each test

##File Structure

```
src/test/
├── java/org/example/springprojektzespolowy/
│   ├── config/
│   │   └── TestSecurityConfig.java           # Security configuration for tests
│   ├── controllers/
│   │   ├── ExpensesControllerIntegrationTest.java
│   │   ├── GroupControllerIntegrationTest.java
│   │   ├── InvitationControllerIntegrationTest.java
│   │   └── UserControllerIntegrationTest.java
│   └── services/
│       └── TestSecurityService.java          # Security service for tests
└── resources/
    ├── application-test.properties           # Test configuration
    └── data/
        ├── users.sql                         # Test data - users
        ├── group.sql                         # Test data - groups
        ├── expenses.sql                      # Test data - expenses
        ├── invitations.sql                   # Test data - invitations
        └── setUpGroupIntagrationTestEnvirament.sql
```

##ow to Use

### 1. Creating a New Integration Test

Create a test class following the existing pattern:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testUser")
@Import(TestSecurityConfig.class)
class MyControllerIntegrationTest {
    
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        // Clean database
        jdbcTemplate.execute("DELETE FROM user_group");
        jdbcTemplate.execute("DELETE FROM groups");
        jdbcTemplate.execute("DELETE FROM users");

        // Load test data
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("data/users.sql"));
        populator.addScript(new ClassPathResource("data/group.sql"));
        populator.execute(dataSource);
    }
    
    @Test
    void testMyEndpoint() {
        // given
        // when
        // then
    }
}
```

### 2. Automatic Features

When you follow the integration test pattern, you automatically get:

✅ **PostgreSQL Container** - running in Docker  
✅ **Disabled Security** - all endpoints accessible  
✅ **Test Data** - users, groups, expenses, and invitations loaded before each test  
✅ **Test Isolation** - each test has a clean database  

### 3. Test Data

SQL scripts automatically load:

**Users:**
- Jan Kowalski (id=1, uid='test-uid-001') - ADMIN in "Wakacje 2025" group
- Anna Nowak (id=2, uid='test-uid-002') - USER in "Wakacje 2025" group
- Piotr Wiśniewski (id=3, uid='test-uid-003') - ADMIN in "Projekt osobisty" group

**Groups:**
- Wakacje 2025 (id=1) - shared by Jan and Anna
- Projekt osobisty (id=2) - only for Piotr

**Expenses:**
- Multiple expenses for group 1 and 2
- Various categories: Food, Transport, Accommodation
- Different payers and participants

**Invitations:**
- Test invitations for group management

## Database Management

Clean database in the middle of a test:

```java
@Autowired
private JdbcTemplate jdbcTemplate;

@BeforeEach
void setUp() {
    jdbcTemplate.execute("DELETE FROM expenses_user");
    jdbcTemplate.execute("DELETE FROM expense");
    jdbcTemplate.execute("DELETE FROM user_group");
    jdbcTemplate.execute("DELETE FROM groups");
    jdbcTemplate.execute("DELETE FROM users");
}
```

## Configuration

### application-test.properties

Settings for tests:
- `spring.jpa.hibernate.ddl-auto=create-drop` - creates schema on startup
- Security disabled through configuration
- SQL initialization enabled

### TestSecurityConfig

Disables all security constraints:
```java
@Configuration
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
```

## Test Examples

### Controller Test with MockMvc

```java
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
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].name", notNullValue()))
                .andExpect(jsonPath("$[0].price", notNullValue()));
    }
}
```

### Testing POST Requests

```java
@Test
@DisplayName("Should create new expense")
void testCreateExpense_shouldReturnCreatedExpense() throws Exception {
    String newExpenseJson = """
            {
                "name": "Test Expense",
                "description": "Test Description",
                "category": "Food",
                "price": 100.00,
                "dateOfExpense": "2025-01-15T12:00:00",
                "participants": ["test-uid-001", "test-uid-002"]
            }""";
    
    mockMvc.perform(post("/budget/{groupId}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(newExpenseJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Test Expense")))
            .andExpect(jsonPath("$.price", is(100.00)));
}
```

### Testing PUT Requests

```java
@Test
@DisplayName("Should update existing expense")
void testUpdateExpense_whenExpenseExists_shouldReturnUpdatedExpense() throws Exception {
    String updateExpenseJson = """
            {
                "id": 1,
                "name": "Updated Expense",
                "description": "Updated Description",
                "category": "Transport",
                "price": 200.00,
                "dateOfExpense": "2025-01-20T14:00:00",
                "creator": "test-uid-001",
                "participants": ["test-uid-001"]
            }""";
    
    mockMvc.perform(put("/budget")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateExpenseJson))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.name", is("Updated Expense")));
}
```

### Testing DELETE Requests

```java
@Test
@DisplayName("Should delete expense")
void testDeleteExpense_whenExpenseExists_shouldReturnDeletedExpense() throws Exception {
    mockMvc.perform(delete("/budget/{expId}", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
}
```

### Nested Test Structure

```java
@Nested
@DisplayName("Endpoint: GET /budget/{groupId}/{expId}")
class GetExpenseByIdTests {
    
    @Test
    @DisplayName("Should return 404 when expense does not exist")
    void testGetExpense_whenExpenseDoesNotExist_shouldReturn404() throws Exception {
        mockMvc.perform(get("/budget/{groupId}/{expId}", 1, 999))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should return expense when exists")
    void testGetExpense_whenExpenseExists_shouldReturnExpense() throws Exception {
        mockMvc.perform(get("/budget/{groupId}/{expId}", 1, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", notNullValue()));
    }
}
```

##Requirements

- Docker running locally (for Testcontainers)
- Maven
- Java 21+

## Troubleshooting

### Issue: Testcontainers cannot connect to Docker

**Solution:** Make sure Docker is running and accessible.

### Issue: SQL scripts are not loaded

**Solution:** Check if files are in `src/test/resources/data/`

### Issue: Tests are slow

**Solution:** First test will be slower as it starts the container. Subsequent tests reuse the container and are faster.

### Issue: Port already in use

**Solution:** Testcontainers automatically assigns random ports. If issues persist, restart Docker.

##Additional Information

- Tests use transactions - default rollback after each test
- SQL logs are enabled in DEBUG mode
- MockMvc automatically available for testing controllers
- Each test class manages its own PostgreSQL container lifecycle
- Database is recreated for each test method ensuring isolation
