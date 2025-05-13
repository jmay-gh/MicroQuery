package edu.uob;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OutputTests {

    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        server.handleCommand("CREATE DATABASE StarWars;");
        server.handleCommand("USE StarWars;");
        server.handleCommand("CREATE TABLE Goodies (Name, Species, Job);");
        server.handleCommand("INSERT INTO Goodies VALUES ('Luke', 'Human', 'Jedi');");
        server.handleCommand("INSERT INTO Goodies VALUES ('Han', 'Human', 'Smuggler');");
        server.handleCommand("INSERT INTO Goodies VALUES ('Yoda', 'Unknown', 'Jedi');");
        server.handleCommand("INSERT INTO Goodies VALUES ('Chewbacca', 'Wookiee', 'Unknown');");
        server.handleCommand("INSERT INTO Goodies VALUES ('R2-D2', 'Droid', 'Astromech');");
        server.handleCommand("CREATE TABLE Baddies (Name, Species, Job);");
        server.handleCommand("INSERT INTO Baddies VALUES ('Vader', 'Human', 'Sith');");
        server.handleCommand("INSERT INTO Baddies VALUES ('Palpatine', 'Human', 'Sith');");
        server.handleCommand("INSERT INTO Baddies VALUES ('Leia', 'Human', 'Senator');");
    }

    @AfterEach
    public void cleanUp() {
        server.handleCommand("DROP DATABASE StarWars;");
    }

    @Test
    public void testUseOutput() {
        // Assert data is preserved between USE commands
        server.handleCommand("CREATE DATABASE StarWars2;");
        server.handleCommand("USE StarWars2;");
        server.handleCommand("CREATE TABLE NewJedis (Name, Job, Age);");
        server.handleCommand("INSERT INTO NewJedis VALUES ('Anakin', 'Jedi', 30);");
        server.handleCommand("USE StarWars;");
        server.handleCommand("USE StarWars2;");
        String result = server.handleCommand("SELECT * FROM NewJedis;");
        assertEquals("[OK]\n" +
                "id\tName\tJob\tAge\t\n" +
                "1\tAnakin\tJedi\t30\t\n", result);
        // Assert can close the server and then use the same database again
        server = new DBServer();
        server.handleCommand("USE StarWars2;");
        result = server.handleCommand("SELECT * FROM NewJedis;");
        assertEquals("[OK]\n" +
                "id\tName\tJob\tAge\t\n" +
                "1\tAnakin\tJedi\t30\t\n", result);
        server.handleCommand("DROP DATABASE StarWars2;");
    }

    @Test
    public void testCreateOutputs() {
        server.handleCommand("CREATE DATABASE StarWars2;");
        server.handleCommand("USE StarWars2;");
        server.handleCommand("CREATE TABLE Planets (System, Species, Race);");
        String result = server.handleCommand("SELECT * FROM Planets;");
        assertEquals("[OK]\nid\tSystem\tSpecies\tRace\t\n", result);
        // Assert server can be restarted and table remains
        server = new DBServer();
        server.handleCommand("USE StarWars2;");
        result = server.handleCommand("SELECT * FROM Planets;");
        assertEquals("[OK]\nid\tSystem\tSpecies\tRace\t\n", result);
    }

    @Test
    public void testDropOutput() {
        // Assert returns table dropped and can't be accessed
        String result = server.handleCommand("DROP TABLE Goodies;");
        assertEquals("[OK]", result);
        result = server.handleCommand("SELECT * FROM Goodies;");
        assertTrue(result.contains("[ERROR]"));
        // Assert server can be restarted and table still dropped
        server = new DBServer();
        server.handleCommand("USE StarWars;");
        result = server.handleCommand("DROP TABLE Baddies;");
        assertEquals("[OK]", result);
        result = server.handleCommand("SELECT * FROM Baddies;");
        assertTrue(result.contains("[ERROR]"));
        // Assert returns database dropped and can't be used
        result = server.handleCommand("DROP DATABASE StarWars;");
        assertEquals("[OK]", result);
        result = server.handleCommand("USE StarWars;");
        assertTrue(result.contains("[ERROR]"));
    }

    @Test
    public void testUpdateOutput() {
        // Assert table can be updated
        server.handleCommand("UPDATE Goodies SET Job='Jedi Master' WHERE Name=='Luke';");
        String result = server.handleCommand("SELECT Job FROM Goodies WHERE Name=='Luke';");
        assertTrue(result.contains("Jedi Master"));
        result = server.handleCommand("SELECT Job FROM Goodies WHERE Name=='Leia';");
        assertFalse(result.contains("Jedi Master"));
        // Assert server can be restarted and updates persist
        server = new DBServer();
        server.handleCommand("USE StarWars;");
        result = server.handleCommand("SELECT Job FROM Goodies WHERE Name=='Luke';");
        assertTrue(result.contains("Jedi Master"));
    }

    @Test
    public void testAlterOutput() {
        // Assert can add column
        server.handleCommand("ALTER TABLE Baddies ADD Alive;");
        String result = server.handleCommand("SELECT * FROM Baddies;");
        assertTrue(result.contains("Alive"));
        // Assert can add data to new column
        result = server.handleCommand("INSERT INTO Baddies VALUES ('Greedo', 'Rodian', 'BountyHunter', FALSE);");
        assertTrue(result.contains("[OK]"));
        // Assert can remove column
        server.handleCommand("ALTER TABLE Goodies DROP Job;");
        result = server.handleCommand("SELECT * FROM Goodies;");
        assertFalse(result.contains("Job"));
        // Assert server restart preserves alterations
        server = new DBServer();
        server.handleCommand("USE StarWars;");
        result = server.handleCommand("SELECT * FROM Baddies WHERE name=='Greedo';");
        assertTrue(result.contains("Greedo") && result.contains("FALSE"));
        result = server.handleCommand("SELECT Job FROM Goodies;");
        assertTrue(result.contains("[ERROR]"));
    }

    @Test
    public void testDeleteOutput() {
        server.handleCommand("DELETE FROM Goodies WHERE Name=='Yoda';");
        String result = server.handleCommand("SELECT Name FROM Goodies WHERE Name=='Yoda';");
        assertFalse(result.contains("Yoda"));
        server.handleCommand("DELETE FROM Goodies WHERE (Name=='Luke') AND (job=='Jedi');");
        result = server.handleCommand("SELECT Name FROM Goodies WHERE Name=='Luke';");
        assertFalse(result.contains("Yoda"));
        // Assert deletion persists after server restart
        server = new DBServer();
        server.handleCommand("USE StarWars;");
        result = server.handleCommand("SELECT Name FROM Goodies;");
        assertFalse(result.contains("Yoda") || result.contains("Luke"));
    }

    @Test
    public void testInsertOutput() {
        // Assert can't insert too many or two few rows
        String result = server.handleCommand("INSERT INTO Baddies VALUES ('Greedo', 'Rodian', 'BountyHunter', FALSE);");
        assertEquals("[ERROR] Number of attributes provided doesn't match the number of columns", result);
        result = server.handleCommand("INSERT INTO Baddies VALUES ('Greedo', 'Rodian');");
        assertEquals("[ERROR] Number of attributes provided doesn't match the number of columns", result);
        // Assert can insert right amount
        result = server.handleCommand("INSERT INTO Baddies VALUES ('Greedo', 'Rodian', 'BountyHunter');");
        assertTrue(result.contains("[OK]"));
        result = server.handleCommand("SELECT Name FROM Baddies WHERE Name=='Greedo';");
        assertTrue(result.contains("Greedo"));
        // Assert the new row persists after server restart
        server = new DBServer();
        server.handleCommand("USE StarWars;");
        result = server.handleCommand("SELECT Name FROM Baddies WHERE Name=='Greedo';");
        assertTrue(result.contains("Greedo"));
    }

    @Test
    public void testSelectOutput() {
        // Assert can wild card match
        String result = server.handleCommand("SELECT * FROM Goodies WHERE Job=='Jedi';");
        assertTrue(result.contains("Luke") && result.contains("Yoda") && !result.contains("Vader"));
        // Assert the output is in the order of the attributes listed
        result = server.handleCommand("SELECT name, id, job FROM Goodies WHERE job=='Jedi';");
        assertTrue(result.contains("[OK]\nName\tid\tJob\t\nLuke\t1\tJedi\t\nYoda\t3\tJedi\t\n"));
        // Assert selects are the same after server restarts
        server = new DBServer();
        server.handleCommand("USE StarWars;");
        result = server.handleCommand("SELECT * FROM Goodies WHERE Job=='Jedi';");
        assertTrue(result.contains("Luke") && result.contains("Yoda") && !result.contains("Vader"));
        result = server.handleCommand("SELECT name, id, job FROM Goodies WHERE job=='Jedi';");
        assertTrue(result.contains("[OK]\nName\tid\tJob\t\nLuke\t1\tJedi\t\nYoda\t3\tJedi\t\n"));
    }

    @Test
    public void testJoinOutputs() {
        // Assert can join two different tables
        String result = server.handleCommand("JOIN Goodies AND Baddies ON Species AND Species;");
        assertEquals("[OK]\n" +
                "id\tgoodies.Name\tgoodies.Job\tbaddies.Name\tbaddies.Job\t\n" +
                "1\tLuke\tJedi\tVader\tSith\t\n" +
                "2\tHan\tSmuggler\tVader\tSith\t\n" +
                "3\tLuke\tJedi\tPalpatine\tSith\t\n" +
                "4\tHan\tSmuggler\tPalpatine\tSith\t\n" +
                "5\tLuke\tJedi\tLeia\tSenator\t\n" +
                "6\tHan\tSmuggler\tLeia\tSenator\t\n", result);
        // Assert can join same table
        result = server.handleCommand("JOIN Baddies AND Baddies ON Job AND Job;");
        assertEquals("[OK]\n" +
                "id\tbaddies.Name\tbaddies.Species\tbaddies.Name\tbaddies.Species\t\n" +
                "1\tVader\tHuman\tVader\tHuman\t\n" +
                "2\tPalpatine\tHuman\tVader\tHuman\t\n" +
                "3\tVader\tHuman\tPalpatine\tHuman\t\n" +
                "4\tPalpatine\tHuman\tPalpatine\tHuman\t\n" +
                "5\tLeia\tHuman\tLeia\tHuman\t\n", result);
        // Assert can close the server and get same response
        server = new DBServer();
        server.handleCommand("USE StarWars;");
        result = server.handleCommand("JOIN Baddies AND Baddies ON Job AND Job;");
        assertEquals("[OK]\n" +
                "id\tbaddies.Name\tbaddies.Species\tbaddies.Name\tbaddies.Species\t\n" +
                "1\tVader\tHuman\tVader\tHuman\t\n" +
                "2\tPalpatine\tHuman\tVader\tHuman\t\n" +
                "3\tVader\tHuman\tPalpatine\tHuman\t\n" +
                "4\tPalpatine\tHuman\tPalpatine\tHuman\t\n" +
                "5\tLeia\tHuman\tLeia\tHuman\t\n", result);
    }

}
