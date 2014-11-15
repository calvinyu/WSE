package edu.nyu.cs.cs2580;

import java.util.Vector;

public class Encoder {
  /*
   * encode: +int -> delta(int) -> vbyte
   */
  public static byte[] encode(int[] input){
    // v-byte compression
    Vector<Byte> output = new Vector<Byte>();
    for(int i : input){
      while(i >= 128){
        output.add((byte) (i & 0x7F));
        i >>>= 7;
      }
      output.add((byte) (i|0x80));
    }
    //convert to primary array
    byte[] outputArray = new byte[output.size()];
    for(int i = 0; i < output.size(); ++i) outputArray[i] = output.get(i);
    return outputArray;
  }

  /*
   * decode: vbyte -> delta(int) -> +int
   */
  public static int[] decode(byte[] input){
    //1. de - vbyte compression
    Vector<Integer> deCompressionResult= new Vector<Integer>();
    for(int i = 0; i < input.length; i++){
      int position = 0;
      int result = ((int)input[i] & 0x7F);
      while((input[i] & 0x80) == 0){
        i++;
        position++;
        int unsignedByte = ((int) input[i] & 0x7F);
        result |= (unsignedByte << (7 * position));
      }
      deCompressionResult.add((result));
    }
    // 2. de delta compression
    Vector<Integer> outputList = new Vector<Integer>();
    int preDocId = 0;
    for(int i=0; i<deCompressionResult.size();){
      // Obtain document ID.
      int docidOffset = deCompressionResult.get(i++);
      int currentDocid = docidOffset + preDocId;
      outputList.add(currentDocid);
      preDocId = currentDocid;
      // Obtain frequency.
      int len = deCompressionResult.get(i++);
      outputList.add(len);
      // Obtain postings list.
      int prePosition = 0;
      for(int j = 0; j < len; ++j){
        int positionOffset = deCompressionResult.get(i++);
        int currentPosition = prePosition + positionOffset;
        prePosition = currentPosition;
        outputList.add(currentPosition);
      }
    }
    // 3. convert to int array
    int[] outputOffset = new int[deCompressionResult.size()];
    for(int i = 0; i < outputList.size(); ++i) outputOffset[i] = outputList.get(i);
    return outputOffset;
  }

  public static int[] decode(byte[] input, Vector<Integer> postingsList,
                             Vector<Integer> docLists, Vector<Integer> docTermFrequency){
    int[] outputOffset = decode(input);
    for(int i = 0; i < outputOffset.length;){
      docLists.add(outputOffset[i++]);
      int len = outputOffset[i++];
      docTermFrequency.add(len);
      for(int j = 0; j < len; ++j){
        postingsList.add(outputOffset[i++]);
      }
    }
    return outputOffset;
  }
}
