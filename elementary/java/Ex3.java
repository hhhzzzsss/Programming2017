import java.util.Scanner;

public class Ex3 {
        public static void main(String[] args) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Name: ");
                String name = scanner.next();
                if (name.equals("Alice") || name.equals("Bob")) {
                        System.out.println("Hello, " + name);
                }
                else {
                        System.out.println("Hello");
                }
        }
}
