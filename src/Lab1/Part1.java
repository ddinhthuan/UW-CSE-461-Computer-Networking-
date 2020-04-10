package Lab1;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Arrays;
import Lab1.header;
public class Part1{
//    public void stageA(){
//
//    }
//    public void stageB(){
//
//    }
//    public void stageC(){
//
//    }
//    public void stageD(){
//
//    }


    public static void main(String[] args)throws IOException{
        header head =new header(10,200,1,023);
        System.out.println("header : "+Arrays.toString(head.getHeader()));


    }
}