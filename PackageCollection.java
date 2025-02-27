import java.util.*;

public class PackageCollection {
    private List<List<Integer>> graph; // Adjacency list representation of the graph
    private int[] packages; // Array to store package information for each location
    private boolean[] visited; // Array to keep track of visited nodes during DFS
    private int minRoads; // Variable to store the minimum number of roads traversed

    public int packageCollection(int[] packages, int[][] roads) {
        // Initialize class variables
        this.packages = packages;
        this.graph = buildGraph(packages.length, roads);
        this.visited = new boolean[packages.length];
        this.minRoads = Integer.MAX_VALUE; // Initialize with a large value

        // Trying to start from each location to find the optimal starting point
        for (int i = 0; i < packages.length; i++) {
            dfs(i, i, 0, new HashSet<>());
        }

        return minRoads;
    }

    private void dfs(int start, int current, int roads, Set<Integer> collected) {
        // If all packages are collected, update minRoads and return
        if (collected.size() == countPackages()) {
            // Add the distance to return to the starting point
            minRoads = Math.min(minRoads, roads + shortestPath(current, start));
            return;
        }

        visited[current] = true; // Mark current node as visited

        // Collect packages within 2 steps of the current location
        for (int i = 0; i < packages.length; i++) {
            if (shortestPath(current, i) <= 2 && packages[i] == 1) {
                collected.add(i);
            }
        }

        // Explore all neighboring locations
        for (int neighbor : graph.get(current)) {
            if (!visited[neighbor]) {
                // Recursive call to explore this path
                dfs(start, neighbor, roads + 1, new HashSet<>(collected));
            }
        }

        visited[current] = false; // Backtrack: mark current node as unvisited
    }

    private List<List<Integer>> buildGraph(int n, int[][] roads) {
        // Create an adjacency list representation of the graph
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        // Add edges to the graph (undirected)
        for (int[] road : roads) {
            graph.get(road[0]).add(road[1]);
            graph.get(road[1]).add(road[0]);
        }
        return graph;
    }

    private int shortestPath(int from, int to) {
        // BFS to find shortest path between two nodes
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[packages.length];
        int[] distance = new int[packages.length];
        Arrays.fill(distance, Integer.MAX_VALUE);

        queue.offer(from);
        visited[from] = true;
        distance[from] = 0;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == to)
                return distance[current];

            for (int neighbor : graph.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    distance[neighbor] = distance[current] + 1;
                    queue.offer(neighbor);
                }
            }
        }

        return Integer.MAX_VALUE; // If no path found
    }

    private int countPackages() {
        // Count the total number of packages to be collected
        int count = 0;
        for (int p : packages) {
            if (p == 1)
                count++;
        }
        return count;
    }

    public static void main(String[] args) {
        PackageCollection solution = new PackageCollection();
        int[] packages = { 1, 0, 0, 0, 0, 1 };
        int[][] roads = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 }, { 4, 5 } };
        System.out.println(solution.packageCollection(packages, roads));
    }
}