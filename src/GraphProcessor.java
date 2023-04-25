import java.security.InvalidAlgorithmParameterException;
import java.util.List;
import java.io.FileInputStream;
import java.util.*;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * 
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    public List<Point> vertices;
    public Map<Point, List<Point>> adjacencyList;

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * 
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */
    public void initialize(FileInputStream file) throws Exception {
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        if (!line.equals("graph")) {
            throw new Exception("Could not read .graph file");
        }
        int numVertices = scanner.nextInt();
        scanner.nextLine();
        vertices = new ArrayList<>();
        adjacencyList = new HashMap<>();
        for (int i = 0; i < numVertices; i++) {
            line = scanner.nextLine();
            String[] parts = line.split(" ");
            Point vertex = new Point(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
            vertices.add(vertex);
            List<Point> adjacentVertices = new ArrayList<>();
            for (int j = 3; j < parts.length; j++) {
                int adjacentVertexIndex = Integer.parseInt(parts[j]);
                Point adjacentVertex = vertices.get(adjacentVertexIndex);
                adjacentVertices.add(adjacentVertex);
            }
            adjacencyList.put(vertex, adjacentVertices);
        }
        scanner.close();
    }

    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * 
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        if (vertices.isEmpty()) {
            return null;
        }
        Point nearest = vertices.get(0);
        double minDistance = p.distance(nearest);
        for (int i = 1; i < vertices.size(); i++) {
            Point vertex = vertices.get(i);
            double distance = p.distance(vertex);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = vertex;
            }
        }
        return nearest;
    }

    }

    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points,
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * 
     * @param start Beginning point. May or may not be in the graph.
     * @param end   Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        if (route == null || route.size() < 2) {
            return 0.0;
        }
        double totalDistance = 0.0;
        Point previousPoint = route.get(0);
        for (int i = 1; i < route.size(); i++) {
            Point currentPoint = route.get(i);
            double distance = previousPoint.distance(currentPoint);
            return totalDistance;
    }

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * 
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        if (!adjacencyList.containsKey(p1) || !adjacencyList.containsKey(p2)) {
            return false;
        }

        // Perform DFS to check if there is a path from p1 to p2
        Set<Point> visited = new HashSet<>();
        Stack<Point> stack = new Stack<>();
        stack.push(p1);

        while (!stack.isEmpty()) {
            Point current = stack.pop();
            visited.add(current);

            // Check if we reached p2
            if (current.equals(p2)) {
                return true;
            }

            // Add adjacent vertices to the stack
            for (Point adj : adjacencyList.get(current)) {
                if (!visited.contains(adj)) {
                    stack.push(adj);
                }
            }
        }

        // No path found
        return false;

    }

    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * 
     * @param start Beginning point.
     * @param end   Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route,
     *                                            either because start is not
     *                                            connected to end or because start
     *                                            equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        if (!adjacencyList.containsKey(start) || !adjacencyList.containsKey(end)) {
            throw new InvalidAlgorithmParameterException("start or end point is not in the graph");
        }
        Map<Point, Double> distances = new HashMap<>();
        Map<Point, Point> predecessors = new HashMap<>();
        PriorityQueue<Point> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        distances.put(start, 0.0);
        queue.offer(start);
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.equals(end)) {
                break;
            }
            for (Point neighbor : adjacencyList.get(current)) {
                double distance = current.distance(neighbor);
                double totalDistance = distances.get(current) + distance;
                if (!distances.containsKey(neighbor) || totalDistance < distances.get(neighbor)) {
                    distances.put(neighbor, totalDistance);
                    predecessors.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }
        if (!predecessors.containsKey(end)) {
            throw new InvalidAlgorithmParameterException("no path found from start to end");
        }
        List<Point> path = new ArrayList<>();
        Point current = end;
        while (!current.equals(start)) {
            path.add(current);
            current = predecessors.get(current);
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

}
