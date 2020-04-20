package Test;


import java.io.*;
import java.net.*;
import java.lang.*;
public class Server {
    public static void main(String[] args) throws IOException {
        final int port = 2999;
        System.out.println("Server waiting for connection on port "+port);
        ServerSocket ss = new ServerSocket(port);
        Socket clientSocket = ss.accept();
        System.out.println("Recieved connection from "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
        //create two threads to send and recieve from client
        RecieveFromClientThread recieve = new RecieveFromClientThread(clientSocket);
        Thread thread = new Thread(recieve);
        thread.start();
        SendToClientThread send = new SendToClientThread(clientSocket);
        Thread thread2 = new Thread(send);
        thread2.start();
    }}
class RecieveFromClientThread implements Runnable
{
    Socket clientSocket=null;
    BufferedReader brBufferedReader = null;

    public RecieveFromClientThread(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }//end constructor
    public void run() {
        try{
            brBufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            String messageString;
            while(true){
                while((messageString = brBufferedReader.readLine())!= null){//assign message from client to messageString
                    if(messageString.equals("EXIT"))
                    {
                        break;//break to close socket if EXIT
                    }
                    System.out.println("From Client: " + messageString);//print the message from client
                    System.out.println("Please enter something to send back to client..");
                }
                this.clientSocket.close();
                System.exit(0);
            }

        }
        catch(Exception ex){System.out.println(ex.getMessage());}
    }
}//end class RecieveFromClientThread
class SendToClientThread implements Runnable
{
    PrintWriter pwPrintWriter;
    Socket clientSock = null;

    public SendToClientThread(Socket clientSock)
    {
        this.clientSock = clientSock;
    }
    public void run() {
        try{
            pwPrintWriter =new PrintWriter(new OutputStreamWriter(this.clientSock.getOutputStream()));//get outputstream

            while(true)
            {
                String msgToClientString = null;
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));//get userinput

                msgToClientString = input.readLine();//get message to send to client

                pwPrintWriter.println(msgToClientString);//send message to client with PrintWriter
                pwPrintWriter.flush();//flush the PrintWriter
                System.out.println("Please enter something to send back to client..");
            }//end while
        }
        catch(Exception ex){System.out.println(ex.getMessage());}
    }//end run
}//end class SendToClientThread

class RecieveThread implements Runnable
{
    Socket sock=null;
    BufferedReader recieve=null;

    public RecieveThread(Socket sock) {
        this.sock = sock;
    }//end constructor
    public void run() {
        try{
            recieve = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));//get inputstream
            String msgRecieved = null;
            while((msgRecieved = recieve.readLine())!= null)
            {
                System.out.println("From Server: " + msgRecieved);
                System.out.println("Please enter something to send to server..");
            }
        }catch(Exception e){System.out.println(e.getMessage());}
    }//end run
}//end class recievethread
class SendThread implements Runnable
{
    Socket sock=null;
    PrintWriter print=null;
    BufferedReader brinput=null;

    public SendThread(Socket sock)
    {
        this.sock = sock;
    }//end constructor
    public void run(){
        try{
            if(sock.isConnected())
            {
                System.out.println("Client connected to "+sock.getInetAddress() + " on port "+sock.getPort());
                this.print = new PrintWriter(sock.getOutputStream(), true);
                while(true){
                    System.out.println("Type your message to send to server..type 'EXIT' to exit");
                    brinput = new BufferedReader(new InputStreamReader(System.in));
                    String msgtoServerString=null;
                    msgtoServerString = brinput.readLine();
                    this.print.println(msgtoServerString);
                    this.print.flush();

                    if(msgtoServerString.equals("EXIT"))
                        break;
                }//end while
                sock.close();}}catch(Exception e){System.out.println(e.getMessage());}
    }//end run method
}//end class