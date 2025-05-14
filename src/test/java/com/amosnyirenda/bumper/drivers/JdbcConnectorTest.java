package com.amosnyirenda.bumper.drivers;

import com.amosnyirenda.bumper.core.*;
import com.amosnyirenda.bumper.events.EventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;




class JdbcConnectorTest {

    DBConnectionManager connectionManager = new DBConnectionManager.ConnectionBuilder()
            .withUrl("jdbc:sqlite:C:/Users/pc/Documents/mylibrary.db")
            .withDb(DBType.SQLITE)
            .buildConnection();

    DBConnector connector = connectionManager.getConnector();
    EventManager eventManager = connectionManager.getEventManager();

    DBQueryBuilder builder = connectionManager.getQueryBuilder();
    DBQueryHandler handler = builder
            .target("books")
            .query("select * from books")
            .buildHandler()
            .withConnector(connector)
            .withEventManager(eventManager);


    int insertedId;

    @BeforeEach
    void setup() {
        try {
            Connection connection = (Connection) connector.connect();
            connection.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                author TEXT,
                year INTEGER
            )
        """);

            PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO books (title, author, year) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            insertStmt.setString(1, "Original Title");
            insertStmt.setString(2, "Original Author");
            insertStmt.setInt(3, 2000);
            insertStmt.executeUpdate();

            ResultSet rs = insertStmt.getGeneratedKeys();
            if (rs.next()) {
                insertedId = rs.getInt(1);
            }

            insertStmt.close();
            rs.close();
        } catch (SQLException e) {
            fail("Setup failed due to SQL exception: " + e.getMessage());
        }
    }

    @Test
    void testUpdateEntry() {
        Map<String, Object> updatedFields = Map.of(
                "title", "Harry Potter and the Philosopher's Stone"
        );

        boolean updated = handler.update("id", String.valueOf(insertedId), updatedFields);
        assertTrue(updated, "Book should be updated");

    }



    @Test
    void testGetEntries(){

        Map<String, Object> book = new HashMap<>();
        book.put("title", "Clean Code");
        book.put("author", "Robert C. Martin");
        book.put("year", 1990);

        boolean inserted = handler.insert(book);
        assertTrue(inserted, "The book should be inserted successfully");

        List<Map<String, Object>> entries = handler.getEntries();

        boolean found = entries.stream().anyMatch(entry ->
                "Clean Code".equals(entry.get("title")) &&
                        "Robert C. Martin".equals(entry.get("author"))
                        );

        assertTrue(found, "Inserted book should be present in retrieved entries");

    }

    @Test
    void testDeleteEntry() {
        boolean deleted = handler.delete("id", String.valueOf(insertedId));
        assertTrue(deleted, "Book should be deleted");
    }
}