import java.util.ArrayList;

public class TestArraylist {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(0,3);
        list.add(0,4);
        for (int i:
             list) {
            System.out.println(i);
        }
    }
}
