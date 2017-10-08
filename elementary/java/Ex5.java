import java.util.Scanner;

public class Ex5 {
        public static void main(String[] args) {
                System.out.print("Number: ");
                Scanner scanner = new Scanner(System.in);
                int number = scanner.nextInt();
                int total = 0;
                for (int i = 1; i <= number; i++) {
                        if (i % 3 == 0 || i % 5 == 0) {
                                total += i;
                        }
                }
                System.out.println(Integer.toString(total));
        }
}
