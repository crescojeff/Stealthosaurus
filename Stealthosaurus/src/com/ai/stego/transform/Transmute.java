/*
 * useful stego link: http://www.dreamincode.net/forums/topic/27950-steganography/
 * for fb stuff, may need to upload/download the source image too
 * since fb seems to potentially alter the image upon upload
 */

package com.ai.stego.transform;

import java.util.ArrayList;

import com.ai.stego.core.StegoMain;

public class Transmute {
	//Handle to root
	private StegoMain hMain;
	
	//Transformation utilities
	private byte[] newImage;
	private int max_color_value = 255;
	
	//Convolution Specific utilities
	private int boundsClearanceX = 0; //defines the extra space a mask needs around its central axis cell
	private int boundsClearanceY = 0;
	private int productSum = 0;
	private int windowTrackerX = 0; //tracks X position in the window
	private int windowTrackerY = 0; //tracks Y position in the window
	private int imageBias = 0;
	private int maskBias = 0;
	private boolean boundLeft = false;
	private boolean boundRight = false;
	private boolean boundUp = false;
	private boolean boundDown = false;
	private String boundStatus = ""; //0 is unbound, 1 is bound left only, 2 is bound right only, 3 is bound up only, 4 is bound down only. From there we combine as if in a string, such that bound left and up would be 13
	
	//private byte podArray[]; //let's keep the responsibility for providing byte[] in the callers of Transmute routines
	
	public Transmute(StegoMain seg){
		hMain = seg;
		//ego sum dominus ursi
		//ego sum dominus strutionum
	}
	
	public int getMaxColorValue(){
		return max_color_value;
	}
	public void setMaxColorValue(int i){
		max_color_value = i;
	}
	
	/*
	 * Hides ascii text in an image by adding the char code value to the pixel value (or the selected color channel values if using a color image) 
	 * we'll be sticking to 8 bits, so if the sum exceeds 255 the resultant value will wrap
	 * On the other end for retrieval of the original text, the modified image will compared to a copy of the original source image. When a variant 
	 * value is detected, the difference between the modified image's value and the source image's value is recorded as the char value.  If the mod value
	 * is less than the source value, the mod value is added to 255 before having th esource value subtracted from it
	 * 
	 * chars are hidden in every Nth pixel, where N*message.length <= imageWidth*imageHeight
	 */
	public byte[] encodeStegMutWithChars(byte[] source,char[] message,int imageWidth, int imageHeight, int N, String dataType){
		//Test N for legal value
		if(N*message.length > imageWidth*imageHeight){
			System.out.println("FATAL ERROR: N is too large. N must conform to the following relationship N*message.length <= imageWidth*imageHeight");
			System.exit(1);
		}
		
		//So long as N is at least 18 (e.g. barely legal)
		byte modImage[] = new byte[imageWidth*imageHeight];
		int x = 0;
		int y = 0;
		int offsetCoefficient = 0; //post-incremented by row
		int offset = 0; //tracks how far into the single D array we need to jump to get to the current X,Y coordinate 
		int modImageIndexCounter = 0;
		int messageIndexCounter = 0;
		int nTracker = 0;
		char cMod;
		char cSrc;
		
		for (y=0;y<imageHeight;y++){
			offset = offsetCoefficient * imageWidth;
			for(x=offset;x<offset+imageWidth;x++){
				//Check to see if we're at an Nth pixel.. if yes, add the char value. else, write source value unchanged
				if(modImageIndexCounter%N == 0 && nTracker < message.length){
					if(source[x] < 0){
						cSrc = (char)((source[x]*-1) + 127);
						System.out.println("cSrc is: " + (int)cSrc + " as an integer, coming from source[x] of: " + source[x] + " with index x of: " + x);
					}
					else{
						cSrc = (char)(source[x]);
						System.out.println("cSrc is: " + (int)cSrc + " as an integer, coming from source[x] of: " + source[x] + " with index x of: " + x);
					}
					modImage[modImageIndexCounter] = makeByteFromChar((char)(cSrc + message[messageIndexCounter]));///applyWrap((source[x] + message[messageIndexCounter]));	
					System.out.println("Encoding... source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is " + message[messageIndexCounter]);
					System.out.println("Encoding...modimage at " + modImageIndexCounter + " is " + modImage[modImageIndexCounter]);//make8bit(modImage[modImageIndexCounter]));
					messageIndexCounter++;
					nTracker++;
				}
				else{
					modImage[modImageIndexCounter] = source[x];
				}
				modImageIndexCounter++;
			}
			offsetCoefficient++;
		}
		//for(int i=0;i<modImage.length;i++){
			//System.out.println("modimage at " + i + " is " + make8bit(modImage[i]));
		//}
		/*
		//FOR DEBUG
		ArrayList<Character> c = decodeStegMutWithChars(modImage,source,512,512,"ppm");
		for(int i=0;i<c.size();i++){
			System.out.println(c.get(i) + " ehwot?!");
		}
		//end DEBUG
		*/
		
		return modImage;
	}
	
	
	
	public ArrayList<Character> decodeStegMutWithChars(byte[] mod,byte[] source,int imageWidth, int imageHeight, String dataType){
		
		/*
		 * Make CERTAIN the header is either removed in the mod image or accounted for in the parsing!
		 * ..ke great sacrifice to he who gave being to the Sun and Morning, who leads the waters, Indra, for ego sum dominus strutionum
		 */
		
		ArrayList<Character> decodedMessage = new ArrayList<Character>();
		int x = 0;
		int y = 0;
		int offsetCoefficient = 0; //post-incremented by row
		int offset = 0; //tracks how far into the single D array we need to jump to get to the current X,Y coordinate 
		int messageIndexCounter = 0;
		
		if(dataType.equals("ppm")){ //Process expecting RGB color channels
			for (y=0;y<imageHeight;y++){
				offset = offsetCoefficient * imageWidth;
				for(x=offset;x<offset+imageWidth;x++){
					//Check for pixel value equivalency
					if(mod[x] == source[x]){
					
						//do nothing, this is a normal pixel
					}
					else{
						
						if(mod[x] > source[x]){
							/*
							decodedMessage.add((char)((mod[x]-source[x])));//+1));
							System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is " + (char)((mod[x]-source[x])));
							System.out.println("2modimage at " + x + " is " + (mod[x]));
							*/
							/*
							if(source[x] < 0){
								decodedMessage.add((char)((mod[x]-source[x])+1));
								System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is " + (char)((mod[x]-source[x])+1));
								System.out.println("2modimage at " + x + " is " + make8bit(mod[x]));
							}
							else{
								*/
								decodedMessage.add((char)((mod[x]-source[x])-1));//+1));
								System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is " + (char)((mod[x]-source[x])-1));//+1));
								System.out.println("2modimage at " + x + " is " + make8bit(mod[x]));
							/*
							}
							*/
						}
						///else if(mod[x] <= source[x]){
						else if(mod[x] < source[x]){
							decodedMessage.add((char)(((255+mod[x])-source[x])+1));
							System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is +255 " + (char)(((255+mod[x])-source[x])+1));
							System.out.println("2modimage at " + x + " is " + (mod[x]));
						}
						messageIndexCounter++;
				
					}
					
				}
				offsetCoefficient++;
			}
		}
		else if(dataType.equals("pgm")){ //Process expecting single greyscale color values
			///char[] cSource = new char[source.length];
			///char[] cMod = new char[mod.length];
			char cMod;
			char cSrc;
			///boolean isFlip = false;
			
			for (y=0;y<imageHeight;y++){
				offset = offsetCoefficient * imageWidth;
				for(x=offset;x<offset+imageWidth;x++){
					//Check for pixel value equivalency
					if(mod[x] == source[x]){
						//do nothing, this is a normal pixel
					}
					else{
					
						/*
						if(mod[x]<0){
							cMod[x] = set8bit(mod[x]);
						}
						if(source[x]<0){
							cSource[x] = set8bit(source[x]);
						}
						*/
						
						//NB: remember that since bytes in Java are signed, any negative values in the array will
						//actually translate to char values greater than 127.  Ergo, if source[x] is 120 and mod[x] is -50, mod[x] is 
						//actually greater than source[x] (as it should be using charbyn).  We need to account for that in our 
						//arithmetic comparisons and resultant operations below...
						
						//First convert our byte values into chars
						if(source[x] < 0){
							cSrc = (char)((source[x]*-1)+127);
						}
						else{
							cSrc = (char)source[x];
						}
						if(mod[x]<0){
							cMod = (char)((mod[x]*-1)+127);
							///isFlip = true;
							//System.out.println("the val of mod[x] is " + mod[x] + " and the val of cMod is " + cMod);
						}
						else{
							cMod = (char)mod[x];
						}
						
						//Now decode using the chars
						decodedMessage.add((char)(Math.abs(cMod-cSrc)));
						System.out.println("index x is: " + x + " and the value of mod[x]: " + mod[x] + " and the value of source[x]: " + source[x] + " became the following char values respectively: " + cMod + "," + cSrc);
						System.out.println("the difference between these chars and the encoded char is: " + (char)(Math.abs(cMod-cSrc)));
					
						/*
						if(mod[x] > source[x]){
							
							if(source[x] < 0){
								cSrc = (char)(source[x]+127);
								if(mod[x]<0){
									cMod = (char)(mod[x]+127);
									///isFlip = true;
									System.out.println("the val of mod[x] is " + mod[x] + " and the val of cMod is " + cMod);
								}
								else{
									cMod = (char)mod[x];
								}
								decodedMessage.add((char)(cSrc-cMod+1));
								System.out.println("2source at " + x + " is " + (source[x]) + " and message at "+ messageIndexCounter + " is " + (char)(cSrc-cMod+1));
								System.out.println("2modimage at " + x + " is " + make8bit(mod[x]));
							}
							else{
								
								decodedMessage.add((char)((mod[x]-source[x])));//+1));
								System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is " + (char)((mod[x]-source[x])));//+1));
								System.out.println("2modimage at " + x + " is " + (mod[x]));
						
							}
						
							
							
						}
						///else if(mod[x] <= source[x]){
						else if(mod[x] < source[x]){
							if(mod[x] < 0){
								cMod = (char)(mod[x]+127);
								if(source[x]<0){
									cSrc = (char)(source[x]+127);
									///isFlip = true;
									System.out.println("the val of source[x] is " + source[x] + " and the val of cSrc is " + cSrc);
								}
								else{
									cSrc = (char)source[x];
								}
								//decodedMessage.add((char)(((255+mod[x])-source[x])+1));
								decodedMessage.add((char)((cSrc-cMod)));
								System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is {no longer} +255 " + (char)(127-(cSrc-cMod)));
								System.out.println("2modimage at " + x + " is " + (mod[x]));
							}
							else{
								
								decodedMessage.add((char)((mod[x]-source[x])));//+1));
								System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is " + (char)(127-(mod[x]-source[x])));//+1));
								System.out.println("2modimage at " + x + " is " + (mod[x]));
						
							}
						}
						*/
						messageIndexCounter++;
						
					}
					
				}
				offsetCoefficient++;
			}
		}
		
		
		return decodedMessage;
	}
	
	/*
	 * uBits is the number of bits to store from each byte of the message in each byte of the source. 8%uBits == 0 must be return true
	 * uBits = 8 means a one-to-one relationship and will result in maximum image distortion but requiring minimum extra space
	 * uBits = 1 means an one-to-eight relationship between the message and source kernels such that only one bit of the message is stored
	 * in each 1 byte of the source image, and it takes 8 bytes of source to complete one byte of message encoding.  Thus for uBits = 1, the source must be
	 * at least 8 times greater in byte length than the message
	 * 
	 * 
	 * Hides any binary 'message' data (such as an executable or an image) in an image whose
	 * size in bytes must be at least 8/uBits times the byte size of the 'message' you wish to hide.
	 * It will be hidden according to the following algorithm:
	 * 
	 * For each byte in the message structure, take the most significant uBits bits and add them to the least significant uBits bits of the source byte
	 * at position n.  Then, add the uBits least significant bits from the message to the uBits least significant bits of the source byte at n+1.
	 * 
	 * The result should be the source image with a color distortion which is inversely proportional to uBits.
	 */
	public byte[] encodeStegMutWithBytes(byte[] source,byte[] message,int imageWidth, int imageHeight, int uBits, String dataType){
		//Test uBits for legal value given the source and message byte sizes
		if((8/uBits)*message.length > source.length){ // < message.length){
			System.out.println("FATAL ERROR: x is too large. x must conform to the following relationship (8/uBits)*source.length < message.length");
			System.exit(1);
		}
		
		//So long as source is large enough we'll continue... quod scriptor quod mulier dixit
		byte modImage[] = new byte[imageWidth*imageHeight];
		int x = 0;
		int y = 0;
		int offsetCoefficient = 0; //post-incremented by row
		int offset = 0; //tracks how far into the single D array we need to jump to get to the current X,Y coordinate 
		int modImageIndexCounter = 0;
		int messageIndexCounter = 0;
		int nTracker = 0;
		int shiftR = 8-uBits;
		int bitMaskIndex = 0;
		int srcTracker = 0;
		String bitmasks[] = new String[8/uBits]; //should be stored in increasing order (0x01...0xFF)
		
		/*
		 * To obtain bits from the message uBits at a time starting from the most significant
		 * and working down to the least significant bits we need to do the following:
		 * loop while i<8 with step uBits
		 * shift message[messageIndexCounter] right by (8-uBits)-i, then bit-wise AND the results 
		 * with a bitmask that sets any extraneous bit positions towards the most significant to zero.  The bitmask for the first 
		 * iteration would be 0xFF or all 1's since there are no extra bits to the left.  Given uBits = 2 and shiftR = 6,
		 * the second iteration would use 0x3F to zero out the left two most-significant bits.  The third would use 0x0F and the last would use 0x03
		 * Generalized, the bitmask at an index x must be bitmask[x] = (pow(2.0,(uBits*(x+1)))-1
		 */
		
		//Populate the bitmask array
		for(int i=0;i<bitmasks.length;i++){
			bitmasks[i] = Integer.toHexString((int)((Math.pow(2.0, uBits*(i+1)))-1));
			System.out.println("hex string is " + bitmasks[i]);
			System.out.println("integer.parseint on radix 16 is " + Integer.parseInt(bitmasks[i], 16));
		}
		
		
		for (y=0;y<imageHeight;y++){
			offset = offsetCoefficient * imageWidth;
			for(x=offset;x<offset+imageWidth;x++){
				//Check to see if we still have some of the message structure left to store
				//if(nTracker < message.length*(8/uBits)){
				if(messageIndexCounter < message.length){
					//for (int i=shiftR;i>=0;i=i-uBits){
					//for (int i=0;i<shiftR;i=i+uBits){
					for (int i=0;i<8;i=i+uBits){
						modImage[modImageIndexCounter] += (byte) applyWrap((source[x+srcTracker] + (message[messageIndexCounter]>>>shiftR)&Integer.parseInt(bitmasks[bitMaskIndex], 16)));	
						System.out.println("source at " + (x+srcTracker) + " is " + source[x+srcTracker] + " and message segment at "+ messageIndexCounter + " is " + ((message[messageIndexCounter]>>>shiftR)&Integer.parseInt(bitmasks[bitMaskIndex], 16)));//message[messageIndexCounter]);
						System.out.println("modimage at " + modImageIndexCounter + " is " + (modImage[modImageIndexCounter]));
						//messageIndexCounter++;
						nTracker++;
						bitMaskIndex++;
						srcTracker++;
						//modImageIndexCounter++;
					}
					messageIndexCounter++;
				}
				else{
					modImage[modImageIndexCounter] = source[x];
					srcTracker++;
					//modImageIndexCounter++;
				}
				modImageIndexCounter++;
				//srcTracker = 0;
				bitMaskIndex = 0;
			}
			offsetCoefficient++;
		}
		//for(int i=0;i<modImage.length;i++){
			//System.out.println("modimage at " + i + " is " + make8bit(modImage[i]));
		//}
		/*
		//FOR DEBUG
		ArrayList<Character> c = decodeStegMutWithChars(modImage,source,512,512,"ppm");
		for(int i=0;i<c.size();i++){
			System.out.println(c.get(i) + " ehwot?!");
		}
		//end DEBUG
		*/
		
		return modImage;
	}
	
	public ArrayList<Byte> decodeStegMutWithBytes(byte[] mod,byte[] source,int imageWidth, int imageHeight, String dataType, int bits){
		
		/*
		 * Make CERTAIN the header is either removed in the mod image or accounted for in the parsing!
		 * would be better to encode the uBits number in the image too, but for now we'll provide it as an argument to this function
		 */
		
		ArrayList<Byte> decodedMessage = new ArrayList<Byte>();
		int x = 0;
		int y = 0;
		int offsetCoefficient = 0; //post-incremented by row
		int offset = 0; //tracks how far into the single D array we need to jump to get to the current X,Y coordinate 
		int messageIndexCounter = 0;
		
		if(dataType.equals("ppm")){ //Process expecting RGB color channels
			for (y=0;y<imageHeight;y++){
				offset = offsetCoefficient * imageWidth;
				for(x=offset;x<offset+imageWidth;x++){
					//Check for pixel value equivalency
					if(mod[x] == source[x]){
					
						//do nothing, this is a normal pixel
					}
					else{
						
						
						/*
						if(mod[x] > source[x]){
							decodedMessage.add((byte)((mod[x]-source[x])));//+1));
							System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is " + (char)((mod[x]-source[x])));
							System.out.println("2modimage at " + x + " is " + (mod[x]));
							
						}
						///else if(mod[x] <= source[x]){
						else if(mod[x] < source[x]){
							decodedMessage.add((byte)(((255+mod[x])-source[x])+1));
							System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is +255 " + (char)(((255+mod[x])-source[x])+1));
							System.out.println("2modimage at " + x + " is " + (mod[x]));
						}
						messageIndexCounter++;
						*/
					}
					
				}
				offsetCoefficient++;
			}
		}
		else if(dataType.equals("pgm")){ //Process expecting single greyscale color values
			for (y=0;y<imageHeight;y++){
				offset = offsetCoefficient * imageWidth;
				for(x=offset;x<offset+imageWidth;x++){
					//Check for pixel value equivalency
					if(mod[x] == source[x]){
						//do nothing, this is a normal pixel
					}
					else{
					
						if(mod[x] > source[x]){
							decodedMessage.add((byte)((mod[x]-source[x])+1));
							System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is " + (char)((mod[x]-source[x])+1));
							System.out.println("2modimage at " + x + " is " + make8bit(mod[x]));
							
						}
						///else if(mod[x] <= source[x]){
						else if(mod[x] < source[x]){
							decodedMessage.add((byte)(((255+mod[x])-source[x])+1));
							System.out.println("2source at " + x + " is " + source[x] + " and message at "+ messageIndexCounter + " is +255 " + (char)(((255+mod[x])-source[x])+1));
							System.out.println("2modimage at " + x + " is " + make8bit(mod[x]));
						}
						messageIndexCounter++;
						
					}
					
				}
				offsetCoefficient++;
			}
		}
		
		
		return decodedMessage;
	}
	
	/*
	 * Check for value greater than MAX_COLOR_VALUE or less than 0.  If found,
	 * return either myInt-MAX_COLOR_VALUE or myInt+MAX_COLOR_VALUE, depending on which bound was crossed
	 */
	public int applyWrap(int myInt){
		if (myInt > max_color_value){
			myInt -= max_color_value;
		}
		else if(myInt < 0){
			myInt += max_color_value;
		}
		return myInt;
	}
	
	
	public int make8bit(byte i){
		int intRep = (int)i;
		//System.out.println("byte i before is " + i);
		//System.out.println("int intRep before is " + intRep);
		if(intRep<0){
			//////intRep += 256;
			intRep *= -1;
			intRep += 127;
		}
		//System.out.println("byte i after is " + i);
		//System.out.println("int intRep after is " + intRep);
		return intRep;
	}
	
	public char set8bit(byte i){
		char charRep = (char)i;
		//System.out.println("byte i before is " + i);
		//System.out.println("int intRep before is " + intRep);
		if(charRep<0){
			//////intRep += 256;
			charRep *= -1;
			charRep += 127;
		}
		//System.out.println("byte i after is " + i);
		//System.out.println("int intRep after is " + intRep);
		return charRep;
	}
	
	/*//can't make an unsigned byte in java -- the below will just be converted back to signed form if it goes beyond 127
	public byte set8bit(byte i){
		//int intRep = (int)i;
		//System.out.println("byte i before is " + i);
		//System.out.println("int intRep before is " + intRep);
		if(i<0){
			//////intRep += 256;
			i *= -1;
			i += 127;
		}
		//System.out.println("byte i after is " + i);
		//System.out.println("int intRep after is " + intRep);
		return i;
	}
	*/
	
	public byte makeByte(int i){
		byte byteRep = (byte)i;
		if(byteRep > 127){
			byteRep -= 256;
		}
		return byteRep;
	}
	
	public byte makeByteFromChar(char c){
		
		
		byte b = 0x00;
		if (c > 127){
			b = (byte)(0-(c-127));
		}
		else{
			b = (byte)c;
		}
		return b;
	}
	
	
	
	//Performs a 180 degree rotation on a 1D array
	public void rotate1D180(int intArray[]){
		int i = 0;
		int x = 0;
		int result[] = new int[intArray.length];
		for(i=intArray.length-1;i>=0;i--){
			result[x] = intArray[i];
			x++;
		}
		intArray = result;
	}
	
}
