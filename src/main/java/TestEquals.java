import java.util.ArrayList;
import java.util.HashMap;

public class TestEquals {
    public int i;

    public TestEquals(int i) {
        this.i = i;
    }

    public boolean equals(Object o) {
        if (o instanceof TestEquals) {
            if (this.i == ((TestEquals)o).i) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        TestEquals test1 = new TestEquals(1);
        TestEquals test2 = new TestEquals(1);

        //ArrayList<TestEquals> list = new ArrayList<TestEquals>();

        //list.add(test1);
        HashMap<TestEquals, Integer> list = new HashMap<TestEquals, Integer>();
        list.put(test1, 1);
        if (list.get(test2) != null) {
            System.out.println("TRUE");
        } else {
            System.out.println("FALSE");
        }
    }
}
