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
    private List<Point> vertices;
    private HashMap<Point, HashSet<Point>> adjacencyList;

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * 
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */
    public void initialize(FileInputStream file) throws Exception {
            Scanner reader = new Scanner(file);
            adjacencyList = new HashMap<>(); 
            String[] first = reader.nextLine().split(" ");
            int numVertices = Integer.parseInt(first[0]);
            int numEdges = Integer.parseInt(first[1]);
    
            Point[] arr = new Point[numVertices];
    
            for(int i = 0; i < numVertices; i ++){ //continue reading lines for every vertex in the file
                String[] line = reader.nextLine().split(" ");
                String name = line[0];
                Double latitude = Double.parseDouble(line[1]);
                Double longitude = Double.parseDouble(line[2]);
                Point p = new Point(latitude, longitude); //creates new Point with input from the file
                adjacencyList.put(p, new HashSet<Point>()); //adds the new Point to the adjacency list with an empty corresponding set for storing adjacent points
                arr[i] = p; //store the point in array at the corresponding index 
            }
    
            for(int i = 0; i < numEdges; i++){ //for every edge in the file
                String[] line = reader.nextLine().split(" ");
                int vertex1 = Integer.parseInt(line[0]);
                int vertex2 = Integer.parseInt(line[1]);
                if(line.length > 2){ //stores the name if it appears in the file
                    String name = line[2];
                }
    
    
                Point p1 = arr[vertex1]; //accesses the point based on the given index in the array previously created 
                Point p2 = arr[vertex2];
                adjacencyList.get(p1).add(p2); //adds both of the points to each others adjacency sets 
                adjacencyList.get(p2).add(p1);
            }
    
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
        }
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
