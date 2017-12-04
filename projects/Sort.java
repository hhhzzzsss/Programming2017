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
        
        //selection(array);
        mergeSort(array, 0, array.length-1);
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
        System.out.println(end-start);
        if (end-start == 1) {
            if (array[start]>array[end]) {
                int t = array[start];
                array[start] = array[end];
                array[end] = t;
            }
            return;
        }
        else if (end-start == 0) {
            return;
        }
        int r1 = start;
        int r2 = end;
        int pivot = array[end];
        start--;
        end++;
        while (true) { //partition
            start++;
            while (array[start] < pivot) {
                start++;
            }
            end--;
            while(array[end] > pivot) {
                end--;
            }
            if (start>=end) {
                break;
            }
            int t = array[start];
            array[start] = array[end];
            array[end] = t;
        }
        quickSort(array, r1, start-1);
        quickSort(array, start, r2);
    }
    
    public static void mergeSort(int[] array, int start, int end) {
        if (end==start) {
            return;
        }
        int midpoint = start + (end-start)/2;
        mergeSort(array, start, midpoint);
        mergeSort(array, midpoint+1, end);
        int length = end-start + 1;
        int[] buffer = new int[length];
        int i = 0;
        int i1 = start;
        int i2 = midpoint+1;
        while(i < length) {
            if (!(i2 <= end) || i1 <= midpoint && array[i1] < array[i2]) {
                buffer[i] = array[i1];
                i1++;
            }
            else {
                buffer[i] = array[i2];
                i2++;
            }
            i++;
        }
        for (i = 0; i < length; i++) {
            array[start+i] = buffer[i];
        }
    }

}
