import java.util.HashMap;

public class TestMap {
    public static void main(String[] args) {
        HashMap<String, HashMap<String, Integer>> main_map = new HashMap<String, HashMap<String, Integer>>();

        HashMap<String, Integer> test_map = new HashMap<String, Integer>();

        test_map.put("1", 1);
        test_map.put("2", 2);

        main_map.put("1", test_map);

        test_map = new HashMap<String, Integer>();

        test_map.put("1", 1);
        test_map.put("2", 2);
        test_map.put("3", 1);
        test_map.put("4", 2);

        main_map.put("2", test_map);

        System.out.println(main_map.get("1").size());
        System.out.println(main_map.get("2").size());
    }
}
