package Lab1;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.net.*;

public class multiThreadServer {
    private static DatagramSocket udpSocket = null;
    private static ServerSocket tcpSocket = null;
    private static final int TIMEOUT = 1000;
    private static final String HOSTNAME = "localhost";
    DatagramSocket ds=null;
    public multiThreadServer(int port)throws IOException {
      //  ServerSocket serverSocket =new ServerSocket(port);//(12235 );
        System.out.println("server start");
        udpSocket =initializeUDPSocket(port,TIMEOUT);
        byte[] buffer2 = new byte[256];
        Listner listner = new Listner();
        Thread thread = new Thread(listner);
        thread.start();

    }


    private  DatagramSocket initializeUDPSocket(int port,int Time) {
        DatagramSocket tmpSocket=null;
        try {
            //connect to the server
            tmpSocket = new DatagramSocket(port);
            tmpSocket.setSoTimeout(Time); // set timeout on the connection - 10 seconds
            System.out.println("create a new udpsocket and listen to port "+port );
        } catch (IOException ex){
            System.err.println("Could not connect to " + HOSTNAME);
            System.err.println(ex);
        }
        return tmpSocket;
    }
    private void stageA(DatagramPacket response){
        System.out.println("start Stage A");
        ByteBuffer responseBuf =ByteBuffer.wrap(response.getData());
   //     DatagramPacket send=null;

        int payload_len=responseBuf.getInt(0);
        int clientPsecret=responseBuf.getInt(4);
        short step=responseBuf.getShort(8);
        short studentID=responseBuf.getShort(10);
        String sendString = "hello world\0";
        byte[] dst=new byte[sendString.getBytes().length];
        responseBuf.get(dst);responseBuf.get(dst); //TODO need to find out how to slice ... right now just temp solution

        if(!Arrays.equals(dst,sendString.getBytes())){
            closeUDPSocket();
            System.out.println("not acceptable for stage A");
            return;
        }

        Random rand = new Random();
        int num = rand.nextInt(25);
        int len = rand.nextInt(50);
        int udp_port = rand.nextInt(1000)+1024;//can't generate port below 1024: permission denied
        int serverPsecretA = rand.nextInt(1000);
        ByteBuffer returnPacket = ByteBuffer.allocate(28);
        header head =new header(16,clientPsecret,2,studentID);
        ByteBuffer headerBuffer =head.byteBuffer;
        returnPacket.put(headerBuffer.array());
        returnPacket.putInt(num);
        returnPacket.putInt(len);
        returnPacket.putInt(udp_port);
        returnPacket.putInt(serverPsecretA);
        DatagramPacket UDPPacket =new DatagramPacket(returnPacket.array(),returnPacket.array().length,response.getAddress(),response.getPort());

        try{

            udpSocket.send(UDPPacket);
            ds = initializeUDPSocket(udp_port,3000);
            System.out.println("port after re-initialized :"+ds.getLocalPort());

            Thread thread =new ClientHandler(ds,serverPsecretA,num);
            thread.start();
            System.out.println("Assigning new thread for this client");
        }catch (IOException e){
            System.err.println("can not send return val back");
            e.printStackTrace();
        }


    }private static void closeUDPSocket() {
        udpSocket.close();
        System.out.println("close connection");
    }
    class Listner implements Runnable {
        @Override
        public void run() {
            String text = null;
            while (true) {
                text = null;
                int server_port = 2425;
                byte[] message = new byte[1500];
                DatagramPacket p = new DatagramPacket(message, message.length);
                DatagramSocket s = null;
                try {
                    s = new DatagramSocket(server_port);
                } catch (SocketException e) {
                    e.printStackTrace();
                    System.out.println("Socket excep");
                }
                try {
                    s.receive(p);
                    System.out.println("get the data" + Arrays.toString((p.getData())));
                    ByteBuffer responseBuf = ByteBuffer.wrap(p.getData());
                    DatagramPacket send = null;

                    int payload_len = responseBuf.getInt(0);
                    int clientPsecret = responseBuf.getInt(4);
                    short step = responseBuf.getShort(8);
                    short studentID = responseBuf.getShort(10);

                    if (clientPsecret == 0 && step == 1) stageA(p);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("IO EXcept");
                }
                text = new String(message, 0, p.getLength());
                System.out.println("message = " + text);
                s.close();
            }

        }
    }
}
