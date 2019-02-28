package app;

import ac.ArithmeticEncoder;
import io.OutputStreamBitSink;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class StaticACEncodeTextFile
{
  public StaticACEncodeTextFile() {}
  
  public static void main(String[] args)
    throws IOException
  {
    String input_file_name = "data/out.dat";
    String output_file_name = "data/static-compressed-diff.dat";
    
    int range_bit_width = 40;
    
    System.out.println("Encoding text file: " + input_file_name);
    System.out.println("Output file: " + output_file_name);
    System.out.println("Range Register Bit Width: " + range_bit_width);
    
    int num_pixels = (int)new File(input_file_name).length();
    

    FileInputStream fis = new FileInputStream(input_file_name);
    
    int[] symbol_counts = new int[512];
    
    int start = fis.read();
    symbol_counts[start] += 1;
    int next_byte = fis.read();
    
    while (next_byte != -1) {
      symbol_counts[(start - next_byte + 256)] += 1;
      start = next_byte;
      next_byte = fis.read();
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
    
    int first_symbol = fis.read();
    encoder.encode(Integer.valueOf(first_symbol), model, bit_sink);
    for (int i = 0; i < num_pixels - 1; i++) {
      int next_symbol = fis.read();
      


      encoder.encode(Integer.valueOf(first_symbol - next_symbol + 256), model, bit_sink);
      first_symbol = next_symbol;
    }
    fis.close();
    



    encoder.emitMiddle(bit_sink);
    bit_sink.padToWord();
    fos.close();
    
    System.out.println("Done.");
  }
}

