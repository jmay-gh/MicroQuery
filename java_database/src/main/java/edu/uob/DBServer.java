package edu.uob;

import DataStuctures.DBHandler;
import DataStuctures.FileHandler;
import Parser.Parser;
import Visitors.ASTVisitor;
import Visitors.VisitorHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;

public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;

    private VisitorHandler selector;
    private DBHandler dbHandler;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();

        selector = new VisitorHandler();
        dbHandler = new DBHandler();
        // Attempt to load existing databases if they exist
        FileHandler.loadDatabases(dbHandler);

        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    public String handleCommand(String command) {
        Parser parser = new Parser();
        String result;
        try {
            parser.parse(command);
            ASTVisitor visitor = selector.selectVisitor(parser.getTokens(), dbHandler);
            parser.getRootNode().accept(visitor);
            result = visitor.getResult();
        }
        catch (Exception error) {
            return "[ERROR] " + error.getMessage();
        }
        return "[OK]" + result;
    }

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
