package edu.uob;

import Parser.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {

    private Parser parser;

    @BeforeEach
    public void setup() {
        parser = new Parser();
    }

    @Test
    public void SelectTests() {
        // Assert select command takes list attributes
        assertDoesNotThrow(() -> parser.parse("SELECT species FROM aliens;"));
        assertDoesNotThrow(() -> parser.parse("SELECT species, age FROM aliens;"));
        // Assert wild card is passed
        assertDoesNotThrow(() -> parser.parse("SELECT * FROM aliens;"));
        // Assert can't pass wild AND attributes
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT *, species FROM aliens;"));
        // Assert doesn't accept invalid list forms
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT ,species, age FROM aliens;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT species, age, FROM aliens;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT species age FROM aliens;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT (species, age) FROM aliens;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT (*) FROM aliens; "));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT (species) FROM aliens;"));
        // Assert can't omit keywords
        assertThrows(IllegalArgumentException.class, () -> parser.parse("species FROM aliens;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT FROM aliens;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT species aliens;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("SELECT species FROM;"));
        // Assert can have condition
        assertDoesNotThrow(() -> parser.parse("SELECT species FROM aliens WHERE name=='Jabba';"));
        assertDoesNotThrow(() -> parser.parse("SELECT * FROM aliens WHERE name=='Jabba' AND planet=='Nal Hutta';"));
        assertDoesNotThrow(() -> parser.parse("SELECT * FROM aliens WHERE (age<='Jabba') AND planet LIKE 'Nal Hutta';"));
    }

    @Test
    public void AlterTests() {
        // Assert alter takes correct syntax
        assertDoesNotThrow(() -> parser.parse("ALTER TABLE aliens DROP BobaFett;"));
        assertDoesNotThrow(() -> parser.parse("ALTER TABLE aliens ADD ObiWan;"));
        // Assert won't accept attribute list
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ALTER TABLE aliens ADD ObiWan, Anakin;"));
        // Assert all keywords are required
        assertThrows(IllegalArgumentException.class, () -> parser.parse("TABLE aliens ADD ObiWan;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ALTER aliens ADD ObiWan;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ALTER TABLE ADD ObiWan;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ALTER TABLE aliens ObiWan;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ALTER TABLE aliens ADD;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("ALTER TABLE aliens DROP;"));
    }

    @Test
    public void UpdateTests() {
        // Assert update tests take correct syntax
        assertDoesNotThrow(() -> parser.parse("UPDATE aliens SET job='Jedi' WHERE name=='Luke';"));
        assertDoesNotThrow(() -> parser.parse("UPDATE aliens SET job='Jedi', age=25 WHERE name=='Luke';"));
        // Assert the incorrect syntax is rejected
        assertThrows(IllegalArgumentException.class, () -> parser.parse("aliens SET job='Jedi' WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE SET job='Jedi' WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens job='Jedi' WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET job='Jedi' name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET job='Jedi' WHERE ;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET job='Jedi' WHERE name=='Luke'"));
        // Assert invalid name=value list formats rejected
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET job='Jedi', age=25, WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET ,job='Jedi', age=25 WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET ,job='Jedi', age=25, WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET job='Jedi',, age=25 WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET (job='Jedi', age=25) WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET job=='Jedi', age=25 WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET 'job'='Jedi', age=25 WHERE name=='Luke';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("UPDATE aliens SET job=Jedi, age=25 WHERE name=='Luke';"));
        // Assert can have varied conditions
        assertDoesNotThrow(() -> parser.parse("UPDATE aliens SET job='Jedi' WHERE (name=='Luke')AND((age==30))OR(lightsaber LIKE 'green');"));
        assertDoesNotThrow(() -> parser.parse("UPDATE aliens SET job='Jedi', age=25 WHERE name=='Luke';"));
    }

    @Test
    public void UseTests() {
        // Assert use tests take correct syntax
        assertDoesNotThrow(() -> parser.parse("USE DB1;"));
        assertDoesNotThrow(() -> parser.parse("use db1 ;"));
        // Assert the incorrect syntax is rejected
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DB1;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("USE;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("USE DB1"));
        // Assert only accepts identifiers and integers
        assertDoesNotThrow(() -> parser.parse("USE DB1;"));
        assertDoesNotThrow(() -> parser.parse("USE 1;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("USE 'DB1';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("USE TRUE;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("USE FALSE;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("USE NULL;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("USE 1.1;"));
    }

    @Test
    public void InsertVoid() {
        // Assert use tests take correct syntax
        assertDoesNotThrow(() -> parser.parse("INSERT INTO TABLE1 VALUES ('Obiwan', 'Blue', 'Jedi', 'Human');"));
        assertDoesNotThrow(() -> parser.parse("insert into table1 values ('Obiwan', 'Blue', 'Jedi', 'Human');"));
        // Assert the incorrect syntax is rejected
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INTO TABLE1 VALUES ('Obiwan', 'Jedi')"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT TABLE1 VALUES ('Obiwan', 'Jedi')"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO VALUES ('Obiwan', 'Jedi')"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO TABLE1 ('Obiwan', 'Jedi')"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO TABLE1 VALUES"));
        // Assert that the values take only the correct form
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO TABLE1 VALUES ('Obiwan' 'Jedi');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO TABLE1 VALUES ('Obiwan',);"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO TABLE1 VALUES 'Obiwan', 'Jedi');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO TABLE1 VALUES ('Obiwan', 'Jedi';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO TABLE1 VALUES 'Obiwan', 'Jedi';"));
        // Assert values only take correct types
        assertDoesNotThrow(() -> parser.parse("INSERT INTO TABLE1 VALUES ('Obiwan', 30.3, TRUE, FALSE, NULL, 4);"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("INSERT INTO TABLE1 VALUES (Obiwan, Jedi);"));
    }

    @Test
    public void DeleteTests() {
        // Assert use tests take correct syntax
        assertDoesNotThrow(() -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan');"));
        assertDoesNotThrow(() -> parser.parse("delete from TABLE1 where (name == 'Obiwan');"));
        // Assert the incorrect syntax is rejected
        assertThrows(IllegalArgumentException.class, () -> parser.parse("FROM TABLE1 WHERE (name == 'Obiwan');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE TABLE1 WHERE (name == 'Obiwan');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM WHERE (name == 'Obiwan');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 (name == 'Obiwan');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE;"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan')"));
        // Assert that the condition only the correct form
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan') AND (lightsaber==blue);"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE name == 'Obiwan') AND (lightsaber=='blue');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan' AND (lightsaber=='blue');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan') AND lightsaber=='blue');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan') AND (lightsaber=='blue';"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan') (lightsaber=='blue');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name = 'Obiwan') AND (lightsaber=='blue');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan') AND (lightsaber!=='blue');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE (name == 'Obiwan' lightsaber=='blue');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE ('Obiwan' AND 'Obiwan') == (lightsaber=='blue');"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("DELETE FROM TABLE1 WHERE ('Obiwan' == 'Obiwan') == (lightsaber=='blue');"));
    }

    @Test
    public void JoinTests() {
        assertDoesNotThrow(() -> parser.parse("JOIN Jedis AND Sith ON Force AND Species;"));
        assertDoesNotThrow(() -> parser.parse("join Jedis and Sith on Force and Species;"));

    }

    @Test
    public void DropTests() {


    }

    @Test
    public void CreateTests() {


    }

}
