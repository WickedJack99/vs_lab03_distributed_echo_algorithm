#author ChatGPT
#!/bin/bash

# Define the range of ports you want to check and kill processes on
START_PORT=5000
END_PORT=5010

# Loop through each port in the range
for (( port=$START_PORT; port<=$END_PORT; port++ ))
do
    # Check if there is a Java process running on the current port
    sudo lsof -i :$port | grep java

    # If a Java process is found, extract the PID and kill the process
    if [ $? -eq 0 ]; then
        PID=$(sudo lsof -t -i :$port -s TCP:LISTEN)
        echo "Killing Java process on port $port with PID $PID"
        sudo kill $PID
    else
        echo "No Java process found on port $port"
    fi
done
