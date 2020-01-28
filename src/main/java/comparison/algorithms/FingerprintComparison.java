package comparison.algorithms;

import com.github.s3curitybug.similarityuniformfuzzyhash.UniformFuzzyHash;
import normalisation.elements.elementContainers.JavaFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FingerprintComparison implements ComparisonAlgorithm {

    private Map<File, Set<Integer>> file_fingerprints;

    @Override
    public double compareFiles(JavaFile file1, JavaFile file2) {
        int SUBSTRING_SIZE = 5;

        UniformFuzzyHash hash1 = new UniformFuzzyHash(file1.toString(), 5);
        UniformFuzzyHash hash2 = new UniformFuzzyHash(file2.toString(), 5);

        int file1_size = file1.length();
        int file2_size = file2.length();


        return file1_size > file2_size ? hash2.similarity(hash1) : hash1.similarity(hash2);
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
