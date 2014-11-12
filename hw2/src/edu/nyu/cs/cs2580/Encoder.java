package edu.nyu.cs.cs2580;

import java.util.Vector;

public class Encoder {

  /*
   * encode: +-short -> +int -> delat(int) -> vbyte
   * decode: vbyte -> delta(int) -> +int -> +-short
   */
  
  
  public static byte[] encode(int[] input){
    // v-byte compression
    Vector<Byte> output = new Vector<Byte>();
    for(int i: input){
      while(i >= 128){
        output.add((byte) (i & 0x7F));
        i>>>=7;
      }
      output.add((byte) (i|0x80));
    }
    //convert to primary array
    byte[] outputArray = new byte[output.size()];
    for(int i=0; i<output.size(); ++i) outputArray[i] = output.get(i);
    return outputArray;
  }

  /*
   * encode: +-short -> +int -> delat -> vbyte
   * decode: vbyte -> delta(int) -> +int -> +-short
   */
  public static int[] decode(byte[] input){
  //1. de - vbyte compression
    Vector<Integer> deCompressionResult= new Vector<Integer>();
    for(int i=0; i<input.length; i++){
      int position = 0;
      int result = ((int)input[i] & 0x7F);
      while((input[i] & 0x80) == 0){
        i++;
        position++;
        int unsignedByte = ((int) input[i] & 0x7F);
        result |= (unsignedByte <<(7*position)); 
      }
      deCompressionResult.add((result));
    }
    //2. de delta compression
      Vector<Integer> outputList = new Vector<Integer>();
      int preDocId = 0;
      for(int i=0; i<deCompressionResult.size();){
        //Doc id
        int docidOffset = deCompressionResult.get(i++);
        int currentDocid = docidOffset + preDocId;
        outputList.add(currentDocid);
        preDocId = currentDocid;
        
        //frequency
        int len = deCompressionResult.get(i++);
        outputList.add(len);
        //posting list
        int prePosition = (int)Short.MIN_VALUE;
        for(int j=0; j<len; ++j){
          int positionOffset = deCompressionResult.get(i++);
          int currentPosition = prePosition + positionOffset;
          prePosition = currentPosition;
          outputList.add(currentPosition);
        }
      }
    //3. convert to short array and plus byte.min
    int[] outputOffset = new int[deCompressionResult.size()];
    for(int i=0; i<outputList.size(); ++i) 
      outputOffset[i] = outputList.get(i);
    return outputOffset;
  }
  public static int[] decode(byte[] input, Vector<Integer> postingsList,
      Vector<Integer> docLists, Vector<Integer> docTermFrequency){
    int[] outputOffset = decode(input);
    //3* put outputList into docIdList, freqList, and postingList
    for(int i=0; i<outputOffset.length;){
      docLists.add(outputOffset[i++]);
      int len = outputOffset[i++];
      docTermFrequency.add(len);
      for(int j=0; j< len; ++j){
        postingsList.add(outputOffset[i++]);
      }
    }
    return outputOffset;
  }

  public static void main(String[] args){
    int[] b = new int[7];
    //1 2 1 3 2 1 1
    b[0]= 1+Short.MIN_VALUE;
    b[1]= 2+Short.MIN_VALUE;
    b[2]= 1+Short.MIN_VALUE;
    b[3]= 3+Short.MIN_VALUE;
    b[4]= 2+Short.MIN_VALUE;
    b[5]= 1+Short.MIN_VALUE;
    b[6]= 1+Short.MIN_VALUE;
    byte[] output = encode(b);
    
    Vector<Integer> postingsList = new Vector<Integer>();
    Vector<Integer> docLists = new Vector<Integer>();
    Vector<Integer> docTermFrequency = new Vector<Integer>();
    int[] input = decode(output, postingsList, docLists, docTermFrequency);
    for(int i:input) System.out.println(i-Short.MIN_VALUE);
    for(int i:docLists) System.out.println("doc " + (i-Short.MIN_VALUE));
    for(int i:docTermFrequency) System.out.println("termF " + (i-Short.MIN_VALUE));
    for(int i:postingsList) System.out.println("pos " + (i-Short.MIN_VALUE));
    
  }
}
