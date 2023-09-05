CREATE TABLE IF NOT EXISTS books (
   id               INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
   title            VARCHAR (100) NOT NULL,
   author           VARCHAR (100) NOT NULL,
   written_date     DATE NOT NULL,
   publisher        VARCHAR (100) NOT NULL,
   publication_date DATE NOT NULL,
   pages            INTEGER NOT NULL,
   language         VARCHAR (100) NOT NULL,
   copies           INTEGER DEFAULT 0,
   created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   deleted_at       TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS genres (
    id    INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name  VARCHAR (100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genre_book (
    genre_id INTEGER NOT NULL,
    book_id INTEGER NOT NULL,
    PRIMARY KEY (genre_id, book_id),
    CONSTRAINT Constr_GenreBook_Genre_fk FOREIGN KEY Genre_fk (genre_id) REFERENCES genres (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT Constr_GenreBook_Book_fk FOREIGN KEY Book_fk (book_id) REFERENCES books (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    id          INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    username    VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    role        VARCHAR(100) DEFAULT 'BORROWER',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS borrowed_books (
    id                  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    book_id             INTEGER NOT NULL,
    borrower_id         INTEGER NOT NULL,
    borrowed_date       DATETIME DEFAULT NOW(),
    expiration_date     DATETIME NOT NULL,
    return_date         DATETIME NULL,
    copies              INTEGER NOT NULL DEFAULT 1,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP NULL
);