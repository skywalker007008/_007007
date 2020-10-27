package read.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyTime {
    public int year;

    public int month;

    public int day;

    public int seconds;

    public MyTime (String time_format, boolean non_use) {

        String pattern = "(\\d+)[\u4e00-\u9fa5]+(\\d+)[\u4e00-\u9fa5]+(\\d+)[\u4e00-\u9fa5]+[ ]+(\\d+)[\u4e00-\u9fa5]+(\\d+)[\u4e00-\u9fa5]+(\\d+)[\u4e00-\u9fa5]+";

        Pattern pat = Pattern.compile(pattern);
        Matcher match = pat.matcher(time_format);
        if (match.find()) {
            this.year = Integer.parseInt(match.group(1));
            this.month = Integer.parseInt(match.group(2));
            this.day = Integer.parseInt(match.group(3));
            int hour = Integer.parseInt(match.group(4));
            int minute = Integer.parseInt(match.group(5));
            int second = Integer.parseInt(match.group(6));

            this.seconds = hour * 3600 + minute * 60 + second;
        }
    }

    public MyTime(String time_format) {
        String[] time_info = time_format.split(" ");

        String[] date_info = time_info[0].split("/");
        this.month = Integer.parseInt(date_info[0]);
        this.day = Integer.parseInt(date_info[1]);
        this.year = Integer.parseInt(date_info[2]);

        String[] clock_info = time_info[1].split("\\:");
        int hour = Integer.parseInt(clock_info[0]);
        int minute = Integer.parseInt(clock_info[1]);
        int second = Integer.parseInt(clock_info[2]);

        this.seconds = hour * 3600 + minute * 60 + second;
/*
        System.out.println("Year: " + year + " Month: " + month + " Day: " + day
                + " HH: " + hour + " MM: " + minute + " SS: " + second);
*/
    }

    public MyTime(int year, int month, int day, int seconds) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.seconds = seconds;
    }

    public int sub(MyTime sub_time) {
        int year = this.year - sub_time.year;
        int month = this.month - sub_time.month;
        int day = this.day - sub_time.day;
        int seconds = this.seconds - sub_time.seconds;


        int[] days = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
        // no date change
        if (year == 0 && month == 0 && day == 0) {
            return seconds;
        }
        // only day changes
        else if (year == 0 && month == 0 && day != 0) {
            return (day * 8760 + seconds);
        }
        // day & month change
        else if (year == 0 && month != 0) {
            if (month < 0) {
                return -sub_time.sub(this);
            } else {

                int days_total = 0;
                for (int mt = sub_time.month; mt < this.month; mt++) {
                    days_total += days[mt];
                    if (MyTime.IsYearSpecial(this.year) && mt == 2) {
                        days_total++;
                    }
                }
                days_total += day;
                return (days_total * 8640 + seconds);
            }
        }
        // even year changes
        else if (year != 0) {
            if (year < 0) {
                return -sub_time.sub(this);
            } else {
                System.out.println("Time so long");
                return 212789;
            }

        }

        else {
            System.out.println("Something wrong");
            return 212789;
        }


    }

    public static boolean IsYearSpecial(int year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                return (year % 400 == 0);
            } else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    public String PrintOut() {
        String str =  String.valueOf(year) + "/" +
                String.valueOf(month) + "/" +
                String.valueOf(day) + " " +
                String.valueOf(seconds / 3600) + ":" +
                String.valueOf((seconds % 3600) / 60) + ":" +
                String.valueOf(seconds % 60);

        return str;
    }
}
