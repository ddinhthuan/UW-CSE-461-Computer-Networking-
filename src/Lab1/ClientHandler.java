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


//    final  socket;
      int psecretA =0;
      int psecretB =Integer.MAX_VALUE;
      int psecretC =Integer.MAX_VALUE;
    private static final int TIMEOUT = 1000;
    private static final String HOSTNAME = "localhost";
    private static InputStream in =null;
    private static OutputStream out =null;


    public ClientHandler(DatagramSocket udpSocket,int psecretA) {

        this.udpSocket =udpSocket;
        this.psecretA  =psecretA;


    }
    @Override
    public void run() {
        byte[] received=new byte[10000];//need to double check here
        DatagramPacket receivedPacket = null;
        while (true) {
            try {

                // Ask user what he wants

                // receive the answer from client
                receivedPacket = new DatagramPacket(received, received.length);// not sure how large I should assign here

                udpSocket.receive(receivedPacket);
                ByteBuffer receivedBuf =ByteBuffer.wrap(receivedPacket.getData());
                System.out.println(Arrays.toString(receivedBuf.array()));
                int payload_len=receivedBuf.getInt(0);
                int clientPsecret=receivedBuf.getInt(4);
                short step=receivedBuf.getShort(8);
                short studentID=receivedBuf.getShort(10);
                if(step!=1){
                    if(udpSocket!=null&& udpSocket.isConnected())closeUDPSocket();
                    if(tcpSocket!=null&& tcpSocket.isConnected())closeTCPSocket();
                    break;
                }
                if(clientPsecret==psecretA ) {
                    stageB();
                }else if(clientPsecret==psecretB ){
                    stageC();
                }else if(clientPsecret==psecretC){
                    stageD();
                }




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
