package Lab1;
import java.io.*;
import java.nio.ByteBuffer;
import java.text.*;
import java.util.*;
import java.net.*;
public class multiThreadServer {
    private static DatagramSocket udpSocket = null;
    private static ServerSocket tcpSocket = null;
    private static final int TIMEOUT = 1000;
    private static final String HOSTNAME = "localhost";
    public multiThreadServer(int port)throws IOException {
        ServerSocket serverSocket =new ServerSocket(port);//(12235 );
        System.out.println("server start");
        initializeUDPSocket(port);
        byte[] buffer2 = new byte[28];
        while(true){
            DatagramPacket response =null;
            try{

            //    System.out.print("try to accepet");

                response = new DatagramPacket(buffer2, buffer2.length);
                udpSocket.receive(response);
                ByteBuffer responseBuf =ByteBuffer.wrap(response.getData());
                stageA(responseBuf);

            }
            catch (Exception e){
                udpSocket.close();
               // e.printStackTrace();

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
    private void stageA(ByteBuffer receivedBuf){
        DatagramPacket send=null;
        int payload_len=receivedBuf.getInt(0);
        int clientPsecret=receivedBuf.getInt(4);
        short step=receivedBuf.getShort(8);
        short studentID=receivedBuf.getShort(10);
        String sendString = "hello world\0";
        byte[] dst=new byte[sendString.getBytes().length];
        receivedBuf.get( dst, 12, sendString.getBytes().length);
        if(!Arrays.equals(dst,sendString.getBytes())||receivedBuf.capacity()!=12+sendString.getBytes().length){
            closeUDPSocket();
        }

        Random rand = new Random();
        int num = rand.nextInt(1000);
        int len = rand.nextInt(1000);
        int udp_port = rand.nextInt(1000);
        int serverPsecretA = rand.nextInt(1000);
        ByteBuffer returnPacket = ByteBuffer.allocate(28);
        returnPacket.putInt(16); //payloadlen
        returnPacket.putInt(clientPsecret);   //psecret
        returnPacket.putShort((short)2); //step
        returnPacket.putShort(studentID); //studentID
        returnPacket.putInt(num);
        returnPacket.putInt(len);
        returnPacket.putInt(udp_port);
        returnPacket.putInt(serverPsecretA);
        DatagramPacket UDPPacket =new DatagramPacket(returnPacket.array(),returnPacket.array().length);
        try{

            udpSocket.send(UDPPacket);

            Thread thread =new ClientHandler(udpSocket);
            thread.start();
            System.out.println("Assigning new thread for this client");
        }catch (IOException e){
            System.err.println("can not send return val back");
        }


    }private static void closeUDPSocket() {
        udpSocket.close();
        System.out.println("close connection");
    }

}