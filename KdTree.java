/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class KdTree {
    private Node root;  //  root of BST
    final private RectHV unitSquare = new RectHV(0, 0, 1, 1);
    //  final private SET<Node> ts;
    final private ArrayList<Point2D> insidePoints = new ArrayList<>();
    private Point2D champion;

    //  Construct the empty set of points
    public KdTree() {
        //  ts = new SET<Node>();
    }

    //  is the set empty?
    public boolean isEmpty() {
        return (root == null);
    }

    //  number of nodes in the TreeSet
    public int size() {
        return size(root);
    }

    //  aiding function to return nodes size
    private int size(Node x) {
        if (x == null) return 0;
        else return x.N;
    }

    //  add the node to the TreeSet (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        // Search for point. Update value if found; grow table if new.
        root = insert(root, p, unitSquare, true);
    }

    //  does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return contains(root, p, true);
    }

    //  draw all points to standard draw
    public void draw() {
        draw(root);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        range(root, rect);
        return insidePoints;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (this.isEmpty()) return null;
        champion = root.p;
        nearest(root, p);
        return champion;
    }

    // Private method to aid the nearest public method
    private void nearest(Node x, Point2D p) {
        if (x == null) return;
        // Check if current point node is closer than champion
        // StdOut.println("Checking node related to point: " + x.p.toString());
        // StdOut.println("Squared distance to query: " + x.p.distanceSquaredTo(p));
        // StdOut.println("Squared distnace from query to champion: " + p.distanceSquaredTo(champion));
        if (x.p.distanceSquaredTo(p) < p.distanceSquaredTo(champion)) champion = x.p;
        // StdOut.println("Current champion: " + champion.toString());
        //  Ingresas a los dos y solamente checas uno...
        if (x.rb != null && x.rb.rect.contains(p)) {
            if (champion.distanceSquaredTo(p) > x.rb.rect.distanceSquaredTo(p)) nearest(x.rb, p);
            if (x.lb != null && champion.distanceSquaredTo(p) > x.lb.rect.distanceSquaredTo(p))
                nearest(x.lb, p);
        }
        else {
            if (x.lb != null && champion.distanceSquaredTo(p) > x.lb.rect.distanceSquaredTo(p))
                nearest(x.lb, p);
            if (x.rb != null && champion.distanceSquaredTo(p) > x.rb.rect.distanceSquaredTo(p))
                nearest(x.rb, p);
        }
        return;
    }

    // private method to aid the range public method
    private void range(Node x, RectHV rect) {
        if (x == null) return;
        // Check if point of current point node is in  rectangle
        if (rect.contains(x.p)) insidePoints.add(x.p);
        // Check if there is intersection with the left node rect
        if (x.lb != null && rect.intersects(x.lb.rect)) range(x.lb, rect);
        // Check if there is intersection with the right node rect
        if (x.rb != null && rect.intersects(x.rb.rect)) range(x.rb, rect);
        return;
    }

    //  private method to aid the draw public method
    private void draw(Node x) {
        if (x == null) return;
        // Draw the vertical or horizontal line
        if (x.orientation) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            Point2D upper = new Point2D(x.p.x(), x.rect.ymax());
            x.p.drawTo(upper);
            Point2D lower = new Point2D(x.p.x(), x.rect.ymin());
            x.p.drawTo(lower);
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            Point2D left = new Point2D(x.rect.xmin(), x.p.y());
            x.p.drawTo(left);
            Point2D right = new Point2D(x.rect.xmax(), x.p.y());
            x.p.drawTo(right);
        }
        // Draw the point that this node represents
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        x.p.draw();
        //  Draw the left child
        draw(x.lb);
        draw(x.rb);
    }

    // private method to aid the insert public method
    private Node insert(Node x, Point2D p, RectHV r, boolean orientation) {

        if (x == null) return new Node(p, r, null, null, orientation, 1);
        if (p.compareTo(x.p) == 0) return x; // if point are equal
        //  Choose comparison type based on the orientation
        RectHV rNew;
        int cmp;
        if (orientation) {  //  compare through x
            cmp = Point2D.X_ORDER.compare(p, x.p);
            if (cmp < 0) {
                //  divide in left and right rectangles
                rNew = new RectHV(r.xmin(), r.ymin(), x.p.x(), r.ymax());
                x.lb = insert(x.lb, p, rNew, !x.orientation);
            }
            else {
                rNew = new RectHV(x.p.x(), r.ymin(), r.xmax(), r.ymax());
                x.rb = insert(x.rb, p, rNew, !x.orientation);
            }
        }
        else {
            cmp = Point2D.Y_ORDER.compare(p, x.p);
            if (cmp < 0) {
                rNew = new RectHV(r.xmin(), r.ymin(), r.xmax(), x.p.y());
                x.lb = insert(x.lb, p, rNew, !x.orientation);
            }
            else {
                rNew = new RectHV(r.xmin(), x.p.y(), r.xmax(), r.ymax());
                x.rb = insert(x.rb, p, rNew, !x.orientation);
            }
        }
        x.N = size(x.lb) + size(x.rb) + 1;
        return x;
    }

    // private method to aid the contains public method
    private boolean contains(Node x, Point2D p, boolean orientation) {
        //  Return value associated with Point in the subtree rooted at x;
        // return null if point not present in subtree rooted at x;
        if (x == null) return false;
        if (p.equals(x.p)) return true;
        int cmp;
        if (orientation) cmp = Point2D.X_ORDER.compare(p, x.p);
        else cmp = Point2D.Y_ORDER.compare(p, x.p);
        if (cmp < 0) return contains(x.lb, p, !x.orientation);
        else return contains(x.rb, p, !x.orientation);
    }


    private static class Node {
        final private Point2D p; // the point
        final private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb; // the left/bottom subtree
        private Node rb; // thre right/top subtree
        private int N; // number of nodes
        final private boolean orientation;

        public Node(Point2D p, RectHV rect, Node lb, Node rb, boolean orientation, int N) {
            this.p = p;
            this.lb = lb;
            this.rb = rb;
            this.orientation = orientation;
            this.rect = rect;
            this.N = N;
        }
    }

    public static void main(String[] args) {
        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        //  Check the constructor
        KdTree kt = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            //  Check the insert function
            kt.insert(p);
        }
        // Check the contains function
        //Point2D p = new Point2D(0.7, 0.2);
        //Point2D p1 = new Point2D(0.5, 0.4);
        //Point2D p2 = new Point2D(0.9, 0.6);
        //Point2D p3 = new Point2D(0.4, 0.7);
        // Boolean n = kt.contains(p);
        // Boolean n1 = kt.contains(p1);
        // Node n2 = kt.contains(p2);
        // Node n3 = kt.contains(p3);
        // if (n1 != null)
        //     StdOut.println(n1.p.toString() + " found with orientation: " + n1.orientation
        //                            + " and rectangle: " + n1.rect.toString());
        //
        // StdOut.println("Left node point: " + n1.lb.p.toString() + " and rectangle: " + n1.lb.rect
        //         .toString());
        // StdOut.println("Right node point: " + n1.rb.p.toString() + " and rectangle: " + n1.rb.rect
        //         .toString());
        // if (n2 != null)
        //     StdOut.println(n2.p.toString() + " found with orientation: " + n2.orientation
        //                            + " and rectangle: " + n2.rect.toString());
        // if (n3 != null)
        //     StdOut.println(n3.p.toString() + " found with orientation: " + n3.orientation
        //                            + " and rectangle: " + n3.rect.toString());
        // if (n != null)
        //     StdOut.println(n.p.toString() + " found with orientation: " + n.orientation
        //                            + " and rectangle: " + n.rect.toString());
        // StdOut.println("Left node point: " + n.lb.p.toString() + " and rectangle: " + n.lb.rect
        //         .toString());
        // StdOut.println("Right node point: " + n.rb.p.toString() + " and rectangle: " + n.rb.rect
        //         .toString());
        kt.draw();
        RectHV rect = new RectHV(0.0, 0.0, 0.6, 0.6);
        rect.draw();
        Iterable<Point2D> selected = kt.range(rect);
        StdOut.println("Points inside the rectangle: " + rect.toString());
        for (Point2D s : selected) {
            StdOut.println(s);
        }
        Point2D query = new Point2D(0.74, 0.31);
        query.draw();
        StdOut.println("The closest point to " + query.toString() + " is " + kt.nearest(query));
        StdOut.println("The size of the three: " + kt.size());
    }
}
