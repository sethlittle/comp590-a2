package app;

import ac.ArithmeticEncoder;
import io.OutputStreamBitSink;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class StaticACEncodeTextFileTEMPORAL
{
  public StaticACEncodeTextFileTEMPORAL() {}
  
  public static void main(String[] args)
    throws IOException
  {
    String input_file_name = "data/out.dat";
    String output_file_name = "data/static-compressed-TEMPORAL.dat";
    
    int range_bit_width = 40;
    
    System.out.println("Encoding text file: " + input_file_name);
    System.out.println("Output file: " + output_file_name);
    System.out.println("Range Register Bit Width: " + range_bit_width);
    
    int num_pixels = (int)new File(input_file_name).length();
    int firstFramePix = 4096;
    int restOfPix = num_pixels - firstFramePix;
    int[][] firstFrame = new int[64][64];
    


    FileInputStream fis = new FileInputStream(input_file_name);
    
    int[] symbol_counts = new int[512];
    
    for (int n = 0; n < 64; n++) {
      for (int m = 0; m < 64; m++) {
        int pixel = fis.read();
        firstFrame[n][m] = pixel;
        symbol_counts[pixel] += 1;
      }
    }
    
    int next_pixel = fis.read();
    while (next_pixel != -1) {
	    for (int n = 0; n < 64; n++) {
	      for (int m = 0; m < 64; m++) {
	        symbol_counts[(firstFrame[n][m] - next_pixel + 256)] += 1;
	        firstFrame[n][m] = next_pixel;
	        next_pixel = fis.read();
	      }
	    }
    }


    fis.close();
    
    Integer[] symbols = new Integer[512];
    for (int i = 0; i < 512; i++) {
      symbols[i] = Integer.valueOf(i);
    }
    

    FreqCountIntegerSymbolModel model = new FreqCountIntegerSymbolModel(symbols, symbol_counts);
    
    ArithmeticEncoder<Integer> encoder = new ArithmeticEncoder(range_bit_width);
    
    FileOutputStream fos = new FileOutputStream(output_file_name);
    OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);
    

    for (int i = 0; i < 512; i++) {
      bit_sink.write(symbol_counts[i], 32);
    }
    

    fis = new FileInputStream(input_file_name);
    
    int firstSymbol = 0;
    for (int n = 0; n < 64; n++) {
      for (int m = 0; m < 64; m++) {
        firstSymbol = fis.read();
        //System.out.println(firstSymbol);
        firstFrame[n][m] = firstSymbol;
        encoder.encode(Integer.valueOf(firstSymbol), model, bit_sink);
      }
    }
    
    next_pixel = fis.read();
    //System.out.println("Next Pixel: " + next_pixel);
    
    int i = 0;
    while (i < restOfPix) {
	    for (int n = 0; n < 64; n++) {
	      for (int m = 0; m < 64; m++) {
	        //System.out.println(firstFrame[n][m] - next_pixel + 256);
	        encoder.encode(Integer.valueOf(firstFrame[n][m] - next_pixel + 256), model, bit_sink);
	        firstFrame[n][m] = next_pixel;
	        next_pixel = fis.read();
	        i++;
	      }
	    }
    }

    fis.close();

    encoder.emitMiddle(bit_sink);
    bit_sink.padToWord();
    fos.close();
    
    System.out.println("Done.");
  }
}

