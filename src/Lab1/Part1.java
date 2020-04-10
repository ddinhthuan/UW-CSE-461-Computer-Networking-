package Lab1;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Arrays;
import Lab1.header;
public class Part1{
    public static void main(String[] args)throws IOException{
        header head =new header(10,'C',"1",23);
        System.out.println("header : "+Arrays.toString(head.getHeader()));
    }
}