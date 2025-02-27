public class CriticalTemperature1a {
    // done
    public static int minMeasurement(int k, int n) {

        int[][] dp = new int[k + 1][n + 1];

        for (int i = 0; i < k; i++) {
            dp[i][0] = 0;
        }
        for (int j = 0; j < n; j++) {
            dp[1][j] = j;

        }
        for (int i = 2; i <= k; i++) {
            for (int j = 1; j <= n; j++) {

                dp[i][j] = Integer.MAX_VALUE;

                for (int x = 1; x <= j; x++) {

                    int worstCase = Math.max(dp[i - 1][x - 1], dp[i][j - x]);
                    dp[i][j] = Math.min(dp[i][j], 1 + worstCase);
                }
            }
        }
        return dp[k][n];

    }

    public static void main(String[] args) {
        System.out.println("Minimum measurements (k=1, n=2):" + minMeasurement(1, 2));
        System.out.println("Minimum measurements (k=1, n=2):" + minMeasurement(2, 6));
        System.out.println("Minimum measurements (k=1, n=2):" + minMeasurement(3, 14));

    }

}
