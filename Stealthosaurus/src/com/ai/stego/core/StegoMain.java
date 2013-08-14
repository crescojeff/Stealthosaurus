/*
 * Nota Bene: this program expects to decode output it created, and it is hardcoded atm to look
 * for the following naming format:
 * 1. Sterling source is "assets/" + imageIn, where imageIn is the exact image name specified on the command line
 * 2. modified source is "assets/result_encode_<algorithm>_<image name specified>.<image format specified>" 
 */

package com.ai.stego.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;


import com.ai.stego.io.FSliaison;
import com.ai.stego.transform.Transmute;

public class StegoMain {
	//Root members
	private byte[] podImageArrayIN; //used for image matrix read from a file
	private byte[] podImageArraySecondaryIN;
	private byte[] podImageArrayOUT; //used for image matrix write to a file
	private byte[] podImageArraySecondaryOUT;
	private int[] intMaskArray; //used for mask read from a file, no negatives
	private char[] charMaskArray; //used for mask read from a file, negative values supported
	private char[] charMessageArray;
	private String imageFileIn;
	private String imageFileOut;
	private String messageFileIn;
	private String op;
	private int imageDims[] = {EntryPoint.imageSizeX,EntryPoint.imageSizeY};
	private int maskDims[] = {EntryPoint.maskSizeX,EntryPoint.maskSizeY};
	private int gammaLow;
	private int gammaHigh;
	private Vector<Vector<Integer>> vectorMaskFeed2D;
	private Vector<Vector<Byte>> vectorArbImageFeed2D;
	private Vector<Vector<Byte>> vectorArbImageOUT;
	private ArrayList<Byte> zeroBytesCollection = new ArrayList<Byte>();
	private String modImageFileIn;
	
	
	//Root children 
	private FSliaison liaison;
	private Transmute alchemy;
	
	
	public StegoMain() throws IOException{
		
		liaison = new FSliaison(this);
		alchemy = new Transmute(this);
		
		
		vectorMaskFeed2D = new  Vector<Vector<Integer>>(10,1);
		vectorArbImageFeed2D = new Vector<Vector<Byte>>(10,1);
		imageFileIn = "assets/" + EntryPoint.imageIn;
		messageFileIn = "assets/message/" + EntryPoint.messageFileIn;
		imageFileOut = "assets/result_";
		podImageArrayIN = new byte[EntryPoint.imageSizeX*EntryPoint.imageSizeY];
		podImageArrayOUT = new byte[EntryPoint.imageSizeX*EntryPoint.imageSizeY];
		//podImageArraySecondaryOUT = new byte[EntryPoint.imageSizeX*EntryPoint.imageSizeY];
		intMaskArray = new int[EntryPoint.maskSizeX*EntryPoint.maskSizeY];
		charMaskArray = new char[EntryPoint.maskSizeX*EntryPoint.maskSizeY];
		op = EntryPoint.opToEx;
		
		if(EntryPoint.opToEx.equals("encode")){
			loadInputArrays();
		}
		else if(EntryPoint.opToEx.equals("decode")){
			if(EntryPoint.imageDataType.equals("pgm")){
				modImageFileIn = imageFileOut + "encode_" + EntryPoint.algToEx + "_" + EntryPoint.imageIn + ".pgm";
			}
			else if(EntryPoint.imageDataType.equals("ppm")){
				modImageFileIn = imageFileOut + "encode_" + EntryPoint.algToEx + "_" + EntryPoint.imageIn + ".ppm";
			}
			loadModInputArrays();
			
		}
		action();
	
		//MaxMemory.getMem();
	}
	
	//Accessors only
	public String getImageNameIN(){
		return imageFileIn;
	}
	public String getMessageFileIN(){
		return messageFileIn;
	}
	public byte[] getPodImageArrayIN(){
		return podImageArrayIN;
	}
	public int[] getIntMaskArray(){
		return intMaskArray;
	}
	public char[] getCharMaskArray(){
		return charMaskArray;
	}
	public int[] getImageDims(){
		return imageDims;
	}
	public int[] getMaskDims(){
		return maskDims;
	}
	//Accessor and setter
	public byte[] getPodImageArrayOUT(){
		return podImageArrayOUT;
	}
	public void setPodImageArrayOUT(byte b[]){
		podImageArrayOUT = b;
	}
	public String getOp(){
		return op;
	}
	public void setOp(String s){
		op = s;
	}
	
	

	//Below we will read in the image and the mask via FSliaison
	//and store the data read in as a 1D array to be processed as if it were
	//a 2D matrix
	public void loadInputArrays() throws IOException{
	  if(EntryPoint.algToEx.equals("charbyn")){
		//Init the streams
		if(EntryPoint.imageDataType.equals("pgm") || EntryPoint.imageDataType.equals("ppm")){
			liaison.createReadBytes(imageFileIn);
			if(EntryPoint.messageFileIn != null && !messageFileIn.equals("assets/masks/none")){
				liaison.createRead(messageFileIn);
			}
			
		
			//Read in the image and mask arrays
			podImageArrayIN = liaison.readInAndReturnBytes(podImageArrayIN.length);
			if(EntryPoint.messageFileIn != null && !messageFileIn.equals("assets/masks/none")){
				charMessageArray = liaison.readInAndReturnChars(EntryPoint.messageLength); //  readInAndReturnNegInts(intMaskArray.length);
			}
			
			//Close the streams
			liaison.closeStreams();
		}
	  }
	  else if(EntryPoint.algToEx.equals("microbits")){
			//Init the streams
			if(EntryPoint.imageDataType.equals("pgm") || EntryPoint.imageDataType.equals("ppm")){
				//ready the source byte input...
				liaison.createReadBytes(imageFileIn);
				
			
				//Read in the image and mask arrays
				podImageArrayIN = liaison.readInAndReturnBytes(podImageArrayIN.length);
				
				//Close the streams
				liaison.closeStreams();
				
				
				//repeat for the message byte input
				podImageArraySecondaryIN = new byte[EntryPoint.messageLength];
				liaison.createReadBytes(messageFileIn);
				podImageArraySecondaryIN = liaison.readInAndReturnBytes(podImageArraySecondaryIN.length);
				liaison.closeStreams();
			}
	  }
		
	}
	
	public void loadModInputArrays() throws IOException{
		//Init the streams
		if(EntryPoint.imageDataType.equals("pgm") || EntryPoint.imageDataType.equals("ppm")){
			System.out.println("imaghefilein is " + imageFileIn);
			System.out.println("modimagefilein is " + modImageFileIn);
			liaison.createReadBytes(imageFileIn);//"assets/leggy_vb.dat");//imageFileIn);
			
			
			podImageArrayIN = liaison.readInAndReturnBytes(podImageArrayIN.length);
			
			//Close the streams
			liaison.closeStreams();
			
	
			System.out.println("modimagfilein is " + modImageFileIn);
			liaison.createReadBytes(modImageFileIn);
			
			//Read in the image and mask arrays
			podImageArrayOUT = liaison.readInAndReturnBytes(podImageArrayOUT.length);
			
			
			//Close the streams
			liaison.closeStreams();
	
		}
	}
	
	
	public void loadOutputArraysChar(String op, ArrayList<Character> chars) throws IOException{
		//Init the streams
		liaison.createWrite(imageFileOut + "secret_in_"  + EntryPoint.imageIn + op + ".txt"); 
		System.out.println("char array size in loadoutputarrayschar is " + chars.size());
		String charArray = "";
		for(int i=0;i<chars.size();i++){
			charArray += chars.get(i);
			System.out.println("in loadOutputArraysChar, char at index " + i + " is " + chars.get(i));
		}
		
		//Read in the image and mask arrays
		liaison.writeOut(charArray);
				
				
			
		//Close the streams
		liaison.closeStreams();
	}
	
	public void loadOutputArrays(String op, byte[] bytesOUT) throws IOException{
		//Init the streams
		if(EntryPoint.imageDataType.equals("pgm")){
			liaison.createWriteBytes(imageFileOut + op + ".pgm"); 
		}
		else if(EntryPoint.imageDataType.equals("ppm")){
			liaison.createWriteBytes(imageFileOut + op + ".ppm");
		}
		
		//Read in the image and mask arrays
		liaison.writeOutBytes(bytesOUT);
		
		
		/*
		//FOR DEBUG
		for(int i=0;i<intMaskArray.length;i++){
			System.out.println(intMaskArray[i]);
		}
		//end FORM DEBUG
		*/
		
		//Close the streams
		liaison.closeStreams();
	}
	
	
	
	//Initiates the chosen action from user input
	
	public void action() throws IOException{
		//char abc[] = {'a','b','c'};
		if(op.equals("encode")){
			if(EntryPoint.algToEx.equals("charbyn")){
				podImageArrayOUT = alchemy.encodeStegMutWithChars(podImageArrayIN, charMessageArray, EntryPoint.imageSizeX, EntryPoint.imageSizeY,EntryPoint.N,EntryPoint.imageDataType);
				loadOutputArrays("encode_charbyn_" + EntryPoint.imageIn,podImageArrayOUT);
			}
			else if(EntryPoint.algToEx.equals("microbits")){
				podImageArrayOUT = alchemy.encodeStegMutWithBytes(podImageArrayIN, podImageArraySecondaryIN, EntryPoint.imageSizeX, EntryPoint.imageSizeY,EntryPoint.microBits,EntryPoint.imageDataType);
				loadOutputArrays("encode_microbits_" + EntryPoint.imageIn,podImageArrayOUT);
			}
		}
		else if(op.equals("decode")){
			if(EntryPoint.algToEx.equals("charbyn")){
				loadOutputArraysChar("decode_charbyn_" + EntryPoint.imageIn,alchemy.decodeStegMutWithChars(podImageArrayOUT, podImageArrayIN, EntryPoint.imageSizeX, EntryPoint.imageSizeY,EntryPoint.imageDataType));
			}
			else if(EntryPoint.algToEx.equals("microbits")){
				ArrayList<Byte> temp = alchemy.decodeStegMutWithBytes(podImageArrayOUT, podImageArrayIN, EntryPoint.imageSizeX, EntryPoint.imageSizeY,EntryPoint.imageDataType,EntryPoint.microBits);
				podImageArraySecondaryOUT = new byte[temp.size()];
				for(int i=0;i<podImageArraySecondaryOUT.length;i++){
					podImageArraySecondaryOUT[i] = temp.get(i);
				}
				loadOutputArrays("decode_microbits_" + EntryPoint.imageIn,podImageArraySecondaryOUT);//alchemy.decodeStegMutWithBytes(podImageArrayOUT, podImageArrayIN, EntryPoint.imageSizeX, EntryPoint.imageSizeY,EntryPoint.imageDataType,EntryPoint.microBits));
			}
		}
		
	}
	
	public int make8bit(byte i){
		int intRep = (int)i;
		//System.out.println("byte i before is " + i);
		//System.out.println("int intRep before is " + intRep);
		if(intRep<0){
			intRep += 256;
		}
		//System.out.println("byte i after is " + i);
		//System.out.println("int intRep after is " + intRep);
		return intRep;
	}
		
		
		
	
	private static class MaxMemory {
	    public static void getMem(){
	        Runtime rt = Runtime.getRuntime();
	        long totalMem = rt.totalMemory();
	        long maxMem = rt.maxMemory();
	        long freeMem = rt.freeMemory();
	        double megs = 1048576.0;

	        System.out.println ("Total Memory: " + totalMem + " (" + (totalMem/megs) + " MB)");
	        System.out.println ("Max Memory:   " + maxMem + " (" + (maxMem/megs) + " MB)");
	        System.out.println ("Free Memory:  " + freeMem + " (" + (freeMem/megs) + " MB)");
	    }
	}

	
}
