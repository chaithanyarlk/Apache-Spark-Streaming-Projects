import requests
import json
import time

def post_data():
    # Define the data you want to send (replace this with your actual data)
    data_to_send = {"key1": "value1", "key2": "value2"}

    # Set the URL for the localhost server
    url = "http://localhost:5000"

    # Convert data to JSON format
    json_data = json.dumps(data_to_send)

    # Set headers (optional)
    headers = {"Content-Type": "application/json"}

    # Send POST request
    response = requests.post(url, data=json_data, headers=headers)

    # Print the response from the server
    print("Response:", response.text)

if __name__ == "__main__":
    # Run the script indefinitely with a 20-second interval
    while True:
        post_data()
        time.sleep(20)
