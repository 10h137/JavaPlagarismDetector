package normalisation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


class NormaliserTest {

    public static void main(String[] args) {
        java.util.List<String> lines = null;
        System.out.println("jhhbhjb");

        try {
            lines = Files.readAllLines(Paths.get("src/main/java/normalisation/test.java"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Normaliser.reduceStructures(lines);
      lines.forEach(System.out::println);
    }

}