import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.io.File;
import java.io.FileInputStream;

//import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.IntegerConversion;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */

    private HashMap<Point, HashSet<Point>> adjList;

    public void initialize(FileInputStream file) throws Exception {
        Scanner reader = new Scanner(file);
        adjList = new HashMap<>(); 
        
        String[] first = reader.nextLine().split(" ");
        int VerticesCount = Integer.parseInt(first[0]);
        int EdgesCount = Integer.parseInt(first[1]);

        Point[] arr = new Point[VerticesCount];

        for(int i = 0; i < VerticesCount; i ++){ 
            String[] line = reader.nextLine().split(" ");
            String name = line[0];
            Double latitude = Double.parseDouble(line[1]);
            Double longitude = Double.parseDouble(line[2]);
            Point p = new Point(latitude, longitude); 
            adjList.put(p, new HashSet<Point>()); 
            arr[i] = p; 
        }

        for(int i = 0; i < EdgesCount; i++){ 
            String[] line = reader.nextLine().split(" ");
            int V1 = Integer.parseInt(line[0]);
            int V2 = Integer.parseInt(line[1]);
            if(line.length > 2){ /
                String name = line[2];
            }
            Point p1 = arr[V1]; 
            Point p2 = arr[V2];
            adjList.get(p1).add(p2); 
            adjList.get(p2).add(p1);
        }

    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        Point winning = p; 

        double shortest = Math.tan(Math.PI/2); 

        for(Point key: adjList.keySet()){
            if(p.distance(key) < shortest){
                shortest = p.distance(key);
                winning = key;
            }
        }
        return winning;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        double total = 0;
        for(int i = 0; i < route.size() - 1; i++){
            Point p1 = route.get(i);
            Point p2 = route.get(i + 1);
            total += p1.distance(p2);
        }
        return total;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        
        Set<Point> visited = new HashSet<>(); 
        Stack<Point> toExplore = new Stack<>(); 


        Point current = p1;
        visited.add(current);
        toExplore.add(current);

        while(!toExplore.isEmpty()){ 
            current = toExplore.pop();
            for(Point neighbor: adjList.get(current)){
                if(neighbor.equals(p2)){ 
                    return true; 
                }
                if(!visited.contains(neighbor)){ 
                    toExplore.push(neighbor);
                    visited.add(neighbor); 
                }
            }
        }
        return false;
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException { 
        Map<Point, Double> dist = new HashMap<>(); 
        Comparator<Point> comp = (a, b) -> (int)(dist.get(a) - dist.get(b));
        PriorityQueue<Point> toExplore = new PriorityQueue<>(comp);  
        Map<Point, Point> previous = new HashMap<>(); 

        Point current = start; 
        dist.put(current, 0.0);
        toExplore.add(current);

        while(!toExplore.isEmpty()){ 
            current = toExplore.remove(); 

            for(Point neighbor: adjList.get(current)){ 
                double weight = current.distance(neighbor); 

                if(!dist.containsKey(neighbor) || dist.get(neighbor) > dist.get(current) + weight){ 
                    toExplore.add(neighbor); 
                    previous.put(neighbor, current);  
                    dist.put(neighbor, dist.get(current) + weight);  
                }
            }
        }

        List<Point> path = new LinkedList<>(); 
        current = end;

        if(!previous.containsKey(end)){ 
            throw new InvalidAlgorithmParameterException("No valid route between points.");
        }

        path.add(end); 

        while(current != start){ 
            Point prev = previous.get(current); 
            path.add(0, prev); 
            current = prev;
        }
        return path;
    }


}
