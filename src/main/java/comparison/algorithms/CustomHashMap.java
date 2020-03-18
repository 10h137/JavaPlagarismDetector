package comparison.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomHashMap {

    private final Map<Integer, List<Integer>> map = new HashMap<>();

    public List<Integer> get(int length) {
        return map.getOrDefault(length, null);
    }

    public void add(int size, int hash) {
        map.computeIfAbsent(size, x -> new ArrayList<>()).add(hash);
    }


}
