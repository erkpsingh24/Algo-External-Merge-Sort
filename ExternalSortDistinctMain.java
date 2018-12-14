import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ExternalMergeSort {

  private static int iteration = 0;
  private static int localPassFileCounter = 0;

  public static void main(String args[]) {
    try {
      File result = mergeSort(divideFileIntoChunks("input-file-path/input.txt")).get(0);
      distinctValues(result, "output-file-path/finaloutput.txt");
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static List<File> divideFileIntoChunks(String fileLocation) throws IOException {
    List<File> chunkFiles = new LinkedList<File>();

    BufferedReader br = new BufferedReader(new FileReader(new File(fileLocation)));
    int chunkSize = 10; //Assuming we have enough memory to store 10 lines
    String line = br.readLine();
    int i = 0;
    int chunkId = 0;
    List<String> tempList = new ArrayList<String>();
    while (line != null) {
      tempList.add(line);
      i++;
      line = br.readLine();
      if (i >= chunkSize) {
        i = 0;
        Collections.sort(tempList);
        File newFile = new File("temp-file-path/TEMP_FILE_" + chunkId);
        newFile.deleteOnExit();
        BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
        chunkFiles.add(newFile);
        for (String s : tempList) {
          bw.write(s);
          bw.newLine();
        }
        bw.close();
        chunkId++;
        tempList.clear();
      }
    }
    br.close();
    return chunkFiles;
  }

  private static File distinctValues(File inputFile, String outputFileName) throws IOException {
    File outputfile = null;
    BufferedReader br = null;
    BufferedWriter bw = null;
    outputfile = new File(outputFileName);
    br = new BufferedReader(new FileReader(inputFile));
    bw = new BufferedWriter(new FileWriter(outputfile));
    String str = null;
    String previousStr = null;
    str = br.readLine();
    while (str != null) {
      if (!str.equals(previousStr)) {
        bw.write(str);
        bw.newLine();
        previousStr = str;
      }
      str = br.readLine();
    }
    bw.close();
    br.close();

    return outputfile;
  }

  private static List<File> mergeSort(List<File> chunkFiles) throws IOException {
    if (chunkFiles.size() == 1) {
      return chunkFiles;
    }
    chunkFiles = mergeSort(merge(chunkFiles));
    return chunkFiles;
  }

  private static List<File> merge(List<File> chunkFiles) throws IOException {
    int i = 0;
    List<File> newChunkFileList = new ArrayList<>();
    while (i < chunkFiles.size() - 1) {
      //Merging the elements of File array by pairs
      newChunkFileList.add(mergeTwoFiles(chunkFiles.get(i), chunkFiles.get(i + 1)));
      i = i + 2;
      localPassFileCounter++;
    }
    if (chunkFiles.size() % 2 != 0) { // Merging the last unpaired value in the new array
      File file1 = newChunkFileList.remove(newChunkFileList.size() - 1);
      newChunkFileList.add(mergeTwoFiles(file1, chunkFiles.get(chunkFiles.size() - 1)));
    }
    localPassFileCounter = 0;
    iteration++;
    return newChunkFileList;
  }

  private static File mergeTwoFiles(File first, File second) throws IOException {
    File file = null;
    file = new File("/Users/karanpreetsingh/Documents/ExternalMergeSort/TEMP_FILE_" + iteration + "_" + localPassFileCounter + ".txt");
    file.deleteOnExit();
    if ((first == null) || second == null)
      return file;
    BufferedReader reader1 = new BufferedReader(new FileReader(first));
    BufferedReader reader2 = new BufferedReader(new FileReader(second));
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));

    String str1 = reader1.readLine();
    String str2 = reader2.readLine();
    while (str1 != null || str2 != null) {
      if (str1 != null && str2 != null) {
        if (str1.compareTo(str2) <= 0) {
          writer.write(str1);
          str1 = reader1.readLine();
        }
        else {
          writer.write(str2);
          str2 = reader2.readLine();
        }
        writer.newLine();
      }
      if (str1 != null && str2 == null) { //Leftover entries from file 1
        writer.write(str1);
        str1 = reader1.readLine();
        writer.newLine();
      }
      if (str2 != null && str1 == null) { //Leftover entries from file 2
        writer.write(str2);
        str2 = reader2.readLine();
        writer.newLine();
      }
    }
    reader1.close();
    reader2.close();
    writer.close();
    return file;
  }
}
