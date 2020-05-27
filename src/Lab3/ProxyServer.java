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
        Listener listener =new Listener();
        Thread thread = new Thread(listener);
        thread.start();

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
        @Override
        public void run() {

            try{

                Socket proxySocket = tcpSocket.accept();
                //Parse first line

                System.out.println("client connected: " + proxySocket.isConnected());
                //Coming from origin server
                //Coming from client
                //1. Connect command
                //2. Non connect Command
            }catch(IOException e){

            }






        }

    }

}
