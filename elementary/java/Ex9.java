import java.util.Scanner;
import java.util.Random;

public class Ex9 {
        public static void main(String[] args) {
                Scanner scanner = new Scanner(System.in);
                Random random = new Random();
                int number = random.nextInt(100) + 1;
                int tries = 0;
                int prevGuess = -1;
                boolean guessed = false;
                while (!guessed) {
                        System.out.print("Number: ");
                        int guess = scanner.nextInt();
                        if (guess < number) {
                                System.out.println("Too low");
                        }
                        else if (guess > number) {
                                System.out.println("Too high");
                        }
                        else {
                                System.out.println("Correct");
                                guessed = true;
                        }
                        if (! (guess == prevGuess) ) {
                                tries++;
                        }
                        prevGuess = guess;
                }
                System.out.println("Tries: " + tries);
        }
}
