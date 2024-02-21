BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "user" (
                                      "id"	INTEGER NOT NULL UNIQUE,
                                      "name"	TEXT NOT NULL,
                                      PRIMARY KEY("id" AUTOINCREMENT)
    );
CREATE TABLE IF NOT EXISTS "order" (
                                       "id"	INTEGER NOT NULL UNIQUE,
                                       "name"	TEXT NOT NULL,
                                       PRIMARY KEY("id" AUTOINCREMENT)
    );
COMMIT;
