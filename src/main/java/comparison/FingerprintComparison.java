package comparison;

import java.io.*;
import java.util.*;

public class FingerprintComparison implements ComparisonAlgorithm {

    private Map<File, Set<Integer>> file_fingerprints;

    @Override
    public List<ComparisonResult> compareFiles(List<File> files) {
        int SUBSTRING_SIZE = 5;

        for (File file : files) {
            try {
                file_fingerprints.put(file, getFingerprints(file, SUBSTRING_SIZE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }


    private Set<Integer> getFingerprints(File file, int substring_size) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        Set<Integer> fingerprints = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        int c, k_counter = 0;
        while ((c = br.read()) != -1) {
            sb.append((char) c);
            k_counter++;
            if (k_counter == substring_size) {
                fingerprints.add(sb.toString().hashCode());
                sb = new StringBuilder();
                k_counter = 0;
            }
        }

        return fingerprints;
    }




}
