package resource;

import java.util.HashMap;

/*
 * Stores all kinds of types that related to warnings
 */
public class TypeLib {

    private static final HashMap<String, Integer> warntype2int_lib = new HashMap<String, Integer>();

    private static final HashMap<Integer, String> int2warntype_lib = new HashMap<Integer, String>();

    private static final HashMap<String, Integer> boardtype2int_lib = new HashMap<String, Integer>();

    private static final HashMap<Integer, String> int2boardtype_lib = new HashMap<Integer, String>();

    private static final HashMap<String, Integer> pairtype2int_lib  = new HashMap<String, Integer>();

    private static final HashMap<Integer, String> int2pairtype_lib = new HashMap<Integer, String>();

    public static int GetWarnTypeInt(String str) {
        if (!warntype2int_lib.containsKey(str)) {
            int size = warntype2int_lib.size();
            warntype2int_lib.put(str, size);
            int2warntype_lib.put(size, str);
            return size;
        } else {
            return warntype2int_lib.get(str);
        }
    }

    public static String GetWarnTypeString(int value) {
        if (!int2warntype_lib.containsKey(value)) {
            return null;
            // Something should never happen
        } else {
            return int2warntype_lib.get(value);
        }
    }

    public static int GetBoardTypeInt(String str) {
        if (!boardtype2int_lib.containsKey(str)) {
            int size = warntype2int_lib.size();
            boardtype2int_lib.put(str, size);
            int2boardtype_lib.put(size, str);
            return size;
        } else {
            return boardtype2int_lib.get(str);
        }
    }

    public static String GetBoardTypeString(int value) {
        if (!int2boardtype_lib.containsKey(value)) {
            return null;
            // Something should never happen
        } else {
            return int2boardtype_lib.get(value);
        }
    }

    public static int GetPairTypeInt(String str) {
        if (!pairtype2int_lib.containsKey(str)) {
            int size = pairtype2int_lib.size();
            pairtype2int_lib.put(str, size);
            int2pairtype_lib.put(size, str);
            return size;
        } else {
            return pairtype2int_lib.get(str);
        }
    }

    public static String GetPairTypeString(int value) {
        if (!int2pairtype_lib.containsKey(value)) {
            return null;
            // Something should never happen
        } else {
            return int2pairtype_lib.get(value);
        }
    }

    public static int GetPairTypeSize() {
        return int2pairtype_lib.size();
    }

    public static int GetWarnTypeSize() {
        return int2warntype_lib.size();
    }

    public static int GetBoardTypeSize() {
        return int2boardtype_lib.size();
    }
}
