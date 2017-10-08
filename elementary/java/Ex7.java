public class Ex7 {
        public static void main(String[] args) {
                for (int i = 1; i <= 12; i++) {
                        String row = "";
                        for (int j = 1; j <= 12; j++) {
                                int product = i*j;
                                String productString = Integer.toString(product);
                                int digits = productString.length();
                                for (int k = 1; k <= 4-digits; k++) {
                                        row += " ";
                                }
                                row += productString;
                        }
                        System.out.println(row);
                }
        }
}
