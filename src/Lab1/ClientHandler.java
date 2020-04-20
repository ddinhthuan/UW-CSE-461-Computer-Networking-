package Lab1;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
public class ClientHandler extends Thread{
    //copy from lab1
    private static DatagramSocket udpSocket = null;
    private static Socket tcpSocket = null;
    private static InputStream in = null;
    private static OutputStream out = null;

    private static final String HOSTNAME = "attu2.cs.washington.edu";
    private static final int TIMEOUT = 1000;
    boolean stageADone=false;

//    final  socket;
    final int psecretA =0;
    final int psecretB =333;
    final int psecretC =666;
    final int psecretD =444;
    public ClientHandler(DatagramSocket udpSocket, InputStream in, OutputStream out) {

        this.udpSocket =udpSocket;
        this.in = in;
        this.out =out;

    }
    @Override
    public void run() {
        byte[] received=new byte[10000];//need to double check here
        String toreturn;
        DatagramPacket DpReceive = null;
        while (true) {
            try {

                // Ask user what he wants

                // receive the answer from client
                DpReceive = new DatagramPacket(received, received.length);// not sure how large I should assign here
                readAllBytes rb = new readAllBytes();
                received = rb.readAllBytes_fn(in);
                ByteBuffer receivedBuf =ByteBuffer.wrap(received);
                //int payload_len=receivedBuf.getInt(0);
                int psecret=receivedBuf.getInt(4);
                //int step=receivedBuf.getShort(8);
                //int studentID=receivedBuf.getShort(10);



                switch (psecret){
                    case psecretA: stageA(receivedBuf);
                    case psecretB: stageB();

                    default: closeUDPSocket();

                }
                if(stageADone){
                    closeUDPSocket();
                    break;
                }

                // write on output stream based on the
                // answer from the client

            } catch (IOException e) {
                e.printStackTrace();
            }
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
            }catch (IOException e){
                System.err.println("can not send return val back");
            }
            stageADone =true;


    }
    private void stageB(){

    }
    private void stageC(){

    }
    private void stageD(){

    }


    private static void closeUDPSocket() {
        udpSocket.close();
    }

    private static void initializeTCPSocket(int tcp_port) throws IOException {
        try {
            tcpSocket = new Socket();
            InetSocketAddress address = new InetSocketAddress(HOSTNAME, tcp_port);
            tcpSocket.connect(address, TIMEOUT);

        } catch (IOException e){
            System.err.println("Could not connect");
            System.err.println(e);
        }
    }

    private static void closeTCPSocket() throws IOException {
        in.close();
        out.close();
        tcpSocket.close();
    }
}
