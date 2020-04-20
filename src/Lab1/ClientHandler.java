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

//    final  socket;
    final int psecretA =0;
    final int psecretB =333;
    final int psecretC =666;
    final int psecretD =444;
    public ClientHandler(DatagramSocket udpSocket) {

        this.udpSocket =udpSocket;


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
                    case psecretB: stageB();
                    default: closeUDPSocket();

                }


                // write on output stream based on the
                // answer from the client

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



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
