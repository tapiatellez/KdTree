/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {
    final private TreeSet<Point2D> ts;

    // construct an empty set of points
    public PointSET() {
        ts = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return ts.isEmpty();
    }

    //  number of points in the set
    public int size() {
        return ts.size();
    }

    //  add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        ts.add(p);
    }

    //  does the set contains point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return ts.contains(p);
    }

    //  draw all points to standard draw
    public void draw() {
        for (Point2D p : ts) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        // Create the iterable
        ArrayList<Point2D> inside = new ArrayList<>();
        // for each point in the frame
        for (Point2D p : ts) {
            // Check if the point is in the
            if ((rect.xmin() <= p.x() && p.x() <= rect.xmax()) && (rect.ymin() <= p.y()
                    && p.y() <= rect.ymax())) {
                inside.add(p);
            }
        }
        return inside;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (ts.isEmpty()) return null;
        Point2D champion = ts.first();
        for (Point2D p2 : ts) {
            if (p.distanceSquaredTo(p2) < p.distanceSquaredTo(champion)) {
                champion = p2;
            }
        }
        return champion;
    }

    public static void main(String[] args) {
        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        //  Check the constructor
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            //  Check the insert function
            brute.insert(p);
        }
        //  Check the isEmpty() function
        StdOut.println("Is the PointSET empty? " + brute.isEmpty());
        //  Check the size() function
        StdOut.println("The size of the PointSET: " + brute.size());
        //  Check contains(p) function
        Point2D pContains = new Point2D(0.0, 0.5);
        StdOut.println("The PointSET contains (0.0, 0.5)?: " + brute.contains(pContains));
        //  Check draws()
        brute.draw();
        //  Check range
        StdOut.println("The points that belong inside the rectangle are: ");
        RectHV rhv = new RectHV(0.0, 0.0, 0.5, 0.5);
        rhv.draw();
        Iterable<Point2D> al = brute.range(rhv);
        int counter = 0;
        for (Point2D p : al) {
            StdOut.println(p.toString());
            counter++;
        }
        StdOut.println("The amount of points inside the rectangle are: " + counter);
        //  Check nearest
        Point2D n = new Point2D(0.0, 0.3);
        StdOut.println("The nearest point to (0.0, 0.3) is: " + brute.nearest(n));
        StdOut.println("With a distance of: " + n.distanceSquaredTo(brute.nearest(n)));
    }
}
