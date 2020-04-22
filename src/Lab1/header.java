package Lab1;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.nio.*;
//From piazza
//So client send psecret 0 on the header with "hello world" on the payload
// then server send back psecret 0 on the header and secretA on the payload
// (random number generated, lets say secretA = 200), then client send back psecret = 200
// on its header?

public class header{
    public ByteBuffer byteBuffer=ByteBuffer.allocate(12);
    public header(int payload_len,int psecret,int step, int studentID){
        byteBuffer.putInt(payload_len);
        byteBuffer.putInt(psecret);
        ByteBuffer tmp0=ByteBuffer.allocate(8);
        tmp0.putInt(step);
        ByteBuffer tmp1=ByteBuffer.allocate(8);
        tmp1.putInt(studentID);
      //  System.out.println("step "+Arrays.toString(tmp0.array())+" student ID "+ Arrays.toString(tmp1.array()) );
        byteBuffer.putShort((short) step);
        byteBuffer.putShort((short)studentID);
        System.out.println("Header "+Arrays.toString(byteBuffer.array()));
    }
}