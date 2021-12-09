import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MakeShards {
  private static final int SHARD_SIZE = 100;

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: MakeShards [input file] [output folder]");
      return;
    }

    File check1 = new File(args[1]);
    boolean directoryCheck = check1.exists();

    if(directoryCheck) {

      String[]entries = check1.list();
      for(String s: entries){
        File currentFile = new File(check1.getPath(),s);
        currentFile.delete();
      }
      boolean success = check1.delete();
      System.out.println("The original existing directory is deleted... " + success);
    }

    Path input = Path.of(args[0]);
    Path outputFolder = Files.createDirectory(Path.of(args[1]));


    try (BufferedReader reader = Files.newBufferedReader(input)) {
      int shardNum = 0;
      String word = reader.readLine();
      while (word != null) {
        List<String> shard = new ArrayList<>(SHARD_SIZE);
        while (shard.size() < SHARD_SIZE && word != null) {
          shard.add(word);
          word = reader.readLine();
        }
        shard.sort(String::compareTo);
        Path output = Path.of(outputFolder.toString(), getOutputFileName(shardNum));
        try (Writer writer = Files.newBufferedWriter(output)) {
          for (int i = 0; i < shard.size(); i++) {
            writer.write(shard.get(i));
            if (i < shard.size() - 1) {
              writer.write(System.lineSeparator());
            }
          }
        }
        shardNum++;
      }
    }
  }

  private static String getOutputFileName(int shardNum) {
    return String.format("shard%02d.txt", shardNum);
  }
}
