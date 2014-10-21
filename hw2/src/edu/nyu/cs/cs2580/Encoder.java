package edu.nyu.cs.cs2580;

import java.util.Vector;

public class Encoder {

  /*
   * encode: +-short -> +int -> delat(int) -> vbyte
   * decode: vbyte -> delta(int) -> +int -> +-short
   */
  
  
  public static byte[] encode(short[] input){
    //1. minus min
    int[] offsetInput = new int[input.length];
    for(int i=0; i<input.length; ++i) offsetInput[i] = (int)input[i] - (int)Short.MIN_VALUE;
    //2. do delta compression
    int previous = 0;
    int current = 0;
    for(int i=0; i<offsetInput.length;){
    //compress doc
      current = previous;
      previous = offsetInput[i];
      if(i!=0){
        offsetInput[i] -= current;
      }
      i++;//to frelist #
      int pre=0;
      int cur=0;
      //positions
      for(int j=1; j<=offsetInput[i]; ++j){
        cur=pre;
        pre=offsetInput[i+j];
        if(j!=1){
          offsetInput[i+j] -= cur;
        }
      }
      i += offsetInput[i] + 1;
    }
    //for(int i:offsetInput) System.out.println(i); 
    //3. v-byte compression
    Vector<Byte> output = new Vector<Byte>();
    for(int i: offsetInput){
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
  
  public static short[] decode(byte[] input, Vector<Short> postingsList,
      Vector<Short> docLists, Vector<Short> docTermFrequency){
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
    //2. de deltat compression
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
        int prePosition = 0;
        for(int j=0; j<len; ++j){
          int positionOffset = deCompressionResult.get(i++);
          int currentPosition = prePosition + positionOffset;
          prePosition = currentPosition;
          outputList.add(currentPosition);
        }
      }
    //3. convert to short array and plus byte.min
    short[] outputOffset = new short[deCompressionResult.size()];
    for(int i=0; i<outputList.size(); ++i) 
      outputOffset[i] = (short) (outputList.get(i) + Short.MIN_VALUE);
    //3* put outputList into docIdList, freqList, and postingList
    for(int i=0; i<outputOffset.length;){
      docLists.add(outputOffset[i++]);
      short len = outputOffset[i++];
      docTermFrequency.add(len);
      System.out.println("len " + ((int)len-(int)(Short.MIN_VALUE)));
      for(int j=0; j< (int)len-(int)(Short.MIN_VALUE); ++j){
        postingsList.add(outputOffset[i++]);
      }
    }
    
    return outputOffset;
  }

  public static void main(String[] args){
    short[] b = new short[7];
    //1 2 1 3 2 1 1
    b[0]= 1+Short.MIN_VALUE;
    b[1]= 2+Short.MIN_VALUE;
    b[2]= 1+Short.MIN_VALUE;
    b[3]= 3+Short.MIN_VALUE;
    b[4]= 2+Short.MIN_VALUE;
    b[5]= 1+Short.MIN_VALUE;
    b[6]= 1+Short.MIN_VALUE;
    byte[] output = encode(b);
    
    Vector<Short> postingsList = new Vector<Short>();
    Vector<Short> docLists = new Vector<Short>();
    Vector<Short> docTermFrequency = new Vector<Short>();
    short[] input = decode(output, postingsList, docLists, docTermFrequency);
    for(short i:input) System.out.println(i-Short.MIN_VALUE);
    for(short i:docLists) System.out.println("doc " + (i-Short.MIN_VALUE));
    for(short i:docTermFrequency) System.out.println("termF " + (i-Short.MIN_VALUE));
    for(short i:postingsList) System.out.println("pos " + (i-Short.MIN_VALUE));
    
  }
}
