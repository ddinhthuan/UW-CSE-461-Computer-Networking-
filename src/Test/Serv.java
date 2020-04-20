package Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;


public class Serv {

/**
 * @param args
 * @throws IOException 
 */
public static void main(String[] args) throws IOException {

        Listner listner = new Listner();
        Thread thread = new Thread(listner);
        thread.start();


        String messageStr = "Hello msg1";
        int server_port = 2425;
        DatagramSocket s = new DatagramSocket();
        InetAddress local = InetAddress.getByName("localhost");
        int msg_length = messageStr.length();
        byte[] message = messageStr.getBytes();
        DatagramPacket p = new DatagramPacket(message, msg_length, local,
                server_port);
        System.out.println("about to send msg1");
        s.send(p);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        messageStr = "Hello msg2";
        msg_length = messageStr.length();
        message = messageStr.getBytes();
        p = new DatagramPacket(message, msg_length, local,
                server_port);
        System.out.println("about to send msg2");
        s.send(p);
}


}
   class Listner implements Runnable
    { 
            @Override
            public void run() {
        String text = null;
        while(true){
            text = null;    
        int server_port = 2425;
        byte[] message = new byte[1500];
        DatagramPacket p = new DatagramPacket(message, message.length);
        DatagramSocket s = null;
        try{
           s = new DatagramSocket(server_port);
        }catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Socket excep");
        }
        try {
        s.receive(p);
        System.out.println("get the data"+ Arrays.toString((p.getData())));
       }catch (IOException e) {
            e.printStackTrace();
                System.out.println("IO EXcept");
            }
        text = new String(message, 0, p.getLength());
        System.out.println("message = "+text);
        s.close();

    }
}

}