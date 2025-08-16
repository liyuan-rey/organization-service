# Database Design Conventions

## General PostgreSQL Best Practices

1. **Naming Conventions**
   - Use lowercase with underscores for table and column names
   - Use descriptive, unambiguous names
   - Avoid reserved keywords

2. **Data Types**
   - Choose appropriate data types (e.g., use TEXT instead of VARCHAR when length varies significantly)
   - Use TIMESTAMP WITH TIME ZONE for temporal data
   - Use SERIAL or IDENTITY for auto-incrementing primary keys

3. **Normalization**
   - Apply normalization principles (1NF, 2NF, 3NF) to eliminate redundancy
   - Balance normalization with performance needs

4. **Indexing**
   - Create indexes on foreign keys and frequently queried columns
   - Use composite indexes for multi-column queries
   - Regularly review and remove unused indexes

5. **Constraints**
   - Use foreign key constraints to maintain referential integrity
   - Implement NOT NULL constraints where appropriate
   - Use check constraints for data validation

6. **Performance Considerations**
   - Use EXPLAIN ANALYZE to understand query performance
   - Consider partitioning for large tables
   - Use connection pooling for application connections

## Project-Specific Conventions

1. **Database Design Deliverables**
   - Database creation scripts with foreign key relationships (format: `*.design.sql`)
   - Database creation scripts without foreign key relationships (format: `*.sql`) with indexes preserved

2. **Primary Keys**
   - All table primary key ID values are generated using 64-bit snowflake algorithm (not UUIDs)

3. **NULL Values**
   - No NULL values allowed in table fields; default values must be provided:
     - Time types default to: `2000-01-01 00:00:00`
     - String types default to: `""`
     - Numeric types default to: `0`

4. **Table Naming Convention**
   - Schema: `organization`
   - Business data tables: `b_org_*`
   - Relationship tables: `r_org_*`
   - Dictionary tables: `d_org_*`
   - Statistical ledger tables: `s_org_*`
   - Tables are named by system and function with appropriate prefixes

5. **Required Fields**
   - All tables must have `id`, `created_at`, `updated_at` and `tenant_id` fields
   - `updated_at` automatically updated on data modification
   - Use Spring JPA's built-in optimistic locking capability for `updated_at`

6. **Dictionary Fields**
   - Simple dictionary values stored as char type with meaningful single letters
   - Text display handled in frontend

7. **Sort Fields**
   - Sort fields named as `sort`
