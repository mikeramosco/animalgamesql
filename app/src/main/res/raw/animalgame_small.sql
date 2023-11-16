CREATE TABLE nodes (
  nodeid INTEGER PRIMARY KEY ASC NOT NULL,
  type VARCHAR(16) NOT NULL default 'question',
  text VARCHAR(200) NOT NULL DEFAULT '',
  userid UNSIGNED INTEGER NOT NULL DEFAULT 0,
  timestamp VARCHAR(64) NOT NULL DEFAULT '0000-00-00 00:00:00',
  viewcount UNSIGNED INTEGER NOT NULL DEFAULT 0
);

INSERT INTO nodes VALUES (1, 'question', 'Does it have 4 legs?', 0, '0000-00-00 00:00:00', 0);
INSERT INTO nodes VALUES (2, 'question', 'Is it hairy?', 0, '0000-00-00 00:00:00', 0);
INSERT INTO nodes VALUES (3, 'question', 'Does it bark?', 0, '0000-00-00 00:00:00', 0);
INSERT INTO nodes VALUES (4, 'answer', 'dog', 0, '0000-00-00 00:00:00', 0);
INSERT INTO nodes VALUES (5, 'answer', 'cat', 0, '0000-00-00 00:00:00', 0);
INSERT INTO nodes VALUES (6, 'answer', 'elephant', 0, '0000-00-00 00:00:00', 0);
INSERT INTO nodes VALUES (7, 'question', 'Does it swim?', 0, '0000-00-00 00:00:00', 0);
INSERT INTO nodes VALUES (8, 'answer', 'teacher', 0, '0000-00-00 00:00:00', 0);
INSERT INTO nodes VALUES (9, 'answer', 'dragon', 0, '0000-00-00 00:00:00', 0);

CREATE TABLE graph (
  graphid INTEGER PRIMARY KEY ASC NOT NULL,
  parentid UNSIGNED INTEGER NOT NULL default 0,
  childid UNSIGNED INTEGER NOT NULL default 0,
  type VARCHAR(5) NOT NULL default 'yes'
);

INSERT INTO graph VALUES (101, 1, 2, 'yes');
INSERT INTO graph VALUES (102, 1, 7, 'no');
INSERT INTO graph VALUES (103, 2, 3, 'yes');
INSERT INTO graph VALUES (104, 2, 6, 'no');
INSERT INTO graph VALUES (105, 3, 4, 'yes');
INSERT INTO graph VALUES (106, 3, 5, 'no');
INSERT INTO graph VALUES (107, 7, 8, 'yes');
INSERT INTO graph VALUES (108, 7, 9, 'no');