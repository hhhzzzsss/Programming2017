
import java.util.Scanner;

public class Ex6 {
        public static void main(String[] args) {
                System.out.print("Number: ");
                Scanner scanner = new Scanner(System.in);
                int number = scanner.nextInt();
                System.out.print("sum or product: ");
                String ans = scanner.next();
                
                if (ans.equals("sum")) {
                        int total = 0;
                        for (int i = 1; i <= number; i++) {
                                total += i;
                        }
                        System.out.println(Integer.toString(total));
                }
                else if (ans.equals("product")) {
                        int total = 1;
                        for (int i = 1; i <= number; i++) {
                                total *= i;
                        }
                        System.out.println(Integer.toString(total));
                }

        }
}
