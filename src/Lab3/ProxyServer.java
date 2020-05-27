package Lab3;

import Test.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {
    private ServerSocket tcpSocket =null;

    public static void main(String[] args)throws IOException{
        ProxyServer proxyServer = new ProxyServer(1234);

    }
    public ProxyServer(int proxy_port){
        //Listening to port 1234
        InitialTCPSocket(proxy_port);
        while(! isStopped()) {
            Socket proxySocket = null;
            try {
                proxySocket = tcpSocket.accept();
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

                    //Implement Handler
                    //Coming from origin server

                    //Coming from client
                    //1. Connect command
                    //2. Non connect Command


        }

    }


}
