
/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize 
 */
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Scanner;

public class GraphDemo {
    public static void main(String[] args) throws Exception {

        Scanner reader = new Scanner(System.in);

        System.out.print("Enter your location (city and state abriviation): ");
        String firstlocation = reader.nextLine();
        System.out.println();
        Point start = getcoordinate(firstlocation, "data/uscities.csv");

        System.out.print("Enter your destination (city and state abriviation): ");
        String endlocation = reader.nextLine();
        System.out.println();
        Point end = getcoordinate(endlocation, "data/uscities.csv");

        GraphProcessor g = new GraphProcessor();
        g.initialize(new FileInputStream("data/usa.graph"));

        long startTime = System.nanoTime();

        start = g.nearestPoint(start);
        end = g.nearestPoint(end);

        List<Point> route = g.route(start, end);
        Double distance = g.routeDistance(route);

        long overall = System.nanoTime() - startTime;
        double elapsedMillis = overall / 1E6;

        // prints a variety of data associated with the route
        String nearestStart = ("(" + start.getLat() + ", " + start.getLon() + ")");
        String nearestEnd = ("(" + end.getLat() + ", " + end.getLon() + ")");

        System.out.println("Nearest point to " + firstlocation + " is " + nearestStart + ". ");
        System.out.println("Nearest point to " + endlocation + " is " + nearestStart + ". ");
        System.out.println("The total distance along the route between " + nearestStart + " and " + nearestEnd + "is "
                + distance + " miles");
        System.out.println(
                "The total time to get nearest points, route, and total distance is: " + elapsedMillis + "ms.");

        Visualize v = new Visualize("data/usa.vis", "images/usa.png");
        v.drawRoute(route);

        reader.close();

    }

    public static Point getcoordinate(String city, String fileName) throws Exception {
        Scanner cf = new Scanner(new File(fileName));
        String[] input = cf.nextLine().split(",");
        String currentCity = input[0] + " " + input[1];

        while (!currentCity.equals(city) && cf.hasNextLine()) {
            input = cf.nextLine().split(",");
            currentCity = input[0] + " " + input[1];
        }
        if (!currentCity.equals(city)) {
            throw new Exception("City not found, please enter city with valid format");
        }

        double lat = Double.parseDouble(input[2]);
        double lon = Double.parseDouble(input[3]);

        cf.close();
        return new Point(lat, lon);
    }
}
