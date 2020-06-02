package Lab3;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Forward extends Thread {

    Socket sender=null;
    Socket receiver=null;

   public Forward(Socket sender,Socket receiver){
       this.sender = sender;
       this.receiver =receiver;
   }


    @Override
    public void run() {
       try {
           DataInputStream fromSender = new DataInputStream(new BufferedInputStream(sender.getInputStream()));
           DataOutputStream outToReceiver = new DataOutputStream(receiver.getOutputStream());
           //https://www.tutorialspoint.com/importance-of-transferto-method-of-inputstream-in-java-9
           fromSender.transferTo(outToReceiver);
        }catch (IOException e){
           e.printStackTrace();
       }
    }
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b) + " ");
        return sb.toString();
    }
}
