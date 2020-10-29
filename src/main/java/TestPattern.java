import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPattern {
    public static void main(String[] args) {
        String pattern =
                "[\u4e00-\u9fa5]*(\\d+)[-]*((\\S|[ ]|[\u4e00-\u9fa5]))*[-](\\d+)[-]((\\S|[\u4e00-\u9fa5])+)[-](\\d+)";
        Pattern pat = Pattern.compile(pattern);

        String str = "0-5-X1L401-1(IN1/OUT1（12- ）)";
        Matcher matcher = pat.matcher(str);
        if (matcher.find()) {
            System.out.println("OK");
        }
    }
}
