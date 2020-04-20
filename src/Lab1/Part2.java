package Lab1;

import java.io.IOException;
import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
/*
The server should start listening on port 12235 (Do not run your server on attu2 on the same port as our server is running here).
The server should handle multiple clients at a time. This can be done using threading. Spawn a seperate thread for every new client and kill the thread when the client finishes.
The server should verify the header of every packet received and close any open sockets to the client and/or fail to respond to the client if:
unexpected number of buffers have been received
unexpected payload, or length of packet or length of packet payload has been received
the server does not receive any packets from the client for 3 seconds
the server does not receive the correct secret
The Server should respond to the client in four stages.In each stage, the server should randomly generate a secret to be sent to the client.
 */
public class Part2 {

    public static void main(String[] args) throws IOException{
       multiThreadServer mtSever =new multiThreadServer(12235);
    }
}
