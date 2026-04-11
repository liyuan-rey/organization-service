# Obsolete Tables Cleanup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove all obsolete hierarchy and association tables/code after org_tree optimization, including 6 database tables and ~44 Java files.

**Architecture:** Top-down cleanup - delete Controllers → Services → Repositories → Entities → DTOs, then modify database scripts and OpenAPI documentation, finally verify with tests.

**Tech Stack:** Spring Boot 4.0, Java 17, PostgreSQL, Gradle

---

## File Structure

**Files to Delete (44 total):**

| Layer | Files |
|-------|-------|
| Controllers | `DepartmentHierarchyController.java`, `DepartmentPersonnelController.java`, `GroupHierarchyController.java`, `GroupDepartmentController.java`, `GroupPersonnelController.java`, `DepartmentGroupController.java`, `TreeController.java` |
| Service Interfaces | `DepartmentHierarchyService.java`, `DepartmentPersonnelService.java`, `GroupHierarchyService.java`, `GroupDepartmentService.java`, `GroupPersonnelService.java`, `DepartmentGroupService.java`, `TreeService.java` |
| Service Impls | `DepartmentHierarchyServiceImpl.java`, `DepartmentPersonnelServiceImpl.java`, `GroupHierarchyServiceImpl.java`, `GroupDepartmentServiceImpl.java`, `GroupPersonnelServiceImpl.java`, `GroupDepartmentServiceImpl.java`, `TreeServiceImpl.java` |
| Repositories | `DepartmentHierarchyRepository.java`, `DepartmentPersonnelRepository.java`, `GroupHierarchyRepository.java`, `GroupDepartmentRepository.java`, `GroupPersonnelRepository.java`, `DepartmentGroupRepository.java` |
| Entities | `DepartmentHierarchyEntity.java`, `DepartmentPersonnelEntity.java`, `GroupHierarchyEntity.java`, `GroupDepartmentEntity.java`, `GroupPersonnelEntity.java`, `DepartmentGroupEntity.java` |
| Request DTOs | `DepartmentHierarchyCreateReq.java`, `DepartmentPersonnelCreateReq.java`, `GroupHierarchyCreateReq.java`, `GroupDepartmentCreateReq.java`, `GroupPersonnelCreateReq.java`, `DepartmentGroupCreateReq.java` |
| Response DTOs | `DepartmentHierarchyRsp.java`, `DepartmentPersonnelRsp.java`, `GroupHierarchyRsp.java`, `GroupDepartmentRsp.java`, `GroupPersonnelRsp.java`, `GroupGroupRsp.java` |
| Tests | `DepartmentHierarchyControllerTest.java`, `DepartmentHierarchyServiceTest.java`, `DepartmentPersonnelControllerTest.java`, `DepartmentPersonnelServiceTest.java`, `GroupHierarchyControllerTest.java`, `GroupHierarchyServiceTest.java`, `GroupDepartmentServiceTest.java`, `GroupPersonnelServiceTest.java`, `TreeServiceTest.java` |

**Files to Modify:**
- `db/init-scripts/01-init-organization-tables.sql` - Remove DDL for 5 obsolete tables
- `db/init-scripts/03-seed-sample-data.sql` - Remove seed data for 3 tables
- `docs/openapi.yaml` - Remove obsolete API endpoint definitions

---

### Task 1: Delete Controllers (7 files)

**Files:**
- Delete: `src/main/java/com/reythecoder/organization/controller/DepartmentHierarchyController.java`
- Delete: `src/main/java/com/reythecoder/organization/controller/DepartmentPersonnelController.java`
- Delete: `src/main/java/com/reythecoder/organization/controller/GroupHierarchyController.java`
- Delete: `src/main/java/com/reythecoder/organization/controller/GroupDepartmentController.java`
- Delete: `src/main/java/com/reythecoder/organization/controller/GroupPersonnelController.java`
- Delete: `src/main/java/com/reythecoder/organization/controller/DepartmentGroupController.java`
- Delete: `src/main/java/com/reythecoder/organization/controller/TreeController.java`

- [ ] **Step 1: Delete hierarchy controllers**

Run:
```bash
cd /home/sam/codes/organization-service/backend
rm -f src/main/java/com/reythecoder/organization/controller/DepartmentHierarchyController.java
rm -f src/main/java/com/reythecoder/organization/controller/DepartmentPersonnelController.java
rm -f src/main/java/com/reythecoder/organization/controller/GroupHierarchyController.java
rm -f src/main/java/com/reythecoder/organization/controller/GroupDepartmentController.java
rm -f src/main/java/com/reythecoder/organization/controller/GroupPersonnelController.java
rm -f src/main/java/com/reythecoder/organization/controller/DepartmentGroupController.java
```

- [ ] **Step 2: Delete TreeController**

Run:
```bash
rm -f src/main/java/com/reythecoder/organization/controller/TreeController.java
```

- [ ] **Step 3: Verify deletion**

Run:
```bash
ls src/main/java/com/reythecoder/organization/controller/ | grep -E "Hierarchy|Personnel|GroupDepartment|GroupPersonnel|DepartmentGroup|Tree"
```

Expected: No output (files deleted)

---

### Task 2: Delete Service Interfaces and Implementations (14 files)

**Files:**
- Delete: `src/main/java/com/reythecoder/organization/service/DepartmentHierarchyService.java`
- Delete: `src/main/java/com/reythecoder/organization/service/impl/DepartmentHierarchyServiceImpl.java`
- Delete: `src/main/java/com/reythecoder/organization/service/DepartmentPersonnelService.java`
- Delete: `src/main/java/com/reythecoder/organization/service/impl/DepartmentPersonnelServiceImpl.java`
- Delete: `src/main/java/com/reythecoder/organization/service/GroupHierarchyService.java`
- Delete: `src/main/java/com/reythecoder/organization/service/impl/GroupHierarchyServiceImpl.java`
- Delete: `src/main/java/com/reythecoder/organization/service/GroupDepartmentService.java`
- Delete: `src/main/java/com/reythecoder/organization/service/impl/GroupDepartmentServiceImpl.java`
- Delete: `src/main/java/com/reythecoder/organization/service/GroupPersonnelService.java`
- Delete: `src/main/java/com/reythecoder/organization/service/impl/GroupPersonnelServiceImpl.java`
- Delete: `src/main/java/com/reythecoder/organization/service/DepartmentGroupService.java`
- Delete: `src/main/java/com/reythecoder/organization/service/impl/DepartmentGroupServiceImpl.java`
- Delete: `src/main/java/com/reythecoder/organization/service/TreeService.java`
- Delete: `src/main/java/com/reythecoder/organization/service/impl/TreeServiceImpl.java`

- [ ] **Step 1: Delete hierarchy service interfaces**

Run:
```bash
rm -f src/main/java/com/reythecoder/organization/service/DepartmentHierarchyService.java
rm -f src/main/java/com/reythecoder/organization/service/DepartmentPersonnelService.java
rm -f src/main/java/com/reythecoder/organization/service/GroupHierarchyService.java
rm -f src/main/java/com/reythecoder/organization/service/GroupDepartmentService.java
rm -f src/main/java/com/reythecoder/organization/service/GroupPersonnelService.java
rm -f src/main/java/com/reythecoder/organization/service/DepartmentGroupService.java
rm -f src/main/java/com/reythecoder/organization/service/TreeService.java
```

- [ ] **Step 2: Delete hierarchy service implementations**

Run:
```bash
rm -f src/main/java/com/reythecoder/organization/service/impl/DepartmentHierarchyServiceImpl.java
rm -f src/main/java/com/reythecoder/organization/service/impl/DepartmentPersonnelServiceImpl.java
rm -f src/main/java/com/reythecoder/organization/service/impl/GroupHierarchyServiceImpl.java
rm -f src/main/java/com/reythecoder/organization/service/impl/GroupDepartmentServiceImpl.java
rm -f src/main/java/com/reythecoder/organization/service/impl/GroupPersonnelServiceImpl.java
rm -f src/main/java/com/reythecoder/organization/service/impl/GroupDepartmentServiceImpl.java
rm -f src/main/java/com/reythecoder/organization/service/impl/TreeServiceImpl.java
```

- [ ] **Step 3: Verify deletion**

Run:
```bash
ls src/main/java/com/reythecoder/organization/service/ | grep -E "Hierarchy|Personnel|GroupDepartment|GroupPersonnel|DepartmentGroup|Tree"
ls src/main/java/com/reythecoder/organization/service/impl/ | grep -E "Hierarchy|Personnel|GroupDepartment|GroupPersonnel|DepartmentGroup|Tree"
```

Expected: No output (files deleted)

---

### Task 3: Delete Repositories (6 files)

**Files:**
- Delete: `src/main/java/com/reythecoder/organization/repository/DepartmentHierarchyRepository.java`
- Delete: `src/main/java/com/reythecoder/organization/repository/DepartmentPersonnelRepository.java`
- Delete: `src/main/java/com/reythecoder/organization/repository/GroupHierarchyRepository.java`
- Delete: `src/main/java/com/reythecoder/organization/repository/GroupDepartmentRepository.java`
- Delete: `src/main/java/com/reythecoder/organization/repository/GroupPersonnelRepository.java`
- Delete: `src/main/java/com/reythecoder/organization/repository/DepartmentGroupRepository.java`

- [ ] **Step 1: Delete repositories**

Run:
```bash
rm -f src/main/java/com/reythecoder/organization/repository/DepartmentHierarchyRepository.java
rm -f src/main/java/com/reythecoder/organization/repository/DepartmentPersonnelRepository.java
rm -f src/main/java/com/reythecoder/organization/repository/GroupHierarchyRepository.java
rm -f src/main/java/com/reythecoder/organization/repository/GroupDepartmentRepository.java
rm -f src/main/java/com/reythecoder/organization/repository/GroupPersonnelRepository.java
rm -f src/main/java/com/reythecoder/organization/repository/DepartmentGroupRepository.java
```

- [ ] **Step 2: Verify deletion**

Run:
```bash
ls src/main/java/com/reythecoder/organization/repository/ | grep -E "Hierarchy|Personnel|GroupDepartment|GroupPersonnel|DepartmentGroup"
```

Expected: No output (files deleted)

---

### Task 4: Delete Entities (6 files)

**Files:**
- Delete: `src/main/java/com/reythecoder/organization/entity/DepartmentHierarchyEntity.java`
- Delete: `src/main/java/com/reythecoder/organization/entity/DepartmentPersonnelEntity.java`
- Delete: `src/main/java/com/reythecoder/organization/entity/GroupHierarchyEntity.java`
- Delete: `src/main/java/com/reythecoder/organization/entity/GroupDepartmentEntity.java`
- Delete: `src/main/java/com/reythecoder/organization/entity/GroupPersonnelEntity.java`
- Delete: `src/main/java/com/reythecoder/organization/entity/DepartmentGroupEntity.java`

- [ ] **Step 1: Delete entities**

Run:
```bash
rm -f src/main/java/com/reythecoder/organization/entity/DepartmentHierarchyEntity.java
rm -f src/main/java/com/reythecoder/organization/entity/DepartmentPersonnelEntity.java
rm -f src/main/java/com/reythecoder/organization/entity/GroupHierarchyEntity.java
rm -f src/main/java/com/reythecoder/organization/entity/GroupDepartmentEntity.java
rm -f src/main/java/com/reythecoder/organization/entity/GroupPersonnelEntity.java
rm -f src/main/java/com/reythecoder/organization/entity/DepartmentGroupEntity.java
```

- [ ] **Step 2: Verify deletion**

Run:
```bash
ls src/main/java/com/reythecoder/organization/entity/ | grep -E "Hierarchy|Personnel|GroupDepartment|GroupPersonnel|DepartmentGroup"
```

Expected: No output (files deleted)

---

### Task 5: Delete Request DTOs (6 files)

**Files:**
- Delete: `src/main/java/com/reythecoder/organization/dto/request/DepartmentHierarchyCreateReq.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/request/DepartmentPersonnelCreateReq.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/request/GroupHierarchyCreateReq.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/request/GroupDepartmentCreateReq.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/request/GroupPersonnelCreateReq.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/request/DepartmentGroupCreateReq.java`

- [ ] **Step 1: Delete request DTOs**

Run:
```bash
rm -f src/main/java/com/reythecoder/organization/dto/request/DepartmentHierarchyCreateReq.java
rm -f src/main/java/com/reythecoder/organization/dto/request/DepartmentPersonnelCreateReq.java
rm -f src/main/java/com/reythecoder/organization/dto/request/GroupHierarchyCreateReq.java
rm -f src/main/java/com/reythecoder/organization/dto/request/GroupDepartmentCreateReq.java
rm -f src/main/java/com/reythecoder/organization/dto/request/GroupPersonnelCreateReq.java
rm -f src/main/java/com/reythecoder/organization/dto/request/DepartmentGroupCreateReq.java
```

- [ ] **Step 2: Verify deletion**

Run:
```bash
ls src/main/java/com/reythecoder/organization/dto/request/ | grep -E "Hierarchy|Personnel|GroupDepartment|GroupPersonnel|DepartmentGroup"
```

Expected: No output (files deleted)

---

### Task 6: Delete Response DTOs (6 files)

**Files:**
- Delete: `src/main/java/com/reythecoder/organization/dto/response/DepartmentHierarchyRsp.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/response/DepartmentPersonnelRsp.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/response/GroupHierarchyRsp.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/response/GroupDepartmentRsp.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/response/GroupPersonnelRsp.java`
- Delete: `src/main/java/com/reythecoder/organization/dto/response/DepartmentGroupRsp.java`

- [ ] **Step 1: Delete response DTOs**

Run:
```bash
rm -f src/main/java/com/reythecoder/organization/dto/response/DepartmentHierarchyRsp.java
rm -f src/main/java/com/reythecoder/organization/dto/response/DepartmentPersonnelRsp.java
rm -f src/main/java/com/reythecoder/organization/dto/response/GroupHierarchyRsp.java
rm -f src/main/java/com/reythecoder/organization/dto/response/GroupDepartmentRsp.java
rm -f src/main/java/com/reythecoder/organization/dto/response/GroupPersonnelRsp.java
rm -f src/main/java/com/reythecoder/organization/dto/response/DepartmentGroupRsp.java
```

- [ ] **Step 2: Verify deletion**

Run:
```bash
ls src/main/java/com/reythecoder/organization/dto/response/ | grep -E "Hierarchy|Personnel|GroupDepartment|GroupPersonnel|DepartmentGroup"
```

Expected: No output (files deleted)

---

### Task 7: Delete Test Files (9 files)

**Files:**
- Delete: `src/test/java/com/reythecoder/organization/controller/DepartmentHierarchyControllerTest.java`
- Delete: `src/test/java/com/reythecoder/organization/service/DepartmentHierarchyServiceTest.java`
- Delete: `src/test/java/com/reythecoder/organization/controller/DepartmentPersonnelControllerTest.java`
- Delete: `src/test/java/com/reythecoder/organization/service/DepartmentPersonnelServiceTest.java`
- Delete: `src/test/java/com/reythecoder/organization/controller/GroupHierarchyControllerTest.java`
- Delete: `src/test/java/com/reythecoder/organization/service/GroupHierarchyServiceTest.java`
- Delete: `src/test/java/com/reythecoder/organization/service/GroupDepartmentServiceTest.java`
- Delete: `src/test/java/com/reythecoder/organization/service/GroupPersonnelServiceTest.java`
- Delete: `src/test/java/com/reythecoder/organization/service/TreeServiceTest.java`

- [ ] **Step 1: Delete controller tests**

Run:
```bash
rm -f src/test/java/com/reythecoder/organization/controller/DepartmentHierarchyControllerTest.java
rm -f src/test/java/com/reythecoder/organization/controller/DepartmentPersonnelControllerTest.java
rm -f src/test/java/com/reythecoder/organization/controller/GroupHierarchyControllerTest.java
```

- [ ] **Step 2: Delete service tests**

Run:
```bash
rm -f src/test/java/com/reythecoder/organization/service/DepartmentHierarchyServiceTest.java
rm -f src/test/java/com/reythecoder/organization/service/DepartmentPersonnelServiceTest.java
rm -f src/test/java/com/reythecoder/organization/service/GroupHierarchyServiceTest.java
rm -f src/test/java/com/reythecoder/organization/service/GroupDepartmentServiceTest.java
rm -f src/test/java/com/reythecoder/organization/service/GroupPersonnelServiceTest.java
rm -f src/test/java/com/reythecoder/organization/service/TreeServiceTest.java
```

- [ ] **Step 3: Verify deletion**

Run:
```bash
find src/test/java/com/reythecoder/organization -name "*.java" | xargs grep -l -E "Hierarchy|Personnel|GroupDepartment|GroupPersonnel|DepartmentGroup|Tree" 2>/dev/null || echo "No obsolete test files found"
```

Expected: "No obsolete test files found"

---

### Task 8: Modify Database DDL Script

**Files:**
- Modify: `db/init-scripts/01-init-organization-tables.sql`

- [ ] **Step 1: Read current DDL script**

Run:
```bash
wc -l db/init-scripts/01-init-organization-tables.sql
```

Expected: ~445 lines

- [ ] **Step 2: Remove org_department_hierarchy table DDL (lines 175-205)**

Delete 31 lines starting from line 175 (-- 5. 部门层级关系表) to line 205 (CREATE TRIGGER):

Run:
```bash
sed -i '175,205d' db/init-scripts/01-init-organization-tables.sql
```

- [ ] **Step 3: Remove org_department_personnel table DDL (now lines 176-205, original 207-236)**

After first deletion, delete 30 lines:

Run:
```bash
sed -i '176,205d' db/init-scripts/01-init-organization-tables.sql
```

- [ ] **Step 4: Remove org_group_hierarchy table DDL (now lines 272-302, original 304-334)**

After two deletions (62 lines removed), delete 31 lines:

Run:
```bash
sed -i '272,302d' db/init-scripts/01-init-organization-tables.sql
```

- [ ] **Step 5: Remove org_group_department table DDL (now lines 304-330, original 336-362)**

After three deletions (93 lines removed), delete 27 lines:

Run:
```bash
sed -i '304,330d' db/init-scripts/01-init-organization-tables.sql
```

- [ ] **Step 6: Remove org_group_personnel table DDL (now lines 332-358, original 364-390)**

After four deletions (120 lines removed), delete 27 lines:

Run:
```bash
sed -i '332,358d' db/init-scripts/01-init-organization-tables.sql
```

- [ ] **Step 7: Verify DDL changes**

Run:
```bash
grep -E "org_department_hierarchy|org_department_personnel|org_group_hierarchy|org_group_department|org_group_personnel" db/init-scripts/01-init-organization-tables.sql
```

Expected: No output (obsolete tables removed)

- [ ] **Step 8: Verify org_tree still present**

Run:
```bash
grep -c "org_tree" db/init-scripts/01-init-organization-tables.sql
```

Expected: >0 (org_tree table still exists)

---

### Task 9: Modify Seed Data Script

**Files:**
- Modify: `db/init-scripts/03-seed-sample-data.sql`

- [ ] **Step 1: Remove org_department_hierarchy seed data (lines 50-54)**

Delete 5 lines containing the INSERT statement:

Run:
```bash
sed -i '50,54d' db/init-scripts/03-seed-sample-data.sql
```

- [ ] **Step 2: Remove org_department_personnel seed data (now lines 52-54, original 57-59)**

After first deletion, delete 3 lines:

Run:
```bash
sed -i '52,54d' db/init-scripts/03-seed-sample-data.sql
```

- [ ] **Step 3: Remove org_group_personnel seed data (now lines 57-59, original 62-64)**

After two deletions (8 lines removed), delete 3 lines:

Run:
```bash
sed -i '57,59d' db/init-scripts/03-seed-sample-data.sql
```

- [ ] **Step 4: Verify seed data changes**

Run:
```bash
grep -E "org_department_hierarchy|org_department_personnel|org_group_personnel" db/init-scripts/03-seed-sample-data.sql
```

Expected: No output (obsolete seed data removed)

- [ ] **Step 5: Verify org_tree seed data still present**

Run:
```bash
grep -c "org_tree" db/init-scripts/03-seed-sample-data.sql
```

Expected: >0 (org_tree seed data still exists)

---

### Task 10: Update OpenAPI Documentation

**Files:**
- Modify: `docs/openapi.yaml`

- [ ] **Step 1: Check openapi.yaml for obsolete paths**

Run:
```bash
grep -n "department-hierarchy|department-personnel|group-hierarchy|group-department|group-personnel|department-group|/trees" docs/openapi.yaml
```

- [ ] **Step 2: Remove obsolete path definitions**

Search and remove all path definitions matching:
- `/api/department-hierarchy/*`
- `/api/department-personnel/*`
- `/api/group-hierarchy/*`
- `/api/group-department/*`
- `/api/group-personnel/*`
- `/api/department-group/*`
- `/api/trees/{groupId}` (old TreeController endpoint)

- [ ] **Step 3: Verify openapi changes**

Run:
```bash
grep -E "department-hierarchy|department-personnel|group-hierarchy|group-department|group-personnel|department-group|/trees" docs/openapi.yaml
```

Expected: No output (obsolete endpoints removed)

---

### Task 11: Run Build and Unit Tests

**Files:**
- None (verification only)

- [ ] **Step 1: Clean and compile**

Run:
```bash
./gradlew clean compileJava
```

Expected: BUILD SUCCESSFUL, no compilation errors

- [ ] **Step 2: Run unit tests**

Run:
```bash
./gradlew test
```

Expected: BUILD SUCCESSFUL, all tests pass

- [ ] **Step 3: Check test report**

Run:
```bash
open build/reports/tests/test/index.html 2>/dev/null || cat build/reports/tests/test/index.html | head -50
```

Expected: All tests passed, no failures

---

### Task 12: Run Integration Tests

**Files:**
- None (verification only)

- [ ] **Step 1: Start Docker database**

Run:
```bash
docker-compose up -d
```

Expected: PostgreSQL container running

- [ ] **Step 2: Run integration tests**

Run:
```bash
./gradlew testIntegration
```

Expected: BUILD SUCCESSFUL, all integration tests pass

- [ ] **Step 3: Verify org_tree functionality**

Run:
```bash
curl -s http://localhost:8080/api/tree/root 2>/dev/null || echo "Integration test verified through Gradle"
```

Expected: Integration tests passed, org_tree API working

---

### Task 13: Commit All Changes

**Files:**
- None (git commit)

- [ ] **Step 1: Stage all deleted files**

Run:
```bash
git add -A
```

- [ ] **Step 2: Verify staged changes**

Run:
```bash
git status
```

Expected: All deletions and modifications listed

- [ ] **Step 3: Commit with proper message**

Run:
```bash
git commit -m "$(cat <<'EOF'
refactor(organization): remove obsolete hierarchy and association tables/code

- Delete 7 Controllers (hierarchy + TreeController)
- Delete 14 Services (7 interfaces + 7 impls)
- Delete 6 Repositories
- Delete 6 Entities
- Delete 12 DTOs (6 request + 6 response)
- Delete 9 Test files
- Remove DDL for 5 obsolete tables from init scripts
- Remove seed data for 3 obsolete tables
- Update openapi.yaml to remove obsolete endpoints

org_tree now replaces all hierarchy functionality.

Refs: backend/docs/superpowers/specs/2026-04-11-obsolete-tables-cleanup-design.md
EOF
)"
```

Expected: Commit created successfully

- [ ] **Step 4: Verify commit**

Run:
```bash
git log -1 --stat
```

Expected: Single commit with ~50 file changes (deletions)

---

## Verification Summary

After completing all tasks:

1. ✅ All 44 Java files deleted
2. ✅ Database DDL updated (5 obsolete tables removed)
3. ✅ Seed data updated (3 obsolete tables removed)
4. ✅ OpenAPI documentation updated
5. ✅ `./gradlew clean build` passes
6. ✅ `./gradlew test` passes
7. ✅ `./gradlew testIntegration` passes
8. ✅ Single commit with proper message