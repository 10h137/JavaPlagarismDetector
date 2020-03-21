package comparison.algorithms;

import normalisation.elements.elementContainers.JavaFile;

import java.util.*;

public class StringComparison implements ComparisonAlgorithm {


    public  List<Queue<Match>> match_list = new ArrayList<>();
    private  List<Match> tiles = new ArrayList<>();

    /**
     * Executes the Running-Karp-Rabin-Greedy-String-Tiling algorithm
     *
     * @param file1
     * @param file2
     * @return
     */
    public double compareFiles(JavaFile file1, JavaFile file2) {

        match_list.clear();
        tiles.clear();
        String longer = (file1.toString().length() > file2.toString().length()) ? file1.toString() : file2.toString();
        String shorter = (file1.toString().length() > file2.toString().length()) ? file2.toString() : file1.toString();
        tiles = RKR_GST(longer, shorter, 5, 20);

        MarkedArray T_vals = new MarkedArray(longer);
        MarkedArray P_vals = new MarkedArray(shorter);
        System.out.println("T size " + T_vals.size());
        System.out.println("P size " + P_vals.size());

        return getScore(T_vals, P_vals);


//        return 0d;
    }

    public List<Match> RKR_GST(String T, String P, int min_match_len, int initial_search_len) {


        if (min_match_len < 1)
            min_match_len = 3;

        if (initial_search_len < 5)
            initial_search_len = 20;

        int search_len = initial_search_len;
        MarkedArray T_vals = new MarkedArray(T);
        MarkedArray P_vals = new MarkedArray(P);
        Set<Integer> s = new HashSet<>();
        while (true) {
            if(s.contains(search_len))break;
            s.add(search_len);
            int l_max = scanPattern(T_vals, P_vals, search_len);
//            System.out.println("l-max" + l_max);
            if (l_max > (2 * search_len)) search_len = l_max;
            else {
                markStrings(T_vals, P_vals);
                if (search_len > 2 * min_match_len) search_len /= 2;
                else if (search_len > min_match_len) search_len = min_match_len;
                else break;
            }

        }

        return tiles;
    }

    public double getScore(MarkedArray P, MarkedArray T) {
        int coverage = tiles.stream().map(x -> x.len).reduce(Integer::sum).orElse(0);
        System.out.println("Coverage " + coverage);
        //((double) 2 * coverage) / ((double) (P.size() + T.size()));
        return ((double) coverage) / ((double) ( T.size())) ;
    }
//
//    private static int scanPattern(List<Queue<Match>> match_queues, MarkedArray TextString, MarkedArray PatternString, int search_len) {
//
//        int longest_match = 0;
//        Queue<Match> match_queue = new LinkedList<>();
//        CustomHashMap map = new CustomHashMap();
//
//        boolean no_next_tile = false;
//        int text_index = 0;
//        while (text_index < TextString.size() - 1) {
//            if (TextString.isMarked(text_index)) {
//                text_index++;
//                continue;
//            }
//
//            int distance_to_tile = TextString.getNextTileIndex(text_index) - text_index;
//            if (distance_to_tile < 0) {
//                distance_to_tile = TextString.size() - text_index;
//                no_next_tile = true;
//            }
//
//            if (distance_to_tile < search_len) {
//                if (no_next_tile) {
//                    text_index = TextString.size();
//                } else {
//                    text_index = TextString.getNextUnmarkedTokenIndex(text_index);
//                    if (text_index == -1) text_index = TextString.size();
//                }
//            } else {
//
//                map.add(generateHash(TextString.getString(text_index, text_index + search_len - 1)), text_index);
//                text_index++;
//            }
//
//        }
//
//
//        no_next_tile = false;
//        int pattern_index = 0;
//        while (pattern_index < PatternString.size()) {
//
//            if (PatternString.isMarked(pattern_index)) {
//                pattern_index++;
//                continue;
//            }
//
//            int distance_to_tile = PatternString.getNextTileIndex(pattern_index) - pattern_index;
//            if (distance_to_tile < 0) {
//                distance_to_tile = PatternString.size() - pattern_index;
//                no_next_tile = true;
//            }
//
//            if (distance_to_tile < search_len) {
//                if (no_next_tile) {
//                    pattern_index = PatternString.size();
//                } else {
//                    pattern_index = PatternString.getNextUnmarkedTokenIndex(pattern_index);
//                    if (pattern_index < 0) {
//                        pattern_index = PatternString.size();
//                    }
//                }
//            } else {
//
//                String str = PatternString.getString(pattern_index, pattern_index + search_len - 1);
//                int hash = generateHash(str);
//                List<Integer> vals = map.get(hash);
//                if (vals != null) {
//                    for (Integer val : vals) {
//                        String T_string = TextString.getString(val, val + search_len - 1);
//                        if (T_string.equals(str)) {
//                            int T_index = val;
//                            int new_search_len = search_len;
//
//                            while (pattern_index + new_search_len < PatternString.size() && T_index + new_search_len < TextString.size()
//                                    && PatternString.get(pattern_index + new_search_len).equals(TextString.get(T_index + new_search_len))
//                                    && !PatternString.isMarked(pattern_index + new_search_len)
//                                    && !TextString.isMarked(T_index + new_search_len)) {
//                                new_search_len++;
//                            }
//
//                            if (new_search_len > 2 * search_len) return new_search_len;
//                            else {
//                                if (longest_match < search_len) {
//                                    longest_match = search_len;
//                                }
//                                Match match = new Match(pattern_index, T_index, new_search_len);
//                                match_queue.add(match);
//                            }
//                        }
//
//                    }
//                }
//
//                pattern_index++;
//
//            }
//
//
//        }
//
//
//        if (match_queue.isEmpty()) {
//            match_list.add(match_queue);
//        }
//        return longest_match;
//    }

    private void markStrings(MarkedArray T, MarkedArray P) {
        for (Queue<Match> queue : match_list) {
            while (!queue.isEmpty()) {
                Match match = queue.poll();
                if (!isOccluded(match, tiles)) {
                    for (int j = 0; j < match.len; j++) {
                        P.mark(match.p_pos + j);
                        T.mark(match.s_pos + j);
                    }
                    tiles.add(match);
                }
            }
        }
        match_list.clear();
    }

    private static int generateHash(String str) {
        int hashValue = 0;
        for (int i = 0; i < str.length(); i++) {
            hashValue = ((hashValue << 1) + (int) str.charAt(i));
        }
        return hashValue;
    }

    private boolean isOccluded(Match match, List<Match> tiles) {
        for (Match tile : tiles) {
            return (Math.max(match.p_pos, tile.p_pos) <= Math.min(match.p_pos + match.len, tile.p_pos + tile.len)
                    ||
                    Math.max(match.s_pos, tile.s_pos) <= Math.min(match.s_pos + match.len, tile.s_pos + tile.len)
            );

        }
        return false;
    }

//    private  boolean isOccluded(Match match, List<Match> tiles) {
//        if(tiles.equals(null) || tiles == null || tiles.size() == 0)
//            return false;
//        for (Match matches : tiles) {
//            if ((matches.p_pos + matches.len == match.p_pos
//                    + match.len)
//                    && (matches.s_pos + matches.len == match.s_pos
//                    + match.len))
//                return true;
//        }
//        return false;
//    }

    private int scanPattern(MarkedArray text_string, MarkedArray pattern_string, int search_len){
        CustomHashMap map = new CustomHashMap();

        scan(text_string, pattern_string, true, search_len, map);
        return scan(text_string, pattern_string, false, search_len, map);
    }

    private int scan(MarkedArray text_string, MarkedArray pattern_string, boolean text, int search_len, CustomHashMap map) {


        int longest_match = 0;
        Queue<Match> match_queue = new LinkedList<>();
        MarkedArray arr = text ? text_string : pattern_string;
//
//        System.out.println(pattern_string.size());
//        System.out.println(text_string.size());

        boolean no_next_tile = false;
        int current_index = 0;
        while (current_index < arr.size() - 1) {
            if (arr.isMarked(current_index)) {
                current_index++;
                continue;
            }

            int distance_to_tile = arr.distanceToNextTile(current_index);
            if (distance_to_tile < 0) {
                distance_to_tile = arr.size() - current_index;
                no_next_tile = true;
            }

            if (distance_to_tile < search_len) {
                if (no_next_tile) {
                    current_index = arr.size();
                } else {
                    current_index = arr.getNextUnmarkedTokenIndex(current_index);
                    if (current_index < 0) current_index = arr.size();
                }
            } else {
                if (text) {
                    // if the text string is being processed
                    map.add(generateHash(arr.getString(current_index, current_index + search_len - 1)), current_index);
                    current_index++;
                } else {
                    // if the pattern string is being processed
                    String pattern_substring = arr.getString(current_index, current_index + search_len - 1);
                    int hash = generateHash(pattern_substring);
                    List<Integer> indexes = map.get(hash);
                    if (indexes != null) {
                        for (Integer start_index : indexes) {
                            String text_substring = text_string.getString(start_index, start_index + search_len - 1);
                            if (text_substring.equals(pattern_substring)) {
                                int text_index = start_index;
                                int new_search_len = search_len;

                                while (current_index + new_search_len < arr.size() && text_index + new_search_len < text_string.size()
                                        && arr.get(current_index + new_search_len).equals(text_string.get(text_index + new_search_len))
                                        && !arr.isMarked(current_index + new_search_len)
                                        && !text_string.isMarked(text_index + new_search_len)) {
                                    new_search_len++;
                                }

                                if (new_search_len > 2 * search_len) return new_search_len;
                                else {
                                    if (longest_match < search_len) longest_match = search_len;
                                    match_queue.add(new Match(current_index, text_index, new_search_len));
                                }
                            }

                        }
                    }

                    current_index++;
                }
            }

        }

        if (!match_queue.isEmpty() && !text) {
            match_list.add(match_queue);
        }
        return longest_match;
    }


}

