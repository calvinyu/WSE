package edu.nyu.cs.cs2580;
import java.io.*;
class Pair<E, F> implements Serializable{
  E first;
  F second;
  Pair(E first, F second){
    this.first = first;
    this.second = second;
  }
}