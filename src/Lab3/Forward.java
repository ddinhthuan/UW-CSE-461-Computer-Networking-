package Lab3;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Forward extends Thread {

    Socket server=null;
    HttpHeader request=null;

   public Forward(Socket server, HttpHeader request){
       this.server = server;
       this.request =request;
   }


    @Override
    public void run() {
       try {
           OutputStream outFromProxy = server.getOutputStream();
           outFromProxy.write(request.getRequest().getBytes());
           outFromProxy.flush();

            } catch (IOException e) {

            }
        try {
           server.close();
        }catch (IOException ie){

        }
    }
}
