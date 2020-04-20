package Lab1;

import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client class
public class testClient
{
    public static void main(String[] args) throws IOException
    {
        try
        {
            Scanner scn = new Scanner(System.in);

            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 5056
            System.out.println("try to connect to port");
            Socket s = new Socket(ip, 4999);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true)
            {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);

                // If client sends exit,close this connection
                // and then break from the while loop
                if(tosend.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }

                // printing date or time as requested by client
                //String received = dis.readUTF();
                //System.out.println(received);
                InputStreamReader in =new InputStreamReader(s.getInputStream());
                //BufferReader def : https://www.geeksforgeeks.org/java-io-bufferedreader-class-java/
                BufferedReader bf =new BufferedReader(in);
                String str =bf.readLine();
                System.out.println("server : "+str);
            }

            // closing resources
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}