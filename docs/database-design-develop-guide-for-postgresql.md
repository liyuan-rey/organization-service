# A Guide to PostgreSQL Database Design and Development

This document aims to provide a unified set of standards and best practice guidelines for the database design of the organization-service project, ensuring consistency, maintainability, and high performance in database design.

## 1. Design Principles

### 1.1 Normalization Requirements

In database design, the principles of relational database theory should be followed to achieve a high level of normalization matching (3NF) to avoid data redundancy and clarify the relationships between data. This helps ensure data consistency and integrity.

In specific scenarios, if the benefits of performance requirements or business convenience outweigh the impact on data management, it is permissible to deviate from normalization requirements, but this must be subject to thorough evaluation.

### 1.2 Character Set and Encoding Standards

The `Unicode` character set and `UTF8` encoding should be used, which is the default setting for PostgreSQL databases. This standard ensures good support for multilingual characters, including Chinese, Japanese, Korean, and others.

For scenarios that use only Chinese, the `GB18030` character set and encoding can be considered, but the use of `GB2312` or `GBK` is not recommended.

### 1.3 Table Structure Design Principles

1. All tables must have a primary key to uniquely identify each record in the table.

2. For fields that associate two tables, primary keys and foreign keys should generally be established separately. Whether to establish foreign key constraints should be determined based on data integrity and performance requirements:
   - It is not recommended to establish foreign key constraints for dictionary table fields that are referenced, as dictionary tables are often referenced by multiple tables, and foreign key checks may cause lock waits and deadlock issues.
   - When a table has more than four foreign key constraints, special attention should be paid to whether they are truly necessary, and consideration should be given to converting them to logical foreign keys rather than foreign key constraints.
   - When a table is referenced as a foreign key by more than four other tables, special attention should be paid to the potential for deadlock issues caused by foreign key check lock waits.

3. Fields with uniqueness requirements must have a uniqueness constraint to ensure data uniqueness.

4. It is recommended to add `created_at` and `updated_at` fields to main business tables for optimistic locking mechanisms and troubleshooting.

5. All tables must include a `tenant_id` field for multi-tenant data isolation.

### 1.4 Partitioned Table Usage Guidelines

Partitioned tables are suitable for the following scenarios:

- Tables larger than 2GB
- Tables with more than 10 million records
- Tables that will contain a large amount of data
- Tables that require regular archiving of logs or deletion of some data

Partitioning strategy selection:

- Range partitioning based on field value ranges (suitable for tables that grow over time)
- List partitioning based on a few key values of a field
- For static tables, hash partitioning or list partitioning can be used

### 1.5 Index Design Principles

1. Fields that need to be used as query conditions in queries should be considered for indexing.

2. To improve performance, it is recommended to index foreign key fields.

3. For composite indexes, the order of index fields is crucial, and fields with higher query frequencies should be placed at the beginning of the index combination.

4. Regularly review and remove unused indexes to reduce storage overhead and maintenance costs.

## 2. Naming Conventions

### 2.1 General Naming Principles

All database objects should follow the following naming principles:

1. Composed of lowercase letters, numbers, and underscores, without the use of uppercase letters or special characters.
2. Names should be descriptive and unambiguous, avoiding reserved words or keywords.
3. Use the singular form of English words.
4. Naming length should be limited to 30 characters.
5. When a single word cannot express the meaning of an object, use multiple words connected by underscores.

### 2.2 Table and Field Naming Conventions

1. Table names should follow specific prefix conventions:
   - Business data tables: `b_org_*`
   - Relationship tables: `r_org_*`
   - Dictionary tables: `d_org_*`
   - Statistical ledger tables: `s_org_*`
   - Temporary tables: `tmp_org_*`

2. Field names should be semantically clear and composed of English words separated by underscores.

3. Primary key fields are typically named `id`, and foreign key fields that reference other tables should follow the format `<referenced_table_name>_id`.

4. Time fields should be named `created_at` and `updated_at`.

### 2.3 Constraint Naming Conventions

1. Primary key constraints: `pk_<table_name>`
2. Foreign key constraints: `fk_<table_name>_<referenced_table_name>` (if there are multiple foreign keys referencing the same table, it should be `fk_<table_name>_<referenced_table_name>_<field_name>`)
3. Uniqueness constraints: `uk_<table_name_abbreviation>_<identifier>`
4. NOT NULL constraints and CHECK constraints are automatically generated by the database.

### 2.4 Index Naming Conventions

1. Regular indexes: `idx_<table_name_abbreviation>_<field_name>`
2. Composite indexes: `idx_<table_name_abbreviation>_<meaning>` (the meaning can be composed of multiple fields)

## 3. Data Type Usage Agreements

When selecting data types, they should be chosen based on actual business requirements and data characteristics:

1. **Character Types**:
   - Use `CHAR(n)` for fixed-length strings
   - Use `VARCHAR(n)` for variable-length strings
   - Use `TEXT` for character data exceeding 1GB

2. **Numeric Types**:
   - Prefer the 4-byte `INTEGER` type
   - Use the 8-byte `BIGINT` type for large numbers
   - Use `DOUBLE PRECISION` for floating-point numbers
   - Avoid using the `DECIMAL` type unless there are special precision requirements

3. **Date and Time Types**:
   - Use `TIMESTAMP WITH TIME ZONE` for times generated by business logic
   - External imported time data can be stored in `VARCHAR` format as `YYYYMMDDHH24MISS`

4. **Boolean Type**:
   - Use PostgreSQL's `BOOLEAN` type

5. **Special Types**:
   - UUID can be used as the primary key for business tables, but it is not recommended for dictionary tables
   - JSON data should be stored using the `JSONB` type
   - Binary data should be stored using the `BYTEA` type

## 4. Project-Specific Standards

### 4.1 Primary Key Generation Strategy

The primary key ID values for all tables should be generated using the 64-bit Snowflake algorithm, rather than UUID. This strategy ensures global uniqueness and trend-increasing nature of the primary keys, which is beneficial for database performance optimization.

### 4.2 NULL Value Handling Standards

To ensure data integrity and avoid calculation issues related to NULL values, NULL values are not allowed in the database. All fields must provide default values:

- The default value for time type fields is: `2000-01-01 00:00:00`
- The default value for string type fields is: empty string `""`
- The default value for numeric type fields is: `0`

### 4.3 Required Field Requirements

All tables must include the following fields:

- `id`: Primary key field
- `created_at`: Record creation time
- `updated_at`: Record last update time, automatically updated using Spring JPA's built-in optimistic locking mechanism
- `tenant_id`: Tenant identifier for multi-tenant data isolation

### 4.4 Dictionary Field Design

1. Simple dictionary values should be stored as character types, using meaningful single-letter identifiers. For example, gender should be stored as `CHAR(1)` with values 'M', 'F', etc.
2. The text display of dictionary values should be handled by the front end, with only the identifiers stored in the database.
3. The sorting field should be uniformly named `sort`.

## 5. Development Standards

### 5.1 SQL Writing Standards

1. **Character Data**: Character data in SQL should uniformly use single quotes. In particular, strings that are purely numeric must be enclosed in single quotes to avoid internal conversions that can lead to performance issues or index失效.

2. **INSERT Statements**: When using INSERT statements, always specify the list of fields for the inserted values. This ensures that existing systems will not be affected if the table structure is changed by adding fields.

3. **SELECT Statements**: Avoid using `SELECT *` statements. Instead, explicitly specify the list of fields needed, unless all fields are truly required.

4. **WHERE Clauses**: For SELECT, UPDATE, and DELETE statements, always check the completeness of the WHERE clause to prevent data loss. If uncertain, first use a SELECT statement to verify the conditions.

### 5.2 Performance Optimization Suggestions

1. **Query Optimization**:
   - When performing conditional queries, place high-filtering attribute fields on the left side of the conditions.
   - Avoid using IN clauses on large tables; consider using join queries instead.
   - Avoid unnecessary sorting operations.

2. **Index Optimization**:
   - Index foreign key fields and fields frequently used in query conditions.
   - Use composite indexes wisely, placing fields with higher query frequencies at the front of the index.
   - Regularly review and remove unused indexes.

3. **Complex SQL**:
   - For very complex SQL (especially those with multiple layers of nesting and subqueries), first consider whether they are caused by poor design.
   - Use EXPLAIN ANALYZE to analyze query performance.

4. **Connection Pool**:
   - Use a connection pool to manage database connections, improving connection reuse rates.

---

Adhering to the above database design best practices helps improve system performance, maintainability, and data consistency. In actual development, these principles should be flexibly applied according to specific business scenarios and consistent standards should be maintained within the team.
