package Lab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class MyTimer extends TimerTask {
    int name ;
    DatagramPacket request;
    DatagramSocket socket;
    HashMap<Integer,Timer> timerMap;

    MyTimer(int name, DatagramPacket request, DatagramSocket socket, HashMap<Integer,Timer> timerMap){
        this.name =name;
        this.request =request;
        this.socket =socket;
        this.timerMap =timerMap;
    }
    @Override
    public void run() {
        if(!timerMap.containsKey(name)){
            try{
                System.out.print("Reset pack id"+name);
                socket.send(request);
            }catch (IOException ex) {

                System.err.println(ex);
            } finally {
                if (socket != null)
                    socket.close();
            }

        }
    }
    public static void main(String[] args){

    }

//    private void completeTask() {
//        try {
//            //assuming it takes 20 secs to complete the task
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void main(String args[]){
//        TimerTask timerTask = new MyTimer();
//        //running timer task as daemon thread
//        Timer timer = new Timer(true);
//        timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
//        System.out.println("TimerTask started");
//        //cancel after sometime
//        try {
//            Thread.sleep(120000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("TimerTask cancelled");
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}