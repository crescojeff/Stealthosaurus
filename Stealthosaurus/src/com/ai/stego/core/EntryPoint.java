package com.ai.stego.core;

import java.io.IOException;
import java.util.Scanner;

public class EntryPoint {
	
	private static StegoMain steg;
	private static Scanner scan = new Scanner(System.in);
	protected static int imageSizeX;
	protected static int imageSizeY;
	protected static int maskSizeX;
	protected static int maskSizeY;
	protected static String imageIn;
	protected static String maskIn;
	protected static String opToEx;
	protected static String algToEx;
	protected static String imageDataType;
	protected static int messageLength;
	protected static String messageFileIn;
	protected static int N;
	protected static int microBits;
	protected static String mode;
	/**
	* @param args
	 * @throws IOException 
	*/
	public static void main(String[] args) throws IOException {
	int isLegal = 0;
	//System.out.println("" + (255 & 207));
	//System.exit(0);
	//Obtain user input 
	/*
	int isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the data type in the image (byte, ascii, or int) ");
		imageDataType = scan.nextLine();
		if(imageDataType.equals("byte") || imageDataType.equals("ascii") || imageDataType.equals("int")){
			isLegal = 1;
		}
		else{
			System.out.println("The image width must be of type byte, ascii, or int");
		}
	}
	*/
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter mode (norm or debug_e or debug_d)  ");
		mode = scan.nextLine();
		if(mode.equals("debug_e")){
			imageSizeX = 245;
			imageSizeY = 485;
			imageIn = "leggy_vb.dat";
			opToEx = "encode";
			algToEx = "charbyn";
			imageDataType = "pgm";
			messageLength = 2168;//693;
			messageFileIn = "fbMessage_mmi.txt";
			N = 30;
			microBits = 2;
			isLegal = 1;
		}
		else if(mode.equals("debug_d")){
			imageSizeX = 245;
			imageSizeY = 485;
			imageIn = "leggy_vb.dat";
			opToEx = "decode";
			algToEx = "charbyn";
			imageDataType = "pgm";
			messageLength = 54;
			messageFileIn = "fbMessage_mmi.txt";
			N = 5;
			microBits = 2;
			isLegal = 1;
		}
		else if(mode.equals("norm")){
			
			isLegal = 1;
		}
		else{
			System.out.println("Please choose one of the following operations: norm or debug ");
		}
	}
	if(!mode.equals("debug_e")&&!mode.equals("debug_d")){
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the operation you wish to execute (encode or decode)  ");
		opToEx = scan.nextLine();
		if(opToEx.equals("encode")){
			System.out.println("Please enter the algorithm to use [charbyn, microbits]");
			algToEx = scan.nextLine();
			if(algToEx.equals("charbyn")){
				System.out.println("Please enter N");
				N = Integer.parseInt(scan.nextLine());
			}
			else if(algToEx.equals("microbits")){
				System.out.println("Please enter ubits value ");
				microBits = Integer.parseInt(scan.nextLine());
			}
			isLegal = 1;
		}
		else if(opToEx.equals("decode")){
			System.out.println("Please enter the algorithm used in encoding [charbyn, microbits]");
			algToEx = scan.nextLine();
			isLegal = 1;
		}
		else{
			System.out.println("Please choose one of the following operations: encode or decode ");
		}
	}
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the image format (pgm for greyscale image, ppm for color image) ");
		imageDataType = scan.nextLine();
		if(imageDataType.equals("pgm") || imageDataType.equals("ppm")){
			isLegal = 1;
		}
		else{
			System.out.println("The image width must be of type pgm or ppm");
		}
	}
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the width of the source image ");
		imageSizeX = Integer.parseInt(scan.nextLine());
		if(imageSizeX > 0){
			if(imageDataType.equals("ppm")){ //for RGB color we need to multiply the width by 3 to allow for the three channels
				imageSizeX = imageSizeX*3;
			}
			isLegal = 1;
		}
		else{
			System.out.println("The image width must be greater than zero");
		}
	}
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the height of the source image ");
		imageSizeY = Integer.parseInt(scan.nextLine());
		if(imageSizeY > 0){
			isLegal = 1;
		}
		else{
			System.out.println("The image height must be greater than zero");
		}
	}
	if(opToEx.equals("encode") && algToEx.equals("charbyn")){
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the length (in chars) of the message to be encoded]");
		messageLength = Integer.parseInt(scan.nextLine());
		if(messageLength >= 0){
			isLegal = 1;
		}
		else{
			System.out.println("The mesage length cannot be negative");
		}
	}
	}
	if(opToEx.equals("encode") && algToEx.equals("microbits")){
		isLegal = 0;
		while(isLegal != 1){
			System.out.println("Please enter the length (in bytes) of the message to be encoded]");
			messageLength = Integer.parseInt(scan.nextLine());
			if(messageLength >= 0){
				isLegal = 1;
			}
			else{
				System.out.println("The mesage length cannot be negative");
			}
		}
		}
	/*
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the height of the mask [enter 1 if not applicable]");
		maskSizeY = Integer.parseInt(scan.nextLine());
		if(maskSizeY > 0){
			isLegal = 1;
		}
		else{
			System.out.println("The mask height must be greater than zero");
		}
	}
	*/
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the name of the image file (ex. photo.pgm) ");
		imageIn = scan.nextLine();
		isLegal = 1;
	}
	
	if(opToEx.equals("encode")){
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the name of the message file (ex. mySecret.txt, myImage.dat, myProgram.dat) ");
		messageFileIn = scan.nextLine();
		isLegal = 1;
	}
	}
	} //end if mode != debug
	/*
	isLegal = 0;
	while(isLegal != 1){
		System.out.println("Please enter the operation you wish to execute (encode or decode)  ");
		opToEx = scan.nextLine();
		if(opToEx.equals("encode")){
			System.out.println("Please enter the algorithm to use [charbyn]");
			algToEx = scan.nextLine();
			System.out.println("Please enter N");
			N = Integer.parseInt(scan.nextLine());
			isLegal = 1;
		}
		else if(opToEx.equals("decode")){
			System.out.println("Please enter the algorithm used in encoding [charbyn]");
			algToEx = scan.nextLine();
			isLegal = 1;
		}
		else{
			System.out.println("Please choose one of the following operations: encode or decode ");
		}
	}
	*/
	
/*
	//Process args
	imageSizeX = Integer.parseInt(args[0]);
	imageSizeY = Integer.parseInt(args[1]);
	maskSizeX = Integer.parseInt(args[2]);
	maskSizeY = Integer.parseInt(args[3]);
	imageIn = args[4];
	maskIn = args[5];
	opToEx = args[6];
*/
	
	//Init root object
	steg = new StegoMain();

	}

	


}
