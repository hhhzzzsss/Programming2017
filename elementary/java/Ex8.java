public class Ex8 {
        public static void main(String[] args) {
                int number = 2;
                while (true) {
                        boolean prime = true;
                        for (int divisor = ; divisor < number; divisor++) {
                                if (number % divisor == 0) {
                                        prime = false;
                                }
                        }
                        if (prime) {
                                System.out.println(Integer.toString(number));
                        }
                        number++;
                }
        }
}
