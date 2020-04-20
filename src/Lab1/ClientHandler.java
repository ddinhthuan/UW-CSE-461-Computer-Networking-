package Lab1;


import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
public class ClientHandler extends Thread{
    //copy from lab1
    private static DatagramSocket udpSocket = null;
    private static ServerSocket tcpSocket = null;

    private  int psecretA =0;
    private  int psecretB =Integer.MAX_VALUE;
    private  int psecretC =Integer.MAX_VALUE;
    private int studentID;
    private int tcp_port;
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
                //TODO Check 4 alginment
                if(step!=1){
                    if(udpSocket!=null&& udpSocket.isConnected())closeUDPSocket();
                    if(tcpSocket!=null&& !tcpSocket.isClosed())closeTCPSocket();
                    break;
                }
                if(clientPsecret==psecretA ) {
                    stageB();
                }else if(clientPsecret==psecretB ){
                    stageC(psecretB);
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
    private void stageC(int clientPsecret){
        ByteBuffer resp=null;


        try{

            initializeTCPSocket(tcp_port);
            Socket clientSocket = tcpSocket.accept();


            Random rand = new Random();
            int num2 = rand.nextInt(100);
            int len2 = rand.nextInt(100);
            psecretC = rand.nextInt(1000);

            //Assign to return val
            ByteBuffer responsePacket =ByteBuffer.allocate(12+14);
            header head =new header(len2,clientPsecret, 2,studentID);
            responsePacket.put(head.byteBuffer.array());
            responsePacket.putInt(num2);

            responsePacket.putInt(len2);
            responsePacket.putInt(psecretC);
            responsePacket.putChar('c');
            PrintWriter pr =new PrintWriter(clientSocket.getOutputStream());
            //https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html
            pr.println(Arrays.toString(responsePacket.array()));
            pr.flush();
            System.out.println("sent to client with "+Arrays.toString(responsePacket.array()));
        }catch (IOException e){
            System.err.println("connection failed");
        }


    }
    private void stageD(){

    }


    private static void closeUDPSocket() {
        udpSocket.close();
    }

    private static void initializeTCPSocket(int tcp_port) throws IOException {
        try {
            tcpSocket = new ServerSocket(tcp_port);

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
