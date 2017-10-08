import java.util.Scanner;

public class Ex4 {
        public static void main(String[] args) {
                System.out.print("Number: ");
                Scanner scanner = new Scanner(System.in);
                int number = scanner.nextInt();
                int total = 0;
                for (int i = 1; i <= number; i++) {
                        total += i;
                }
                System.out.println(Integer.toString(total));
        }
}
