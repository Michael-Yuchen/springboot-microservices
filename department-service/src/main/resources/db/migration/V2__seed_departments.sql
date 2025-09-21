INSERT INTO department.departments (name, code, description)
VALUES
  ('Engineering', 'ENG001', 'Builds and maintains products'),
  ('HR', 'HR001', 'People operations and recruiting')
ON CONFLICT DO NOTHING;
