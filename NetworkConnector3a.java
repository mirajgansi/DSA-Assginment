import java.util.*;

//done
public class NetworkConnector3a {
    public static int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    public static void union(int[] parent, int x, int y) {
        parent[find(parent, x)] = find(parent, y);
    }

    public static int minTotalCost(int n, int[] modules, int[][] connections) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        for (int[] connection : connections) {
            pq.add(connection);
        }
        int[] parent = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
        int minModuleCost = Arrays.stream(modules).min().orElse(0);
        int totalCost = minModuleCost;
        int edgesUsed = 0;
        while (!pq.isEmpty() && edgesUsed < n - 1) {
            int[] connection = pq.poll();
            int device1 = connection[0];
            int device2 = connection[1];
            int cost = connection[2];

            if (find(parent, device1) != find(parent, device2)) {
                union(parent, device1, device2);
                totalCost += cost;
                edgesUsed++;
            }
        }
        return (edgesUsed == n - 1) ? totalCost : -1;
    }

    public static void main(String[] args) {
        int n = 3;
        int[] modules = { 1, 2, 2 };
        int[][] connections = { { 1, 2, 1 }, { 2, 3, 1 } };
        System.out.println(minTotalCost(n, modules, connections));
    }
}