package com.buaa.edu.domain.temp.resource;

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

    public TypeLib() {

    }

    public int GetWarnTypeInt(String str) {
        if (!warntype2int_lib.containsKey(str)) {
            int size = warntype2int_lib.size();
            warntype2int_lib.put(str, size);
            int2warntype_lib.put(size, str);
            return size;
        } else {
            return warntype2int_lib.get(str);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public static HashMap<Integer, String> getInt2warntype_lib() {
        return int2warntype_lib;
    }

    public String GetWarnTypeString(int value) {
        if (!int2warntype_lib.containsKey(value)) {
            return null;
            // Something should never happen
        } else {
            return int2warntype_lib.get(value);
        }
    }

    public int GetBoardTypeInt(String str) {
        if (!boardtype2int_lib.containsKey(str)) {
            int size = warntype2int_lib.size();
            boardtype2int_lib.put(str, size);
            int2boardtype_lib.put(size, str);
            return size;
        } else {
            return boardtype2int_lib.get(str);
        }
    }

    public String GetBoardTypeString(int value) {
        if (!int2boardtype_lib.containsKey(value)) {
            return null;
            // Something should never happen
        } else {
            return int2boardtype_lib.get(value);
        }
    }

    public int GetPairTypeInt(String str) {
        if (!pairtype2int_lib.containsKey(str)) {
            int size = pairtype2int_lib.size();
            pairtype2int_lib.put(str, size);
            int2pairtype_lib.put(size, str);
            return size;
        } else {
            return pairtype2int_lib.get(str);
        }
    }

    public String GetPairTypeString(int value) {
        if (!int2pairtype_lib.containsKey(value)) {
            return null;
            // Something should never happen
        } else {
            return int2pairtype_lib.get(value);
        }
    }

    public int GetPairTypeSize() {
        return int2pairtype_lib.size();
    }

    public int GetWarnTypeSize() {
        return int2warntype_lib.size();
    }

    public int GetBoardTypeSize() {
        return int2boardtype_lib.size();
    }
}
