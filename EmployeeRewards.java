import java.util.PriorityQueue;
import java.util.Arrays;

//done
public class EmployeeRewards {
    // Function to find the minimum number of measurements required
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

    // Function to find the kth lowest combined return
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

    // Function to determine the minimum number of rewards needed
    public static int minRewards(int[] ratings) {
        int n = ratings.length;
        int[] rewards = new int[n];
        Arrays.fill(rewards, 1); // Every employee gets at least 1 reward

        // Left to right pass: Ensure increasing ratings get more rewards
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                rewards[i] = rewards[i - 1] + 1;
            }
        }

        // Right to left pass: Ensure decreasing ratings get more rewards
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }

        // Sum up total rewards needed
        int totalRewards = 0;
        for (int reward : rewards) {
            totalRewards += reward;
        }

        return totalRewards;
    }

    // Main function to test the implementation
    public static void main(String[] args) {
        System.out.println(findMinMeasurements(1, 2)); // Output: 2
        System.out.println(findMinMeasurements(2, 6)); // Output: 3
        System.out.println(findMinMeasurements(3, 14)); // Output: 4

        int[] returns1 = { 2, 5 };
        int[] returns2 = { 3, 4 };
        System.out.println(findKthLowestReturn(returns1, returns2, 2)); // Output: 8

        int[] returns3 = { -4, -2, 0, 3 };
        int[] returns4 = { 2, 4 };
        System.out.println(findKthLowestReturn(returns3, returns4, 6)); // Output: 0

        int[] ratings1 = { 1, 0, 2 };
        System.out.println(minRewards(ratings1)); // Output: 5

        int[] ratings2 = { 1, 2, 2 };
        System.out.println(minRewards(ratings2)); // Output: 4
    }
}