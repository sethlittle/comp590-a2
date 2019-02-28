package app;

import ac.ArithmeticDecoder;
import ac.BinarySearchArithmeticDecoder;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class StaticACDecodeTextFileTEMPORAL {
  
  public static void main(String[] args)
    throws InsufficientBitsLeftException, IOException
  {
    String input_file_name = "data/static-compressed-TEMPORAL.dat";
    String output_file_name = "data/reout-TEMPORAL.dat";
    
    FileInputStream fis = new FileInputStream(input_file_name);
    
    InputStreamBitSource bit_source = new InputStreamBitSource(fis);
    
    int num_pixels = 1228800;
    int firstFramePix = 4096;
    int restOfPix = num_pixels - firstFramePix;
    int[][] firstFrame = new int[64][64];
    

    int[] symbol_counts = new int[512];
    Integer[] symbols = new Integer[512];
    
    for (int i = 0; i < 512; i++) {
      symbol_counts[i] = bit_source.next(32);
      symbols[i] = Integer.valueOf(i);
    }
    
    FreqCountIntegerSymbolModel model = 
      new FreqCountIntegerSymbolModel(symbols, symbol_counts);
    

    int range_bit_width = 40;
    BinarySearchArithmeticDecoder<Integer> decoder = new BinarySearchArithmeticDecoder(range_bit_width);
    

    System.out.println("Uncompressing file: " + input_file_name);
    System.out.println("Output file: " + output_file_name);
    System.out.println("Range Register Bit Width: " + range_bit_width);
    System.out.println("Number of symbols: " + num_pixels);
    
    FileOutputStream fos = new FileOutputStream(output_file_name);
    

    int firstSymbol = 0;
    for (int n = 0; n < 64; n++) {
      for (int m = 0; m < 64; m++) {
        firstSymbol = ((Integer)decoder.decode(model, bit_source)).intValue();
        fos.write(firstSymbol);
        firstFrame[n][m] = firstSymbol;
      }
    }
    
    int i = 0;
    while (i < restOfPix) {
	    for(int n = 0; n < 64; n++) {
	      for (int m = 0; m < 64; m++) {
	        int next_pixel = ((Integer)decoder.decode(model, bit_source)).intValue();
	        fos.write(firstFrame[n][m] - next_pixel + 256);
	        firstFrame[n][m] = (firstFrame[n][m] - next_pixel + 256);
	        i++;
	      }
	    }
    }


    System.out.println("Done.");
    fos.flush();
    fos.close();
    fis.close();
  }
}