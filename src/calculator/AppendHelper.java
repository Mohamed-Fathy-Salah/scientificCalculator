package calculator;

import java.util.TreeMap;

class AppendHelper {
    private TreeMap<Character, String> returnTree;
    private TreeMap<Character, String> multiplyTree;

    public static final int RETURN = 0;
    public static final int MULTIPLY = 1;
    public static final int ANOTHER = -1;

    AppendHelper() {
        returnTree = new TreeMap<>();
        multiplyTree = new TreeMap<>();
        fill();
    }

    private void fill() {
        returnTree.put('√', "+)!P");
        returnTree.put('P', "+)!P.π±");
        returnTree.put('!', "!");
        returnTree.put('.', ".");
        returnTree.put('+', "+!P");
        returnTree.put('(', "+!P√");
        returnTree.put('π', "!P");
        returnTree.put(' ', ".+)!P√");

        multiplyTree.put('!', "0.(");
        multiplyTree.put('0', "(π");
        multiplyTree.put('π', ".(π0");
        multiplyTree.put('.', "(π");
        multiplyTree.put(')',"0.π(");
    }

    public int check(char lastChar, char input) {
        if (returnTree.getOrDefault(lastChar, "").contains(Character.toString(input)))
            return RETURN;
        if (multiplyTree.getOrDefault(lastChar, "").contains(Character.toString(input)))
            return MULTIPLY;
        return ANOTHER;
    }
}
