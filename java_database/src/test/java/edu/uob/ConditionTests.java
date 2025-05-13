package edu.uob;

import DataStuctures.*;
import Nodes.*;
import Parser.*;
import Visitors.ConditionVisitor;
import Visitors.SelectVisitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionTests {

    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        server.handleCommand("CREATE DATABASE StarWars;");
        server.handleCommand("USE StarWars;");
        server.handleCommand("CREATE TABLE Characters (Name, Species, Job,  hasForce, numMidachlorians, Planet, Age);");
        server.handleCommand("INSERT INTO Characters VALUES ('Luke', 'Human', 'Jedi', TRUE, 15000, 'Tatooine', 23);");
        server.handleCommand("INSERT INTO Characters VALUES ('Leia', 'Human', 'Senator', FALSE, 0, 'Alderaan', 23);");
        server.handleCommand("INSERT INTO Characters VALUES ('Han', 'Human', 'Smuggler', FALSE, 0, 'Corellia', 32);");
        server.handleCommand("INSERT INTO Characters VALUES ('Yoda', 'Unknown', 'Jedi', TRUE, 20000, 'Dagobah', 900);");
        server.handleCommand("INSERT INTO Characters VALUES ('Vader', 'Human', 'Sith', TRUE, 19000, 'Mustafar', 45);");
        server.handleCommand("INSERT INTO Characters VALUES ('Palpatine', 'Human', 'Sith', TRUE, 18000, 'Coruscant', 85);");
        server.handleCommand("INSERT INTO Characters VALUES ('Chewbacca', 'Wookiee', 'Unknown', false, 0, 'Kashyyyk', 200);");
        server.handleCommand("INSERT INTO Characters VALUES ('R2-D2', 'Droid', 'Astromech', false, 0, 'Naboo', 50);");
    }

    @AfterEach
    public void cleanUp() {
        server.handleCommand("DROP DATABASE StarWars;");
    }

    @Test
    public void evaluationTests() {
        String result = server.handleCommand("SELECT Name FROM Characters WHERE (numMidachlorians > 10000);");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\nVader\t\nPalpatine\t\n", result);
        result = server.handleCommand("SELECT Name FROM Characters WHERE (species=='Human' AND hasForce==TRUE);");
        assertEquals("[OK]\nName\t\nLuke\t\nVader\t\nPalpatine\t\n", result);
        result = server.handleCommand("SELECT Name FROM Characters WHERE ( job == 'Jedi' OR numMidachlorians >= 19000);");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\nVader\t\n", result);
        result = server.handleCommand("SELECT Name FROM Characters WHERE ( ( species == 'Human' AND job == 'Jedi' ) OR ( numMidachlorians > 18000 ) );");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\nVader\t\n", result);
        result = server.handleCommand("SELECT Name FROM Characters WHERE (name LIKE 'L');");
        assertEquals("[OK]\nName\t\nLuke\t\nLeia\t\nPalpatine\t\n", result);
        result = server.handleCommand("SELECT Name FROM Characters WHERE ((species=='Human' AND job=='Sith') OR (numMidachlorians >= 15000 AND name != 'Vader'));");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\nVader\t\nPalpatine\t\n", result);
    }

    @Test
    public void parenthesisTests() {
        String result = server.handleCommand("SELECT Name FROM Characters WHERE (((species=='Human' AND job=='Sith')));");
        assertEquals("[OK]\nName\t\nVader\t\nPalpatine\t\n", result);
    }

    @Test
    public void likeComparatorTests() {
        // Can LIKEN integers
        String result = server.handleCommand("SELECT Name FROM Characters WHERE (age LIKE 23);");
        assertEquals("[OK]\nName\t\nLuke\t\nLeia\t\n", result);
        // Can LIKEN substrings
        result = server.handleCommand("SELECT Name FROM Characters WHERE (name LIKE 'L');");
        assertEquals("[OK]\nName\t\nLuke\t\nLeia\t\nPalpatine\t\n", result);
        // Assert can boolean LIKES
        result = server.handleCommand("SELECT Name FROM Characters WHERE (name LIKE 'L') AND (name LIKE 'u');");
        assertEquals("[OK]\nName\t\nLuke\t\n", result);
        // Assert can test different types
        result = server.handleCommand("SELECT Name FROM Characters WHERE (name LIKE 5);");
        assertEquals("[OK]\nName\t\n", result);
    }

    @Test
    public void floatInputTests() {
        String result = server.handleCommand("SELECT Name FROM Characters WHERE (numMidachlorians >= 0.5);");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\nVader\t\nPalpatine\t\n", result);
        result = server.handleCommand("SELECT Name FROM Characters WHERE ( job == 'Jedi' AND numMidachlorians >= +0.5);");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\n", result);
        result = server.handleCommand("SELECT Name FROM Characters WHERE (numMidachlorians >= -1.0);");
        assertEquals("[OK]\nName\t\nLuke\t\nLeia\t\nHan\t\nYoda\t\nVader\t\nPalpatine\t\nChewbacca\t\nR2-D2\t\n", result);
    }

    @Test
    public void boolInputTests() {
        // Assert invalid comparator gives nothing
        String result = server.handleCommand("SELECT Name FROM Characters WHERE (Name >= True);");
        assertEquals("[OK]\nName\t\n", result);
        // Assert null returns nothing
        result = server.handleCommand("SELECT Name FROM Characters WHERE (Name >= NULL);");
        assertEquals("[OK]\nName\t\n", result);
        result = server.handleCommand("SELECT Name FROM Characters WHERE (hasForce == False AND hasForce == True);");
        assertEquals("[OK]\nName\t\n", result);
        // Assert passes on ==
        result = server.handleCommand("SELECT Name FROM Characters WHERE (hasForce == tRue);");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\nVader\t\nPalpatine\t\n", result);
        // Assert passes on ==
        result = server.handleCommand("SELECT Name FROM Characters WHERE (hasForce == false);");
        assertEquals("[OK]\nName\t\nLeia\t\nHan\t\nChewbacca\t\nR2-D2\t\n", result);
        // Assert passes on LIKE
        result = server.handleCommand("SELECT Name FROM Characters WHERE (hasForce LIKE true);");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\nVader\t\nPalpatine\t\n", result);
        // Assert passes on LIKE
        result = server.handleCommand("SELECT Name FROM Characters WHERE (hasForce LIKE fAlsE);");
        assertEquals("[OK]\nName\t\nLeia\t\nHan\t\nChewbacca\t\nR2-D2\t\n", result);
        // Assert passes on !=
        result = server.handleCommand("SELECT Name FROM Characters WHERE (hasForce != true);");
        assertEquals("[OK]\nName\t\nLeia\t\nHan\t\nChewbacca\t\nR2-D2\t\n", result);
        // Assert passes on !=
        result = server.handleCommand("SELECT Name FROM Characters WHERE (hasForce != false);");
        assertEquals("[OK]\nName\t\nLuke\t\nYoda\t\nVader\t\nPalpatine\t\n", result);
    }

}
