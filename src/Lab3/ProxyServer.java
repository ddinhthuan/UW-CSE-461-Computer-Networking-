package Lab3;

import Test.Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ProxyServer {
    private ServerSocket tcpSocket =null;

    public static void main(String[] args){
        //TODO uncomment when ready
        /*
        if(args.length != 2){
            throw new IllegalArgumentException("insufficient arguments");
        }
        String program = args[0];
        int port = Integer.parseInt(args[1]);
        if(port > 65535 || 1024 > port){ // todo valid range from 0 - 65535?
            throw new IllegalArgumentException("Invalid port number");
        }

        ProxyServer proxyServer = new ProxyServer(port);
        */
        ProxyServer proxyServer = new ProxyServer(1234);

    }
    public ProxyServer(int proxy_port){
        //Listening to port 1234
        InitialTCPSocket(proxy_port);
        while(! isStopped()) {
            Socket proxySocket = null;
            try {
                proxySocket = tcpSocket.accept(); //connect to localhost
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            Listener listener =new Listener(proxySocket);
            Thread thread = new Thread(listener);
            thread.start();
        }
    }
    private Boolean isStopped(){
        return false;
    }

    private void InitialTCPSocket(int proxy_port){
        try {
            tcpSocket = new ServerSocket(proxy_port);
            System.out.println("Server started");
            System.out.println("Waiting for a client...");

        } catch (IOException e){
            System.err.println("Could not connect");
            System.err.println(e);
        }
    }
    class Listener implements Runnable {
        Socket proxySocket=null;
        Listener(Socket proxySocket){
            this.proxySocket = proxySocket;
        }
        @Override
        public void run() {
                    //Parse first line
            System.out.println("client connected: " + proxySocket.isConnected());
            System.out.println("The client is: " + proxySocket.getLocalAddress());
                //Implement Handler
                    //Coming from origin server - header from user request
            // e.g. GET hostname - parse out hostname then send to host
            //modify keep alive flag - set to false if GET request
            //e.g. CONNECT request --> set up back and forth request

            //Client sends request to web server
            //Request asks server to retrieve resource - identified by URI
            Socket client = null;
            Socket server = null;
            try{
                byte[] request_bytes = new byte[1024];
                byte[] reply_bytes = new byte[4096];

                InputStream inFromClient = proxySocket.getInputStream();
                OutputStream outToClient = proxySocket.getOutputStream();

                //connect socket to server
                try{
                    server = new Socket(proxySocket.getInetAddress(), 1234); //todo fix hardcoded
                } catch (IOException e){
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                    out.flush();
                    throw new RuntimeException(e);
                }
                InputStream inFromServer = server.getInputStream();
                OutputStream outToServer = server.getOutputStream();

                int bytes_read;
                while((bytes_read = inFromClient.read(request_bytes)) != -1){
                  //  System.out.println("Made it here");
                    outToServer.write(request_bytes, 0, bytes_read);
                    String request = new String(request_bytes, StandardCharsets.UTF_8);
                    System.out.println("to server-->" + request);
                    outToServer.flush();
                }


//Server processes request - sends response
            } catch (IOException e) {
                e.printStackTrace();
            }


            //Coming from client
                    //1. Connect command
                    //2. Non connect Command


        }

    }


}
