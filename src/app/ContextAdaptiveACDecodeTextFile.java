package app;

import ac.ArithmeticDecoder;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ContextAdaptiveACDecodeTextFile
{  
  public static void main(String[] args) throws InsufficientBitsLeftException, IOException {
    String input_file_name = "data/context-adaptive-compressed.dat";
    String output_file_name = "data/reout.dat";
    
    FileInputStream fis = new FileInputStream(input_file_name);
    
    InputStreamBitSource bit_source = new InputStreamBitSource(fis);
    
    Integer[] symbols = new Integer[512];
    
    for (int i = 0; i < 256; i++) {
      symbols[i] = Integer.valueOf(i);
    }
    
    int num_pixels = 1228800;

    FreqCountIntegerSymbolModel[] models = new FreqCountIntegerSymbolModel[512];
    
    for (int i = 0; i < 256; i++)
    {
      models[i] = new FreqCountIntegerSymbolModel(symbols);
    }


    int range_bit_width = 40;
    ArithmeticDecoder<Integer> decoder = new ArithmeticDecoder(range_bit_width);

    System.out.println("Uncompressing file: " + input_file_name);
    System.out.println("Output file: " + output_file_name);
    System.out.println("Range Register Bit Width: " + range_bit_width);
    System.out.println("Number of encoded symbols: " + num_pixels);
    
    FileOutputStream fos = new FileOutputStream(output_file_name);
    

    FreqCountIntegerSymbolModel model = models[0];
    
    int firstFramePixels = 4096;
    int[] framePix = new int[firstFramePixels];
    int otherPixels = num_pixels - firstFramePixels;
    for (int i = 0; i < firstFramePixels; i++) {
      int sym = ((Integer)decoder.decode(model, bit_source)).intValue();
      framePix[i] = sym;
      fos.write(sym);
    }
    for (int i = 0; i < otherPixels; i++) {
      int sym = ((Integer)decoder.decode(model, bit_source)).intValue();
      fos.write(sym);
      

      model.addToCount(framePix[(i % firstFramePixels)]);
      

      model = models[framePix[(i % firstFramePixels)]];
      framePix[(i % firstFramePixels)] = sym;
    }
    

    System.out.println("Done.");
    fos.flush();
    fos.close();
    fis.close();
  }
}