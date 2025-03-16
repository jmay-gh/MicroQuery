# MicroQuery
MicroQuery is a lightweight SQL-like database server with persistent file storage. It features a custom-built parser, interpreter, and evaluator for executing structured queries. Designed for efficiency and flexibility, MicroQuery allows clients to interact with stored data through a simple query language, all while maintaining persistence on disk.

###  🔑 Features
- 🔹 **SQL-like Query Language** – Supports `SELECT`, `INSERT`, `UPDATE`, `DELETE`, etc.
- 🔹 **Persistent Storage** – Data is persistently saved to the disk.
- 🔹 **Client-Server Architecture** – Supports client access 🔗
- 🔹 **BNF-Based Parsing** – Uses a well-defined [Backus-Naur Form grammar](docs/queryBNF).
- 🔹 **Cross-Platform Compatibility** – Runs on Windows, Linux, and macOS. 

## 🛠 Get Started

### 1️⃣ Clone the repository
Clone this repo onto your computer and enter the project.
```
git clone https://github.com/jmay-gh/MicroQuery.git
cd MicroQuery
```

### 2️⃣ Startup the client and server
The server and client can be started by executing the start script:
```
./start.sh
```

### 3️⃣ Execute queries
You can run queries directly from the client. Get started by creating a database!
```
CREATE DATABASE myNewDatabase;
```
Check out some [examples here](docs/examples) for help.

## 📁 Project Structure

Here's a breakdown of the programs structure:
```
/MicroQuery
├── README.md               # You're currently here
├── /src                    # Source code directory
│   ├── /Parser/            # BNF parser logic
│   ├── /Interpreter/       # Query execution logic
│   ├── /DataStructure/     # Data storage logic
│   ├── DBServer.java       # Server logic
│   ├── DBClient.java       # Client logic
├── /databases              # Database file storage
├── /docs                   # Project documentation
├── LICENSE                 # License information
├── .gitignore              # Git ignore rules
```

