package app;

import java.io.FileInputStream;

public class AnalyticsFile { 
	
  public static void main(String[] args) throws java.io.IOException {
    String input_file_name = "data/out.dat";
    

    System.out.println("Encoding text file: " + input_file_name);
    
    int num_pixels = (int)new java.io.File(input_file_name).length();
    
    System.out.println("Number of pixels: " + num_pixels);

    FileInputStream fis = new FileInputStream(input_file_name);
    int first = 0;
    int count1 = 0;
    for (int i = 0; i < 300; i++) {
      for (int n = 0; n < 64; n++) {
        for (int m = 0; m < 64; m++) {
          if ((i == 0) && (n == 0) && (m == 0)) {
            first = fis.read();
          }
          else {
            int next = fis.read();
            

            if ((first - next < 10) && (first - next > -10)) {
              count1++;
            }
            
            first = next;
          }
        }
      }
    }
    


    fis.close();
    

    FileInputStream fis2 = new FileInputStream(input_file_name);
    int count2 = 0;
    int[][] firstFrame = new int[64][64];
    
    for (int n = 0; n < 64; n++) {
      for (int m = 0; m < 64; m++) {
        firstFrame[n][m] = fis2.read();
      }
    }
    

    for (int i = 1; i < 300; i++) {
      for (int n = 0; n < 64; n++) {
        for (int m = 0; m < 64; m++) {
          int current = fis2.read();
          int diff = firstFrame[n][m] - current;
          

          if ((diff < 10) && (diff > -10)) {
            count2++;
          }
          
          firstFrame[n][m] = current;
        }
      }
    }
    

    fis2.close();
    fis.close();
    
    System.out.println("TEMPORAL: " + count2 + " / " + num_pixels);
    
    System.out.println("DIFFERENTIAL: " + count1 + " / " + num_pixels);
    
    System.out.println("Done");
  }
}
