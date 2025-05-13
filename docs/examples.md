
# 💼 MicroQuery Usage Guide – *Finance Tracker*

This guide demonstrates how to use **MicroQuery** to manage databases and tables, relatingg to client accounts, transactions, and financial data. It includes valid queries, joins, updates, deletions, and error-handling examples.

---

## 📁 1. Creating and Selecting a Database

```sql
CREATE DATABASE finance_tracker;
[OK]

USE finance_tracker;
[OK]
```

---

## 📋 2. Creating Tables

```sql
CREATE TABLE clients (full_name, balance, active);
[OK]
```

---

## ✍️ 3. Inserting Client Records

```sql
INSERT INTO clients VALUES ('Emma Taylor', 10500.75, TRUE);
[OK]
INSERT INTO clients VALUES ('James Lee', 6300.00, TRUE);
[OK]
INSERT INTO clients VALUES ('Olivia Chen', 1200.25, FALSE);
[OK]
INSERT INTO clients VALUES ('Noah Patel', 500.00, FALSE);
[OK]
```

---

## 🔍 4. Querying Client Data

### View All Clients

```sql
SELECT * FROM clients;
[OK]
id  full_name     balance    active
1   Emma Taylor   10500.75   TRUE
2   James Lee     6300.00    TRUE
3   Olivia Chen   1200.25    FALSE
4   Noah Patel    500.00     FALSE
```

### Clients Other Than James

```sql
SELECT * FROM clients WHERE full_name != 'James Lee';
[OK]
id  full_name     balance    active
1   Emma Taylor   10500.75   TRUE
3   Olivia Chen   1200.25    FALSE
4   Noah Patel    500.00     FALSE
```

### Only Active Clients

```sql
SELECT * FROM clients WHERE active == TRUE;
[OK]
id  full_name   balance    active
1   Emma Taylor 10500.75   TRUE
2   James Lee   6300.00    TRUE
```

---

## 💳 5. Creating a Transactions Table

```sql
CREATE TABLE transactions (description, client_id);
[OK]

INSERT INTO transactions VALUES ('Wire Transfer', 3);
[OK]
INSERT INTO transactions VALUES ('Deposit', 1);
[OK]
INSERT INTO transactions VALUES ('Wire Transfer', 4);
[OK]
INSERT INTO transactions VALUES ('Bill Payment', 2);
[OK]

SELECT * FROM transactions;
[OK]
id  description    client_id
1   Wire Transfer  3
2   Deposit        1
3   Wire Transfer  4
4   Bill Payment   2
```

---

## 🔗 6. Joining Tables

### Match `transactions.client_id` with `clients.id`

```sql
JOIN transactions AND clients ON client_id AND id;
[OK]
id  transactions.description  clients.full_name  clients.balance  clients.active
1   Wire Transfer             Olivia Chen        1200.25          FALSE
2   Deposit                   Emma Taylor        10500.75         TRUE
3   Wire Transfer             Noah Patel         500.00           FALSE
4   Bill Payment              James Lee          6300.00          TRUE
```

---

## ✏️ 7. Updating Records

### Adjust Client Balance

```sql
UPDATE clients SET balance = 750.00 WHERE full_name == 'Noah Patel';
[OK]
```

```sql
SELECT * FROM clients WHERE full_name == 'Noah Patel';
[OK]
id  full_name   balance  active
4   Noah Patel  750.00   FALSE
```

---

## 🗑️ 8. Deleting Records

### Remove Inactive Client

```sql
DELETE FROM clients WHERE full_name == 'James Lee';
[OK]
```

```sql
SELECT * FROM clients;
[OK]
id  full_name     balance    active
1   Emma Taylor   10500.75   TRUE
3   Olivia Chen   1200.25    FALSE
4   Noah Patel    750.00     FALSE
```

---

## 🎯 9. Conditional Queries

### Inactive Clients with Balance > 700

```sql
SELECT * FROM clients WHERE (active == FALSE) AND (balance > 700);
[OK]
id  full_name    balance  active
3   Olivia Chen  1200.25  FALSE
4   Noah Patel   750.00   FALSE
```

### Clients with Names Containing 'a'

```sql
SELECT * FROM clients WHERE full_name LIKE 'a';
[OK]
id  full_name     balance    active
1   Emma Taylor   10500.75   TRUE
4   Noah Patel    750.00     FALSE
```

### IDs of Inactive Clients

```sql
SELECT id FROM clients WHERE active == FALSE;
[OK]
id
3
4
```

### Clients with Balance Over $10,000

```sql
SELECT full_name FROM clients WHERE balance > 10000;
[OK]
full_name
Emma Taylor
```

---

## ⚙️ 10. Altering the Table

### Add Account Type

```sql
ALTER TABLE clients ADD account_type;
[OK]

UPDATE clients SET account_type = 'Premium' WHERE full_name == 'Emma Taylor';
[OK]
```

```sql
SELECT * FROM clients;
[OK]
id  full_name     balance    active  account_type
1   Emma Taylor   10500.75   TRUE    Premium
3   Olivia Chen   1200.25    FALSE
4   Noah Patel    750.00     FALSE
```

### Remove Active Status

```sql
ALTER TABLE clients DROP active;
[OK]

SELECT * FROM clients;
[OK]
id  full_name     balance    account_type
1   Emma Taylor   10500.75   Premium
3   Olivia Chen   1200.25
4   Noah Patel    750.00
```

---

## ❌ 11. Error Handling Examples

### Missing Semicolon

```sql
SELECT * FROM clients
[ERROR]: Semicolon missing at end of line
```

### Table Does Not Exist

```sql
SELECT * FROM loans;
[ERROR]: Table does not exist
```

### Column Does Not Exist

```sql
SELECT credit_score FROM clients WHERE full_name == 'Olivia Chen';
[ERROR]: Attribute does not exist
```

---

## 🧹 12. Cleanup

### Remove Low-Balance Clients

```sql
DELETE FROM clients WHERE balance < 1000;
[OK]

SELECT * FROM clients;
[OK]
id  full_name     balance    account_type
1   Emma Taylor   10500.75   Premium
3   Olivia Chen   1200.25
```

```sql
DROP TABLE clients;
[OK]

DROP TABLE transactions;
[OK]

DROP DATABASE finance_tracker;
[OK]
```
