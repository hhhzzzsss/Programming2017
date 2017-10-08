public class Ex11 {
        public static void main(String[] args) {
                double total = 0.0;
                for (double k = 1.0; k <= 1000000.0; k++) {
                        total += Math.pow(-1.0, k+1.0) / (2.0*k - 1.0);
                }
                total *= 4.0;
                System.out.println(Double.toString(total));
        }
}
