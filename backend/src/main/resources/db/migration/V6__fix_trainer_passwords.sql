-- Fix trainer password hashes (V2 had an incorrect hash; correct hash is for Test1234!)
UPDATE users
SET password_hash = '$2a$10$GVq4ZSb2xF4f2Q0vKrmTw.cPRINclHoihg270Phz3EJFq6M7eGloe'
WHERE user_type = 'TRAINER';