import java.util.Random;
class Sort {
    public static void main(String[] args) {
        Random rand = new Random();
        int[] array = new int[20];
        array[0] = rand.nextInt(100);
        System.out.print(array[0]);
        for (int i = 1; i < array.length; i++) {
            array[i] = rand.nextInt(100);
            System.out.print(", ");
            System.out.print(array[i]);
        }
        System.out.println();
        
        insertion(array);
        System.out.print(array[0]);
        for (int i = 1; i < array.length; i++) {
            System.out.print(", ");
            System.out.print(array[i]);
        }
        System.out.println();
    }

    public static void insertion(int[] array) {
        for (int i = 1; i < array.length; i++) {
            int thingToInsert = array[i];
            int j;
            for (j = 0; j < i; j++) { //find index to insert into
                if (array[j] >= array[i]) {
                    break;
                }
            }
            for (int k = i; k > j; k--) { //shift values upward to make room;
                array[k] = array[k-1];
            }
            array[j] = thingToInsert;
        }
    }

    public static void selection(int[] array) {
        for (int i = 0; i < array.length-1; i++) {
            int min = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int j = i; j < array.length; j++) {
                if (array[j] < min) {
                    min = array[j];
                    minIndex = j;
                }
            }
            int t = array[i];
            array[i] = array[minIndex];
            array[minIndex] = t;
        }
    }

    public static void quickSort(int[] array, int start, int end) {
        int pivot = array[end];
        while (start < end) { //partition
            while (array[start] < pivot) {
                start++;
            }
            while(array[end] >= pivot) {
                end--;
            }
            int t = array[start];
            array[start] = array[end];
            array[end] = t;
        }

    }

}
