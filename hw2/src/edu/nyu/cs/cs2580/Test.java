package edu.nyu.cs.cs2580;

import java.util.Vector;

public class Test {
  private static byte[] encode(short[] input){
    byte[] output = new byte[input.length*2];
    for(int i=0; i<input.length; ++i){
      output[i*2+1] = (byte)(input[i] & 0xff);
      output[i*2] = (byte)((input[i] >> 8) & 0xff);
    }
    return output;
  }

  private static short[] decode(byte[] input){
    short[] output = new short[input.length];
    for(int i=0; i<input.length; i+=2){
          output[i] = (short) (input[i]<<8 | input[i+1]);
    }
    return output;
  }

  public static void main(String[] args){
    byte[] a = new byte[2];
    a[0] = 2;
    a[1] = 3;
    System.out.println(Test.decode(a)[0]);
    short[] b = new short[1];
    b[0]= 515;
    System.out.println(Test.encode(b)[0]);
  }
  
  private static void decodeList(byte[] list, Vector<Short> postingsList,
      Vector<Short> docLists, Vector<Short> docTermFrequency){
    //parsing
      short[] sList = Test.decode(list);
      for(int i=0; i<sList.length;){
        //Doc id
        if(i == 0 )
          docLists.add(sList[i++]);
        else 
          docLists.add((short) (docLists.get(docLists.size()-1) + sList[i++]));
        //frequency
        short len = sList[i++];
        docTermFrequency.add(len);
        //posting list
        for(int j=0; j<len; ++j){
          if(j==0)
            postingsList.add(sList[i++]);
          else
            postingsList.add((short) (postingsList.get(postingsList.size()-1) + sList[i++]));
        }
      }
      return;
  }
}
