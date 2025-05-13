package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorTests {

    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    @Test
    public void UseTests() {
        // Assert can't use non-existent database
        String result = server.handleCommand("USE DB1;");
        assertEquals("[ERROR] Database 'DB1' doesn't exist", result);
        // Assert can use existing database
        server.handleCommand("CREATE DATABASE DB1;");
        result = server.handleCommand("USE DB1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert database is insensitive
        result = server.handleCommand("UsE dB1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert can swap to another database
        server.handleCommand("CREATE TABLE TABLE1;");
        server.handleCommand("CREATE DATABASE DB2;");
        result = server.handleCommand("USE dB2;");
        assertFalse(result.startsWith("[ERROR]"));
        server.handleCommand("CREATE TABLE TABLE2;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert can swap back and table still there
        server.handleCommand("USE dB1;");
        result = server.handleCommand("CREATE TABLE TABLE1;");
        assertEquals("[ERROR] Table 'TABLE1' already exists", result);
        result = server.handleCommand("CREATE TABLE TABLE2;");
        assertFalse(result.startsWith("[ERROR]"));
        // Clean up
        server.handleCommand("DROP DATABASE DB1;");
        server.handleCommand("DROP DATABASE DB2;");
    }

    @Test
    public void CreateTests() {
        // Assert CAN create database
        String result = server.handleCommand("CREATE DATABASE DB1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN't create duplicate databases
        result = server.handleCommand("CREATE DATABASE DB1;");
        assertEquals("[ERROR] Database 'DB1' already exists", result);
        // Assert CAN re-create database after dropped
        server.handleCommand("DROP DATABASE DB1;");
        result = server.handleCommand("CREATE DATABASE DB1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN'T create table before USE
        result = server.handleCommand("CREATE TABLE TABLE1;");
        assertEquals("[ERROR] Current database not set.", result);
        // Assert CAN create table after USE
        server.handleCommand("USE DB1;");
        result = server.handleCommand("CREATE TABLE TABLE1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN create table with headers
        result = server.handleCommand("CREATE TABLE TABLE2 (JACK, 10);");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN'T create duplicate tables regardless of content
        result = server.handleCommand("CReaTE tABLE TAblE2;");
        assertEquals("[ERROR] Table 'TAblE2' already exists", result);
        result = server.handleCommand("CREATe TabLE TabLE1;");
        assertEquals("[ERROR] Table 'TabLE1' already exists", result);
        // Assert tables are insensitive
        result = server.handleCommand("CREATE TABLE table1;");
        assertEquals("[ERROR] Table 'table1' already exists", result);
        // Assert CAN re-create table after its dropped
        server.handleCommand("DROP TABLE TABLE1;");
        result = server.handleCommand("CreATE TAblE TABLE1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN'T create table after database dropped
        server.handleCommand("DROP DATABASE DB1;");
        result = server.handleCommand("CREATE TABLE table1;");
        assertEquals("[ERROR] Current database not set.", result);
    }

    @Test
    public void DropTests() {
        // Assert CAN'T drop non-existent databases
        String result = server.handleCommand("DROP DATABASE DB1;");
        assertEquals("[ERROR] Database 'DB1' doesn't exist", result);
        // Assert CAN'T drop non-existent table
        server.handleCommand("CREATE DATABASE DB1;");
        server.handleCommand("USE DB1;");
        result = server.handleCommand("DROP TABLE TABLE1;");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        // Assert CAN drop existing database and case-insensitive
        result = server.handleCommand("DroP DAtaBAsE Db1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN drop existing table
        server.handleCommand("CREATE DATABASE DB1;");
        server.handleCommand("USE DB1;");
        server.handleCommand("CREATE TABLE TABLE1;");
        result = server.handleCommand("DrOp TabLE TAblE1;");
        server.handleCommand("CREATE TABLE TABLE1;");
        // Assert TABLE1 can be re-added
        server.handleCommand("CREATE TABLE TABLE1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN drop not current database
        server.handleCommand("CREATE DATABASE DB2;");
        server.handleCommand("USE DB1;");
        result = server.handleCommand("DROP DATABASE DB2;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN'T drop table in non-current database
        server.handleCommand("CREATE DATABASE DB2;");
        server.handleCommand("CREATE TABLE TABLE1;");
        server.handleCommand("USE DB2;");
        result = server.handleCommand("DROP TABLE TABLE1;");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        // Clean up
        server.handleCommand("DROP DATABASE DB1;");
        server.handleCommand("DROP DATABASE DB2;");
    }

    @Test
    public void AlterTests() {
        // Assert CAN'T alter non-existent table
        server.handleCommand("CREATE DATABASE DB1;");
        server.handleCommand("USE DB1;");
        String result = server.handleCommand("ALTER TABLE TABLE1 DROP COLUMN1;");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        // Assert CAN'T DROP non-existent attribute
        server.handleCommand("CREATE TABLE TABLE1;");
        result = server.handleCommand("ALTER TABLE TABLE1 DROP COLUMN1;");
        assertEquals("[ERROR] Column 'COLUMN1' doesn't exist", result);
        // Assert CAN add attribute
        result = server.handleCommand("ALTER TABLE TABLE1 ADD COLUMN1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN'T add duplicate attributes
        result = server.handleCommand("ALTER TABLE TABLE1 ADD COLUMN1;");
        assertEquals("[ERROR] Column 'COLUMN1' already exists", result);
        // Assert CAN'T add id column
        result = server.handleCommand("ALTER TABLE TABLE1 ADD id;");
        assertEquals("[ERROR] Column 'id' already exists", result);
        // Assert CAN drop existing attribute
        result = server.handleCommand("ALTER TABLE TABLE1 DROP COLUMN1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN'T re-drop attribute
        result = server.handleCommand("ALTER TABLE TABLE1 DROP COLUMN1;");
        assertEquals("[ERROR] Column 'COLUMN1' doesn't exist", result);
        // Assert CAN'T drop id column
        result = server.handleCommand("ALTER TABLE TABLE1 DROP id;");
        assertEquals("[ERROR] Cannot drop ID row", result);
        // Assert CAN'T drop or add column after table dropped
        server.handleCommand("ALTER TABLE TABLE1 ADD COLUMN1;");
        server.handleCommand("DROP TABLE TABLE1;");
        result = server.handleCommand("ALTER TABLE TABLE1 ADD COLUMN2;");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        result = server.handleCommand("ALTER TABLE TABLE1 DROP COLUMN1;");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        // Assert adding and dropping is case insensitive
        server.handleCommand("CREATE TABLE TABLE1;");
        result = server.handleCommand("ALTER TABLE TABLE1 ADD column1;");
        assertFalse(result.startsWith("[ERROR]"));
        result = server.handleCommand("ALTER TABLE table1 ADD colUmn1;");
        assertEquals("[ERROR] Column 'column1' already exists", result);
        result = server.handleCommand("ALTER TABLE TABLE1 ADD COLUMN1;");
        assertEquals("[ERROR] Column 'column1' already exists", result);
        result = server.handleCommand("ALTER TABLE table1 DROP COLumn1;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert can't add or drop after database dropped
        server.handleCommand("DROP DATABASE DB1;");
        result = server.handleCommand("ALTER TABLE TABLE1 ADD COLUMN2;");
        assertEquals("[ERROR] Current database not set.", result);
        result = server.handleCommand("ALTER TABLE TABLE1 DROP COLUMN1;");
        assertEquals("[ERROR] Current database not set.", result);
    }

    @Test
    public void InsertTests() {
        // Assert CAN'T insert with no database
        String result = server.handleCommand("INSERT INTO TABLE1 VALUES ('NAME', 10);");
        assertEquals("[ERROR] Current database not set.", result);
        // Assert CAN'T Insert to non-existent table
        server.handleCommand("CREATE DATABASE DB1;");
        server.handleCommand("USE DB1;");
        result = server.handleCommand("INSERT INTO TABLE1 VALUES ('NAME', 10);");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        // Assert CAN'T insert the wrong number of values
        server.handleCommand("CREATE TABLE TABLE1;");
        result = server.handleCommand("INSERT INTO TABLE1 VALUES ('BEN', 10);");
        assertEquals("[ERROR] Number of attributes provided doesn't match the number of columns", result);
        // Assert CAN insert the right number of values
        server.handleCommand("CREATE TABLE TABLE2 (NAME, AGE);");
        result = server.handleCommand("INSERT INTO TABLE2 VALUES ('BEN', 10);");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert insert is case-insensitive
        result = server.handleCommand("insErT iNtO tabLe2 VAluES ('BEN', 10);");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN'T insert after table is dropped
        server.handleCommand("DROP TABLE TABLE2;");
        result = server.handleCommand("INSERT INTO TABLE2 VALUES ('BEN', 10);");
        assertEquals("[ERROR] Table 'TABLE2' doesn't exist", result);
        // Assert CAN'T insert into table after its database is dropped
        server.handleCommand("CREATE TABLE TABLE2 (NAME, AGE);");
        server.handleCommand("DROP DATABASE DB1;");
        result = server.handleCommand("INSERT INTO TABLE1 VALUES ('BEN', 10);");
        assertEquals("[ERROR] Current database not set.", result);
    }

    @Test
    public void SelectTests() {
        // Assert CAN'T select non-existent table
        server.handleCommand("CREATE DATABASE DB1;");
        server.handleCommand("USE DB1;");
        server.handleCommand("CREATE TABLE TABLE1 (NAME, AGE);");
        server.handleCommand("INSERT INTO TABLE1 VALUES ('BEN', 10);");
        String result = server.handleCommand("SELECT NAME FROM TABLE2 WHERE NAME == 'BEN';");
        assertEquals("[ERROR] Table 'TABLE2' doesn't exist", result);
        // Assert CAN select from existing table
        result = server.handleCommand("SELECT NAME FROM TABLE1 WHERE NAME == 'BEN';");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN'T select non-existent attributes
        result = server.handleCommand("SELECT NAME FROM TABLE1 WHERE EMAIL == 'BEN';");
        assertEquals("[ERROR] Attribute 'EMAIL' does not exist", result);
        result = server.handleCommand("SELECT EMAIL FROM TABLE1 WHERE NAME == 'BEN';");
        assertEquals("[ERROR] Attribute 'EMAIL' does not exist", result);
        // Assert passes where conditional doesn't exist
        result = server.handleCommand("SELECT NAME FROM TABLE1 WHERE NAME == 'James';");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert case-insensitive
        result = server.handleCommand("SEleCT naMe FroM table1 WheRE nAMe == 'ben';");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert can't select from table after it's dropped
        server.handleCommand("DROP TABLE TABLE1;");
        result = server.handleCommand("SELECT NAME FROM TABLE1 WHERE NAME == 'BEN';");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        // Assert can't select from table after it's database is dropped
        server.handleCommand("CREATE TABLE TABLE1 (NAME, AGE);");
        server.handleCommand("DROP DATABASE DB1;");
        result = server.handleCommand("SELECT NAME FROM TABLE1 WHERE NAME == 'BEN';");
        assertEquals("[ERROR] Current database not set.", result);
    }

    @Test
    public void JoinTests() {
        // Assert can't join before database set
        String result = server.handleCommand("JOIN TABLE1 AND TABLE2 ON AGE AND AGE;");
        assertEquals("[ERROR] Current database not set.", result);
        server.handleCommand("CREATE DATABASE DB1;");
        result = server.handleCommand("JOIN TABLE1 AND TABLE2 ON AGE AND AGE;");
        assertEquals("[ERROR] Current database not set.", result);
        // Assert can't join non-existent tables
        server.handleCommand("USE DB1;");
        result = server.handleCommand("JOIN TABLE1 AND TABLE2 ON NONE AND NONE;");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        server.handleCommand("CREATE TABLE TABLE1;");
        result = server.handleCommand("JOIN TABLE1 AND TABLE2 ON NONE AND AGE;");
        assertEquals("[ERROR] Table 'TABLE2' doesn't exist", result);
        // Assert can't join non-existent attributes
        server.handleCommand("CREATE TABLE TABLE2;");
        result = server.handleCommand("JOIN TABLE1 AND TABLE2 ON NONE AND NONE;");
        assertEquals("[ERROR] Attribute 'NONE' does not exist", result);
        server.handleCommand("ALTER TABLE TABLE1 ADD AGE;");
        result = server.handleCommand("JOIN TABLE1 AND TABLE2 ON AGE AND NONE;");
        assertEquals("[ERROR] Attribute 'NONE' does not exist", result);
        server.handleCommand("ALTER TABLE TABLE2 ADD AGE;");
        result = server.handleCommand("JOIN TABLE1 AND TABLE2 ON NONE AND AGE;");
        assertEquals("[ERROR] Attribute 'NONE' does not exist", result);
        // Assert CAN join existing tables on existing attributes and case insensitive
        result = server.handleCommand("JoiN TAblE1 AnD TabLE2 On AgE AND AgE;");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert can't join after one table is dropped
        server.handleCommand("DROP taBLE TABLe1;");
        result = server.handleCommand("JoiN TAblE1 AnD TabLE2 On AgE AND AgE;");
        assertEquals("[ERROR] Table 'TAblE1' doesn't exist", result);
        // Assert can't join tables from dropped database
        server.handleCommand("CREATE TABLE TABLE1;");
        server.handleCommand("DROP DATAbaSE dB1;");
        result = server.handleCommand("JoiN TAblE1 AnD TabLE2 On AgE AND AgE;");
        assertEquals("[ERROR] Current database not set.", result);
    }

    @Test
    public void DeleteTests() {
        // Assert can't delete before database added or set
        String result = server.handleCommand("DELETE FROM TABLE1 WHERE name=='Ben';");
        assertEquals("[ERROR] Current database not set.", result);
        server.handleCommand("CREATE DATABASE DB1;");
        result = server.handleCommand("DELETE FROM TABLE1 WHERE name=='Ben';");
        assertEquals("[ERROR] Current database not set.", result);
        // Assert can't delete before table is created
        server.handleCommand("USE DB1;");
        result = server.handleCommand("DELETE FROM TABLE1 WHERE name=='Ben';");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        // Assert allows deleting non-existent data
        server.handleCommand("CREATE TABLE TABLE1;");
        result = server.handleCommand("DELETE FROM TABLE1 WHERE name=='Ben';");
        assertFalse(result.startsWith("[ERROR]"));
        server.handleCommand("ALTER TABLE TABLE1 ADD name;");
        result = server.handleCommand("DELETE FROM TABLE1 WHERE name=='Ben';");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN delete existing data
        server.handleCommand("CREATE TABLE TABLE2 (NAME, AGE, EMAIL);");
        server.handleCommand("INseRT InTO TabLE2 VAluES ('BEN', 10, 'ben@10');");
        server.handleCommand("INSERT InTO TAblE2 VALUES ('JAMES', 12, 'james@10');");
        result = server.handleCommand("DELETE FROM TABLE2 WHERE name=='Ben';");
        assertFalse(result.startsWith("[ERROR]"));
        result = server.handleCommand("SELECT * FROM TABLE2 WHERE name=='Ben';");
        assertEquals("[OK]\nid\tNAME\tAGE\tEMAIL\t\n", result);
        // Assert case insensitive
        server.handleCommand("INseRT InTO TabLE2 VAluES ('BEN', 10, 'ben@10');");
        result = server.handleCommand("DELETE FROM TABLE2 WHERE name=='Ben';");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert can't delete from dropped table
        server.handleCommand("DROP TABLE TABLE2;");
        result = server.handleCommand("DELETE FROM TABLE2 WHERE name=='Ben';");
        assertEquals("[ERROR] Table 'TABLE2' doesn't exist", result);
        // Assert can't delete from table when database dropped
        server.handleCommand("DROP DATABASE DB1;");
        result = server.handleCommand("DELETE FROM TABLE1 WHERE name=='Ben';");
        assertEquals("[ERROR] Current database not set.", result);
    }

    @Test
    public void UpdateTests() {
        // Assert can't update before database added or set
        String result = server.handleCommand("UPDATE TABLE1 SET name='James' WHERE name=='Ben';");
        assertEquals("[ERROR] Current database not set.", result);
        server.handleCommand("CREATE DATABASE DB1;");
        result = server.handleCommand("UPDATE TABLE1 SET name='James' WHERE name=='Ben';");
        assertEquals("[ERROR] Current database not set.", result);
        // Assert can't update before table is created
        server.handleCommand("USE DB1;");
        result = server.handleCommand("UPDATE TABLE1 SET name='James' WHERE name=='Ben';");
        assertEquals("[ERROR] Table 'TABLE1' doesn't exist", result);
        // Assert can't update non-existent attributes
        server.handleCommand("CREATE TABLE TABLE1;");
        result = server.handleCommand("UPDATE TABLE1 SET name='James' WHERE name=='Ben';");
        assertEquals("[ERROR] Attribute 'name' not in table", result);
        // Assert allows update non-existent data
        server.handleCommand("CREATE TABLE TABLE2 (NAME, AGE, EMAIL);");
        result = server.handleCommand("UPDATE TABLE2 SET name='James' WHERE name=='Ben';");
        assertFalse(result.startsWith("[ERROR]"));
        // Assert CAN update existing data
        server.handleCommand("INseRT InTO TabLE2 VAluES ('BEN', 10, 'ben@10');");
        server.handleCommand("INSERT InTO TAblE2 VALUES ('JAMES', 12, 'james@10');");
        result = server.handleCommand("UPDATE TABLE2 SET name='James' WHERE name=='Ben';");
        assertFalse(result.startsWith("[ERROR]"));
        result = server.handleCommand("SELECT id, NAME FROM TABLE2 WHERE name=='James';");
        assertEquals("[OK]\nid\tNAME\t\n1\tJames\t\n2\tJAMES\t\n", result);
        // Assert case-insensitive
        result = server.handleCommand("UPdaTE TAblE2 SeT name='John' WHerE name=='JAMES';");
        assertFalse(result.startsWith("[ERROR]"));
        result = server.handleCommand("SELECT id, NAME FROM TABLE2 WHERE name=='John';");
        assertEquals("[OK]\nid\tNAME\t\n1\tJohn\t\n2\tJohn\t\n", result);
        // Assert can't update dropped table
        server.handleCommand("DROP TABLE TABLE2;");
        result = server.handleCommand("UPdaTE TAblE2 SeT name='John' WHerE name=='JAMES';");
        assertEquals("[ERROR] Table 'TAblE2' doesn't exist", result);
        // Assert can't update table in dropped database
        server.handleCommand("DROP DATABASE DB1;");
        result = server.handleCommand("UPDATE TABLE1 SET name='James' WHERE name=='Ben';");
        assertEquals("[ERROR] Current database not set.", result);
    }

}
