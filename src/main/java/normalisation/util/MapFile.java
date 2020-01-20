package normalisation.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapFile {


    public static final HashMap<String, List<String>> replacement_map = new HashMap<>();

    static {
        List<String> map_implementations = Arrays.asList("HashMap", "TreeMap", "LinkedHashMap", "ConcurrentHashMap", "ConcurrentSkipListMap",
                "EnumMap", "HashTable", "IdentityHashMap", "WeakHashMap");
        replacement_map.put("Map", map_implementations);

        List<String> set_implementations = Arrays.asList("AbstractSet", "ConcurrentSkipListSet", "CopyOnWriteArraySet", "EnumSet", "HashSet",
                "LinkedHashSet", "TreeSet");
        replacement_map.put("Set", set_implementations);

        List<String> list_implementations = Arrays.asList("AbstractList", "AbstractSequentialList", "ArrayList", "AttributeList", "CopyOnWriteArrayList",
                "LinkedList", "RoleList", "Stack", "Vector", "RoleUnresolvedList");
        replacement_map.put("List", list_implementations);

        List<String> integer_implementations = Arrays.asList("int", "long", "Long", "AtomicInteger", "AtomicLong", "BigInteger",
                "Byte", "Short");
        replacement_map.put("Integer", integer_implementations);

        List<String> decimal_implementations = Arrays.asList("double", "Double", "float", "Float", "BigDecimal");
        replacement_map.put("Decimal", decimal_implementations);


    }
}
