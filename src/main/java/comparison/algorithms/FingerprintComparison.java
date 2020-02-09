package comparison.algorithms;

import com.github.s3curitybug.similarityuniformfuzzyhash.UniformFuzzyHash;
import normalisation.elements.elementContainers.JavaFile;

public class FingerprintComparison implements ComparisonAlgorithm {

    @Override
    public double compareFiles(JavaFile file1, JavaFile file2) {

        UniformFuzzyHash hash1 = new UniformFuzzyHash(file1.toString(), 5);
        UniformFuzzyHash hash2 = new UniformFuzzyHash(file2.toString(), 5);

        int file1_size = file1.length();
        int file2_size = file2.length();

        System.out.println(file1.toString() + "\n\n\n\n");
        System.out.println(file2.toString());

        return file1_size > file2_size ? hash2.similarity(hash1) : hash1.similarity(hash2);
    }


}
