package comparison.algorithms;

public class MarkedArray {

    private final String[][] array;

    public MarkedArray(String text) {
        String[] split_text = text.split("\\w+");
        array = new String[2][split_text.length];
        array[0] = split_text;
    }

    public String get(int index) {
        if (index > array[0].length) return null;
        return array[0][index];
    }

    public boolean isMarked(int index) {
        return !array[1][index].isEmpty();
    }

    public void mark(int index) {
        array[1][index] = "*";
    }

    public int getNextUnmarkedTokenIndex(int index) {
        return distanceToNext(index, false);
    }

    public int getNextTileIndex(int index) {
        return distanceToNext(index, true);
    }

    public int size() {
        return array[0].length;
    }

    private int distanceToNext(int index, boolean marked) {
        int next_marked = -1;
        for (int i = index + 1; i < array[1].length; i++) {
            if (marked && !array[1][i].isEmpty()) {
                next_marked = i;
                break;
            }
        }
        return next_marked;
    }


}