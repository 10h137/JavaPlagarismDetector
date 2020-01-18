package normalisation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapFile {


    static final HashMap<String, List<String>> replacement_map = new HashMap<>();

    static{
        List<String> map_implementations = Arrays.asList(new String[]{
                "HashMap", "TreeMap", "LinkedHashMap"
        });
        replacement_map.put("Map",map_implementations);


    }
}
