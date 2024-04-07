$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Any, 9990)
$listener.Start()

Write-Host "Listening for connections on port 9999..."

while ($true) {
    $client = $listener.AcceptTcpClient()
    $stream = $client.GetStream()
    $reader = [System.IO.StreamReader]::new($stream)

    while ($stream.DataAvailable) {
        $data = $reader.ReadLine()
        Write-Host "Received: $data"
    }

    $client.Close()
}

# Note: To stop the script, you can terminate the PowerShell session or press Ctrl+C.
