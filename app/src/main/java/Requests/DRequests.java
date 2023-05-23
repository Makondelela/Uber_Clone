package Requests;

import java.util.ArrayList;
import java.util.List;

public class DRequests {
    private static List<String> strings = new ArrayList<>();

    public static void addString(int index, String string) {
        strings.add(index, string);
    }

    public static List<String> getStrings(int index1, int index2) {
        return strings.subList(index1, index2+1);
    }
}