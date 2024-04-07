import socket
import time

# Define the host and port
host = "localhost"
port = 9999

# Create a socket object
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect to the socket
s.connect((host, port))

# Send data to the socket
def send_data(message):
    s.sendall(message.encode("utf-8"))

# Send some messages to the socket every 2 seconds
try:
    for i in range(5):
        message = f"Message {i+1}\n"
        send_data(message)
        time.sleep(2)
finally:
    # Close the socket connection
    s.close()
