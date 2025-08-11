-- Create organization table
CREATE TABLE IF NOT EXISTS organizations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add index on name column
CREATE INDEX IF NOT EXISTS idx_organizations_name ON organizations(name);

-- Insert sample data
INSERT INTO organizations (name, description) VALUES 
    ('Acme Corporation', 'A sample organization for testing'),
    ('Globex Inc', 'Another sample organization for testing')
ON CONFLICT (name) DO NOTHING;