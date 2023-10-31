public class old{
    public static void main(String[] args) {
        System.out.println("Generating random array.");
        Random random = new Random();
        unsorted = new Integer[SIZE];
        int i = 0;
        while (i < unsorted.length) {
            int j = random.nextInt(unsorted.length * 10);
            unsorted[i++] = j;
        }
        System.out.println("Generated random array.");
        System.out.println("Generating sorted array.");
        sorted = new Integer[SIZE];
        for (i = 0; i < sorted.length; i++) {
            sorted[i] = i;
        }
        System.out.println("Generated sorted array.");
        System.out.println("Generating reverse sorted array.");
        reverse = new Integer[SIZE];
        for (i = (reverse.length - 1); i >= 0; i--) {
            reverse[i] = (SIZE - 1) - i;
        }
        System.out.println("Generated reverse sorted array.");
        System.out.println();
        System.out.flush();
        System.out.println("Starting sorts...");
        System.out.println();
        System.out.flush();
        if (showInsertion) {
            // Insertion sort
            long bInsertion = System.nanoTime();
            Integer[] result = InsertionSort.sort(unsorted.clone());
            if (checkResults && !check(result))
                System.err.println("InsertionSort failed.");
            long aInsertion = System.nanoTime();
            double diff = (aInsertion - bInsertion) / 1000000d / 1000d;
            System.out.println("Random: InsertionSort=" + FORMAT.format(diff) + " secs");
            if (showResult)
                showResult(unsorted, result);
            if (showComparison)
                insertionResults[insertionCount++] = diff;
            putOutTheGarbage();

            bInsertion = System.nanoTime();
            result = InsertionSort.sort(sorted.clone());
            if (checkResults && !check(result))
                System.err.println("InsertionSort failed.");
            aInsertion = System.nanoTime();
            diff = (aInsertion - bInsertion) / 1000000d / 1000d;
            System.out.println("Sorted: InsertionSort=" + FORMAT.format(diff) + " secs");
            if (showResult)
                showResult(sorted, result);
            if (showComparison)
                insertionResults[insertionCount++] = diff;
            putOutTheGarbage();

            bInsertion = System.nanoTime();
            result = InsertionSort.sort(reverse.clone());
            if (checkResults && !check(result))
                System.err.println("InsertionSort failed.");
            aInsertion = System.nanoTime();
            diff = (aInsertion - bInsertion) / 1000000d / 1000d;
            System.out.println("Reverse sorted: InsertionSort=" + FORMAT.format(diff) + " secs");
            if (showResult)
                showResult(reverse, result);
            if (showComparison)
                insertionResults[insertionCount++] = diff;
            putOutTheGarbage();

            System.out.println();
            System.out.flush();
        }
    }
}
