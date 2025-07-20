CREATE EXTENSION IF NOT EXISTS dblink;

DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'app_user') THEN
      CREATE USER app_user WITH PASSWORD '#nem_reader123$';
   END IF;
END
$$;

GRANT USAGE ON SCHEMA public TO app_user;
GRANT CREATE ON SCHEMA public TO app_user;
