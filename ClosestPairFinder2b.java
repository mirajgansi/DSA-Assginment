import java.util.Arrays;

//done
public class ClosestPairFinder2b {
    public static int[] findClosestPair(int[] x_coords, int[] y_coords) {
        int n = x_coords.length;
        int minDistance = Integer.MAX_VALUE;
        int[] result = new int[2];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int distance = Math.abs(x_coords[i] - x_coords[j]) + Math.abs(y_coords[i] - y_coords[j]);
                if (distance < minDistance
                        || (distance == minDistance && (i < result[0] || (i == result[0] && j < result[1])))) {
                    minDistance = distance;
                    result[0] = i;
                    result[1] = j;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] x_coords1 = { 1, 2, 3 };
        int[] y_coords1 = { 2, 3, 4 };
        int[] result1 = findClosestPair(x_coords1, y_coords1);
        System.out.println("Closest pair indices: [" + result1[0] + ", " + result1[1] + "]");

        int[] x_coords2 = { 1, 1, 2, 2 };
        int[] y_coords2 = { 1, 2, 1, 2 };
        int[] result2 = findClosestPair(x_coords2, y_coords2);
        System.out.println("Closest pair indices: [" + result2[0] + ", " + result2[1] + "]");

        int[] x_coords3 = { 0, 10, 100 };
        int[] y_coords3 = { 0, 10, 100 };
        int[] result3 = findClosestPair(x_coords3, y_coords3);
        System.out.println("Closest pair indices: [" + result3[0] + ", " + result3[1] + "]");
    }
}