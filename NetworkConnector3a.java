import java.util.*;

//done
public class NetworkConnector3a {
    // Helper function to find the root parent of a node
    public static int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    // Helper function to union two sets
    public static void union(int[] parent, int x, int y) {
        parent[find(parent, x)] = find(parent, y);
    }

    // Method to calculate the minimum cost
    public static int minTotalCost(int n, int[] modules, int[][] connections) {
        // Create a priority queue to sort connections by cost
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        // Add all given connections to the priority queue
        for (int[] connection : connections) {
            pq.add(connection);
        }

        // Initialize Union-Find (Disjoint Set) structure to manage connections
        int[] parent = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }

        // Step 1: Find the minimum module cost
        int minModuleCost = Arrays.stream(modules).min().orElse(0);
        int totalCost = minModuleCost; // Start with the minimum module cost

        // Step 2: Process the priority queue and build the network
        int edgesUsed = 0;
        while (!pq.isEmpty() && edgesUsed < n - 1) {
            int[] connection = pq.poll(); // Get the connection with the least cost
            int device1 = connection[0];
            int device2 = connection[1];
            int cost = connection[2];

            // Check if the devices are already connected
            if (find(parent, device1) != find(parent, device2)) {
                union(parent, device1, device2); // Connect them
                totalCost += cost; // Add the cost
                edgesUsed++; // Increase the number of edges used
            }
        }

        // If we have used fewer edges than needed, return -1 (not possible to connect
        // all)
        return (edgesUsed == n - 1) ? totalCost : -1;
    }

    // Main method to test the solution
    public static void main(String[] args) {
        int n = 3;
        int[] modules = { 1, 2, 2 };
        int[][] connections = { { 1, 2, 1 }, { 2, 3, 1 } };

        // Print the result
        System.out.println(minTotalCost(n, modules, connections));
    }
}