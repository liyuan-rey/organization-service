package com.reythecoder.organization.integration;

import com.reythecoder.organization.dto.request.CreateTreeNodeReq;
import com.reythecoder.organization.dto.request.MoveTreeNodeReq;
import com.reythecoder.organization.entity.EntityType;
import com.reythecoder.organization.repository.OrgTreeNodeRepository;
import io.github.robsonkades.uuidv7.UUIDv7;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Integration tests for OrgTree node operations.
 *
 * Uses @SpringBootTest with Testcontainers for real database testing.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
@DisplayName("OrgTree Integration Tests")
class OrgTreeIntegrationTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("organization_db")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("01-init-department-personnel-group-tables.sql")
            .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF8")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrgTreeNodeRepository orgTreeNodeRepository;

    private UUID groupId;
    private UUID departmentId;
    private UUID personnelId;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        // Clean up any existing data
        orgTreeNodeRepository.deleteAll();

        // Create test entity IDs
        groupId = UUIDv7.randomUUID();
        departmentId = UUIDv7.randomUUID();
        personnelId = UUIDv7.randomUUID();
    }

    @Test
    @DisplayName("Test 1: should create node successfully")
    void createNode_shouldReturnCreatedNode() throws Exception {
        // Given
        CreateTreeNodeReq req = new CreateTreeNodeReq(
                null,
                EntityType.GROUP,
                groupId,
                "根群组",
                "a0"
        );

        // When & Then - Verify the request succeeds and returns data
        mockMvc.perform(post("/api/tree/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.type").value("GROUP"))
                .andExpect(jsonPath("$.data.name").value("根群组"));
    }

    @Test
    @DisplayName("Test 2: should get node by ID successfully")
    void getNode_shouldReturnNode() throws Exception {
        // Given - Create a node first and get its ID
        UUID nodeId = createTestNode(null, EntityType.GROUP, groupId, "测试节点", "a0");

        // When & Then
        mockMvc.perform(get("/api/tree/nodes/{nodeId}", nodeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(nodeId.toString()));
    }

    @Test
    @DisplayName("Test 3: should move node to new parent successfully")
    void moveNode_shouldReturnMovedNode() throws Exception {
        // Given - Create a root node first (level 0)
        UUID rootId = createTestNode(null, EntityType.GROUP, groupId, "根节点", "a0");

        // Create two child nodes under root (level 1)
        UUID parentId = createTestNode(rootId, EntityType.GROUP, groupId, "父节点", "b0");
        UUID childId = createTestNode(rootId, EntityType.DEPARTMENT, departmentId, "待移动子节点", "c0");

        // When - Move child under parent
        MoveTreeNodeReq moveReq = new MoveTreeNodeReq(
                childId,
                parentId,
                "d0"  // newSortRank
        );

        // Then
        mockMvc.perform(post("/api/tree/nodes/{nodeId}/move", childId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(childId.toString()));
    }

    @Test
    @DisplayName("Test 4: should remove node successfully")
    void removeNode_shouldReturnNoContent() throws Exception {
        // Given - Create a root node first (level 0)
        UUID rootId = createTestNode(null, EntityType.GROUP, groupId, "根节点", "a0");

        // Create a child node under root (level 1) - this can be deleted
        UUID childId = createTestNode(rootId, EntityType.DEPARTMENT, departmentId, "待删除子节点", "b0");

        // When & Then - Delete the child node (not root level)
        mockMvc.perform(post("/api/tree/nodes/{nodeId}/remove", childId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test 5: should get subtree successfully")
    void getSubtree_shouldReturnSubtree() throws Exception {
        // Given - Create a node
        UUID nodeId = createTestNode(null, EntityType.GROUP, groupId, "根节点", "a0");

        // When & Then
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/subtree", nodeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(nodeId.toString()));
    }

    @Test
    @DisplayName("Test 6: should get children nodes successfully")
    void getChildren_shouldReturnListOfChildren() throws Exception {
        // Given - Create parent node
        UUID parentId = createTestNode(null, EntityType.GROUP, groupId, "父节点", "a0");

        // Create child node under parent
        createTestNode(parentId, EntityType.DEPARTMENT, departmentId, "子节点", "b0");

        // When & Then
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/children", parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("Test 7: should get empty children list when no children exist")
    void getChildren_shouldReturnEmptyListWhenNoChildren() throws Exception {
        // Given - Create a node without children
        UUID nodeId = createTestNode(null, EntityType.GROUP, groupId, "孤立节点", "a0");

        // When & Then
        mockMvc.perform(get("/api/tree/nodes/{nodeId}/children", nodeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * Helper method to create a test node and return its ID.
     */
    private UUID createTestNode(UUID parentId, EntityType entityType, UUID entityId,
                                String alias, String sortRank) throws Exception {
        CreateTreeNodeReq req = new CreateTreeNodeReq(
                parentId,
                entityType,
                entityId,
                alias,
                sortRank
        );

        String response = mockMvc.perform(post("/api/tree/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(
            objectMapper.readTree(response).at("/data/id").asText()
        );
    }
}
