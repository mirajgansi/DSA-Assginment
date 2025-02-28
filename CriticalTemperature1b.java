import java.util.PriorityQueue;

public class CriticalTemperature1b {
    // done
    public static int findMinMeasurements(int k, int n) {
        int[][] dp = new int[k + 1][n + 1];
        int attempts = 0;

        // Initialize the dp table
        while (dp[k][attempts] < n) {
            attempts++;
            for (int i = 1; i <= k; i++) {
                dp[i][attempts] = dp[i - 1][attempts - 1] + dp[i][attempts - 1] + 1;
            }
        }

        return attempts;
    }

    public static int findKthLowestReturn(int[] returns1, int[] returns2, int k) {
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        for (int i = 0; i < returns1.length; i++) {
            minHeap.offer(new int[] { returns1[i] * returns2[0], i, 0 });
        }
        int result = 0;
        while (k-- > 0) {
            int[] current = minHeap.poll();
            result = current[0];
            int i = current[1], j = current[2];

            if (j + 1 < returns2.length) {
                minHeap.offer(new int[] { returns1[i] * returns2[j + 1], i, j + 1 });
            }
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(findMinMeasurements(1, 2));
        System.out.println(findMinMeasurements(2, 6));
        System.out.println(findMinMeasurements(3, 14));

        int[] returns1 = { 2, 5 };
        int[] returns2 = { 3, 4 };
        System.out.println(findKthLowestReturn(returns1, returns2, 2));

        int[] returns3 = { -4, -2, 0, 3 };
        int[] returns4 = { 2, 4 };
        System.out.println(findKthLowestReturn(returns3, returns4, 6));
    }
}