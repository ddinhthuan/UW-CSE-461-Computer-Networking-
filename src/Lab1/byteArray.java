package Lab1;

import java.util.Arrays;
public class byteArray {


        public static void main(String[] args) {
            String str = "PANKAJ";
            byte[] byteArr = str.getBytes();
            // print the byte[] elements
            System.out.println("String to byte array: " + Arrays.toString(byteArr));
        }
}
