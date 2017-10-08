public class Ex10 {
        public static void main(String[] args) {
                int year = 2018;
                int leapyears = 0;
                while (leapyears < 20) {
                        if (year%4==0 && (year%100!=0 || year%400==0)) {
                                System.out.println(Integer.toString(year));
                                leapyears++;
                        }
                        year++;
                }
        }
}
