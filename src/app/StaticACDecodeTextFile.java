package app;

import ac.ArithmeticDecoder;
import ac.BinarySearchArithmeticDecoder;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class StaticACDecodeTextFile
{  
  public static void main(String[] args)
    throws InsufficientBitsLeftException, IOException
  {
    String input_file_name = "data/static-compressed-diff.dat";
    String output_file_name = "data/reout-diff.dat";
    
    FileInputStream fis = new FileInputStream(input_file_name);
    
    InputStreamBitSource bit_source = new InputStreamBitSource(fis);
    

    int[] symbol_counts = new int[512];
    Integer[] symbols = new Integer[512];
    
    for (int i = 0; i < 512; i++) {
      symbol_counts[i] = bit_source.next(32);
      symbols[i] = Integer.valueOf(i);
    }
    
    FreqCountIntegerSymbolModel model = 
      new FreqCountIntegerSymbolModel(symbols, symbol_counts);
    
    int num_pixels = 1228800;
    


    int range_bit_width = 40;
    BinarySearchArithmeticDecoder<Integer> decoder = new BinarySearchArithmeticDecoder(range_bit_width);
    

    System.out.println("Uncompressing file: " + input_file_name);
    System.out.println("Output file: " + output_file_name);
    System.out.println("Range Register Bit Width: " + range_bit_width);
    System.out.println("Number of symbols: " + num_pixels);
    
    FileOutputStream fos = new FileOutputStream(output_file_name);
    
    int firstSym = ((Integer)decoder.decode(model, bit_source)).intValue();
    fos.write(firstSym);
    for (int i = 0; i < num_pixels - 1; i++) {
      int next_symbol = ((Integer)decoder.decode(model, bit_source)).intValue();
      


      fos.write(firstSym - next_symbol + 256);
      firstSym = firstSym - next_symbol + 256;
    }
    
    System.out.println("Done.");
    fos.flush();
    fos.close();
    fis.close();
  }
}
