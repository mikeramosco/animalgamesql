CREATE TABLE nodes (
  nodeid INTEGER PRIMARY KEY ASC NOT NULL,
  type VARCHAR(16) NOT NULL default 'question',
  text VARCHAR(200) NOT NULL DEFAULT '',
  userid UNSIGNED INTEGER NOT NULL DEFAULT 0,
  timestamp VARCHAR(64) NOT NULL DEFAULT '0000-00-00 00:00:00',
  viewcount UNSIGNED INTEGER NOT NULL DEFAULT 0
);

INSERT INTO nodes VALUES (1, 'answer', 'dog', 0, '0000-00-00 00:00:00', 0);

CREATE TABLE graph (
  graphid INTEGER PRIMARY KEY ASC NOT NULL,
  parentid UNSIGNED INTEGER NOT NULL default 0,
  childid UNSIGNED INTEGER NOT NULL default 0,
  type VARCHAR(5) NOT NULL default 'yes'
);