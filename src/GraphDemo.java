import java.io.FileInputStream;
import java.util.*;

/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
public class GraphDemo {
    public static void main(String[] args) throws Exception{
        GraphProcessor graphProcessor = new GraphProcessor();
        Visualize visualizer = new Visualize ("data/usa.vis", "images/usa.png");
        FileInputStream input = new FileInputStream("data/usa.graph");
        graphProcessor.initialize(input);
        Scanner scan = new Scanner(System.in);

        System.out.println("City 1 Latitude");
        Double firstLat = Double.valueOf(scan.nextLine());
        System.out.println("City 1 Longitude");
        Double firstLong = Double.valueOf(scan.nextLine());

        System.out.println("City 2 Latitude");
        Double secondLat = Double.valueOf(scan.nextLine());
        System.out.println("City 2 Longitude");
        Double secondLong = Double.valueOf(scan.nextLine());

        long timeStart = System.nanoTime();
        Point point1 = new Point(firstLat, firstLong);
        Point point2 = new Point(secondLat, secondLong);

        Point start = graphProcessor.nearestPoint(point1);
        Point end = graphProcessor.nearestPoint(point2);

        List<Point> myRoute = graphProcessor.route(start,end);
        visualizer.drawRoute(myRoute);

        double myDist = graphProcessor.routeDistance(myRoute);
        System.out.println("Distance: "+ myDist + "miles");


    }

    }
}