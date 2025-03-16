#!/bin/bash

# Start server
echo "Starting serve..."
java -cp bin DBServer

# Start client
echo "Starting client..."
java -cp bin DBClient
