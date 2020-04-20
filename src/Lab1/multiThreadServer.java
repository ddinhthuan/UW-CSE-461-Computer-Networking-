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
        udpSocket =initializeUDPSocket(port);
        byte[] buffer2 = new byte[256];

        String text = null;
        while(true) {
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
                ByteBuffer responseBuf =ByteBuffer.wrap(p.getData());
                DatagramPacket send=null;

                int payload_len=responseBuf.getInt(0);
                int clientPsecret=responseBuf.getInt(4);
                short step=responseBuf.getShort(8);
                short studentID=responseBuf.getShort(10);
                if(clientPsecret==0 && step==1) stageA(p);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO EXcept");
            }
            text = new String(message, 0, p.getLength());
            System.out.println("message = " + text);
            s.close();
        }

    }


    private  DatagramSocket initializeUDPSocket(int port) {
        DatagramSocket tmpSocket=null;
        try {
            //connect to the server
            tmpSocket = new DatagramSocket(port);
            tmpSocket.setSoTimeout(TIMEOUT); // set timeout on the connection - 10 seconds
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
        DatagramPacket send=null;

        int payload_len=responseBuf.getInt(0);
        int clientPsecret=responseBuf.getInt(4);
        short step=responseBuf.getShort(8);
        short studentID=responseBuf.getShort(10);
        String sendString = "hello world\0";
        byte[] dst=new byte[sendString.getBytes().length];
        int off =12;int len0 =sendString.getBytes().length;int size =dst.length;
        responseBuf.get(dst);responseBuf.get(dst); //TODO need to find out how to slice ... right now just temp solution
        System.out.println("Dst "+Arrays.toString(dst));
        System.out.println("buf "+Arrays.toString( sendString.getBytes()));
        System.out.println("cap "+responseBuf.capacity()+" send "+sendString.getBytes().length+12);

        if(!Arrays.equals(dst,sendString.getBytes())){
            closeUDPSocket();
            System.out.println("not acceptable for stage A");
            return;
        }

        Random rand = new Random();
        int num = rand.nextInt(1000);
        int len = rand.nextInt(1000);
        int udp_port = rand.nextInt(1000)+1024;//can't generate port below 1024: permission denied
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
        DatagramPacket UDPPacket =new DatagramPacket(returnPacket.array(),returnPacket.array().length,response.getAddress(),response.getPort());

        try{

            udpSocket.send(UDPPacket);
            DatagramSocket ds = initializeUDPSocket(udp_port);
            System.out.println("port after re intialized :"+ds.getPort());

            Thread thread =new ClientHandler(ds,serverPsecretA);
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

}