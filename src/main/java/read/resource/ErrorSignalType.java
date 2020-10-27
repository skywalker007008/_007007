package read.resource;

public class ErrorSignalType {

    public static final int NOTICE_TYPE = 0;
    public static final int NOT_URGENCY_TYPE = 1;
    public static final int IMPORTANT_TYPE = 2;
    public static final int EMERGENCY_TYPE = 3;

    public String string_type;

    public int value_type;

    public int important_value;

    public ErrorSignalType(String string_type, int value_type, String import_value) {
        this.string_type = string_type;
        this.value_type = value_type;

        String not_urgency = new String("次要");
        String emergency = new String("紧急");
        String important = new String("重要");
        String notice = new String("提示");
        if (import_value.hashCode() == notice.hashCode()) {
            this.important_value = ErrorSignalType.NOTICE_TYPE;
        } else if (import_value.hashCode() == not_urgency.hashCode()) {
            this.important_value = ErrorSignalType.NOT_URGENCY_TYPE;
        } else if (import_value.hashCode() == important.hashCode()) {
            this.important_value = ErrorSignalType.IMPORTANT_TYPE;
        } else if (import_value.hashCode() == emergency.hashCode()) {
            this.important_value = ErrorSignalType.EMERGENCY_TYPE;
        }
        //this.important_value = import_value;
    }
}
