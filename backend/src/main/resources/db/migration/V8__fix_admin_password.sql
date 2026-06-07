UPDATE users
SET password_hash = (
    SELECT ph FROM (
                       SELECT password_hash AS ph FROM users WHERE email = 'marko.strength@fitschedule.com'
                   ) AS tmp
)
WHERE email = 'admin@fitschedule.com';