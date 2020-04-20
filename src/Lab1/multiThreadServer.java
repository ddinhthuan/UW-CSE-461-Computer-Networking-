package Lab1;
import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
public class multiThreadServer {
    private static DatagramSocket udpSocket = null;
    private static ServerSocket tcpSocket = null;
    private static final int TIMEOUT = 1000;
    private static final String HOSTNAME = "attu2.cs.washington.edu";
    public multiThreadServer(int port)throws IOException {
        ServerSocket serverSocket =new ServerSocket(port);//(12235 );
        System.out.println("server start");
        initializeUDPSocket(port);
        while(true){

            Socket socket =null;
            try{
                System.out.print("try to accepet");
                socket= serverSocket.accept();
                System.out.println("A new client is connected :"+socket.toString());

                DataInputStream dataInputStream =new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream =new DataOutputStream(socket.getOutputStream());
                System.out.println("Assigning new thread for this client");
                Thread thread =new ClientHandler(udpSocket,dataInputStream,dataOutputStream);
                thread.start();
            }
            catch (Exception e){
                udpSocket.close();
                e.printStackTrace();

            }
        }


    }
    private  void initializeUDPSocket(int port) {
        try {
            //connect to the server
            udpSocket = new DatagramSocket(port);
            udpSocket.setSoTimeout(TIMEOUT); // set timeout on the connection - 10 seconds

        } catch (IOException ex){
            System.err.println("Could not connect to " + HOSTNAME);
            System.err.println(ex);
        }
    }
}