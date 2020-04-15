package comparison.algorithms;

import com.github.s3curitybug.similarityuniformfuzzyhash.UniformFuzzyHash;
import normalisation.elements.elementContainers.JavaFile;

import java.util.HashMap;
import java.util.Map;

import static com.github.s3curitybug.similarityuniformfuzzyhash.UniformFuzzyHash.SimilarityTypes.ARITHMETIC_MEAN;

public class FingerprintComparison implements ComparisonAlgorithm {

    Map<Integer, UniformFuzzyHash> hashes = new HashMap<>();

    @Override
    public double compareFiles(JavaFile file1, JavaFile file2) {

        UniformFuzzyHash hash1;
        UniformFuzzyHash hash2;
        int obj_hash1 = file1.hashCode();
        int obj_hash2 = file2.hashCode();
        hash1 = hashes.containsKey(obj_hash1) ? hashes.get(obj_hash1) : new UniformFuzzyHash(file1.toString(), 5);
        hash2 = hashes.containsKey(obj_hash2) ? hashes.get(obj_hash2) : new UniformFuzzyHash(file2.toString(), 5);

        hashes.putIfAbsent(obj_hash1,hash1);
        hashes.putIfAbsent(obj_hash2,hash2);

        int file1_size = file1.length();
        int file2_size = file2.length();

        if (file1_size == 0 || file2_size == 0) return 0;

        return file1_size > file2_size ? hash2.similarity(hash1, ARITHMETIC_MEAN) : hash1.similarity(hash2,ARITHMETIC_MEAN);
    }


}
