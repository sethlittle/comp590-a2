package app;

import ac.ArithmeticEncoder;
import io.OutputStreamBitSink;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class ContextAdaptiveACEncodeTextFile {  
  public static void main(String[] args) throws IOException {
    String input_file_name = "data/out.dat";
    String output_file_name = "data/context-adaptive-compressed-temporal.dat";
    
    int range_bit_width = 40;
    
    System.out.println("Encoding text file: " + input_file_name);
    System.out.println("Output file: " + output_file_name);
    System.out.println("Range Register Bit Width: " + range_bit_width);
    
    int num_pixels = (int)new File(input_file_name).length();
    
    System.out.println("Number of pixels: " + num_pixels);
    
    Integer[] pixelIntensities = new Integer['Ā'];
    for (int i = 0; i < 256; i++) {
      pixelIntensities[i] = Integer.valueOf(i);
    }
   
    FreqCountIntegerSymbolModel[] models = new FreqCountIntegerSymbolModel['Ā'];
    
    for (int i = 0; i < 256; i++)
    {
      models[i] = new FreqCountIntegerSymbolModel(pixelIntensities);
    }
    
    ArithmeticEncoder<Integer> encoder = new ArithmeticEncoder(range_bit_width);
    
    FileOutputStream fos = new FileOutputStream(output_file_name);
    OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);
    

    FileInputStream fis = new FileInputStream(input_file_name);
    

    FreqCountIntegerSymbolModel model = models[0];
    


    int firstFramePixels = 4096;
    int[] framePix = new int[firstFramePixels];
    int otherPixels = num_pixels - firstFramePixels;
    int next_pixel = fis.read();
    for (int i = 0; i < firstFramePixels; i++) {
      framePix[i] = next_pixel;
      encoder.encode(Integer.valueOf(next_pixel), model, bit_sink);
      next_pixel = fis.read();
    }
    
    for (int i = 0; i < otherPixels; i++) {
      encoder.encode(Integer.valueOf(next_pixel), model, bit_sink);
      

      model.addToCount(framePix[(i % firstFramePixels)]);
      

      model = models[framePix[(i % firstFramePixels)]];
      framePix[(i % firstFramePixels)] = next_pixel;
      next_pixel = fis.read();
    }

    System.out.println("TEMPORAL: 1090599 / " + num_pixels);
    
    System.out.println("DIFFERENTIAL: 748844 / " + num_pixels);
    
    fis.close();
    

    encoder.emitMiddle(bit_sink);
    bit_sink.padToWord();
    fos.close();
    
    System.out.println("Done");
  }
}
