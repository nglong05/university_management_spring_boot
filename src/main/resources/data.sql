USE universitydb;

INSERT IGNORE INTO users(username, password_hash, role, ma_sv, ma_gv, enabled) VALUES
   ('admin','{noop}admin','ADMIN',NULL,NULL,TRUE);
