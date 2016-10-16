package iBeaconServer;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
/**
 * Created by Alex on 2016/9/26.
 */
public class Dijkstra {
    private Vertex A = null;
    private Vertex B = null;
    private Vertex C = null;
    private Vertex D = null;
    private Vertex E = null;
    private Vertex F = null;
    private String lastPoint = "";

    public Dijkstra() {
        A = new Vertex("A");
        B = new Vertex("B");
        C = new Vertex("C");
        D = new Vertex("D");
        E = new Vertex("E");
        F = new Vertex("F");

        // set the edges and weight
        A.adjacencies = new Edge[]{new Edge(B, 3), new Edge(F, 3)};
        B.adjacencies = new Edge[]{new Edge(C, 3), new Edge(A, 3)};
        //B.adjacencies = new Edge[]{new Edge(C, 3), new Edge(A, 3), new Edge(E, 3)};
        C.adjacencies = new Edge[]{new Edge(D, 3), new Edge(B, 3)};
        D.adjacencies = new Edge[]{new Edge(E, 3), new Edge(C, 3)};
        //E.adjacencies = new Edge[]{new Edge(F, 3), new Edge(D, 3), new Edge(B, 3)};
        E.adjacencies = new Edge[]{new Edge(F, 3), new Edge(D, 3)};
        F.adjacencies = new Edge[]{new Edge(A, 3), new Edge(E, 3)};
    }

    public String getPath(String start, String end){
        Vertex startPoint = getVertex(start) ;
        Vertex endPoint = getVertex(end) ;

        System.out.println("2222" + startPoint.toString() + endPoint.toString());
        computePaths(startPoint);
        System.out.println(start + " to " + end + ": " + endPoint.minDistance);
        List<Vertex> path = getShortestPathTo(endPoint);
        System.out.println("Path: " + path);

        String carPath = "" ;
        String carCommand = "" ;
        int i ;
        for(i = 0 ; i < path.size() ; i ++ ){
            carPath = carPath + path.get(i);
        }
        carPath = lastPoint + carPath ;
        lastPoint = carPath.substring( carPath.length() - 2, carPath.length() - 1 );
        System.out.println("lastPoint: " + lastPoint);

        if( carPath.length() >= 3){
            for(i = 0 ; i <= carPath.length() - 3 ; i ++ ){
                carCommand = carCommand + getCommand(carPath.substring( i , i + 3)) ;
            }
        }
        else{
            if(carPath.equals("AB")){
                carCommand = "U";
            }
            else if(carPath.equals("AF")){
                carCommand = "R";
            }
        }

        initialVertex();
        return carCommand ;
    }
    public String getCommand(String path){
        switch (path){
            case "ABC":case "CBA":case "DEF":case "FED":
                return "U";
            case "ABE":case "BEF":case "BCD":case "CDE":case "DEB":case "EBC":case "EFA":case "FAB":
                return "R";
            case "EBA":case "FEB":case "DCB":case "EDC":case "BED":case "CBE":case "AFE":case "BAF":
                return "L";
            case "ABA":case "AFA":case "BAB":case "BCB":case "CBC":case "CDC":case "DCD":case "DED":case "EDE":case "EFE":case "FEF":case "FAF":
                return "D";
            default:
                return "";
        }
    }

    public Vertex getVertex(String loc){
        switch (loc){
            case "A":case "化妝品":
                return A;
            case "B":case "生活用品B":
                return B;
            case "C":case "餅乾":
                return C;
            case "D":case "調理食品":
                return D;
            case "E":case "廚具":
                return E;
            case "F":case "電視":
                return F;
            default:
                return null;
        }
    }

    static class Vertex implements Comparable<Vertex> {
        public final String name ;
        public Edge[] adjacencies ;
        public double minDistance = Double.POSITIVE_INFINITY ;
        public Vertex previous ;
        public Vertex(String argName) { name = argName ; }
        public String toString() { return name ; }
        public int compareTo(Vertex other) {
            return Double.compare(minDistance, other.minDistance ) ;
        }
    }

    static class Edge {
        public final Vertex target;
        public final double weight;
        public Edge(Vertex argTarget, double argWeight) {
            target = argTarget; weight = argWeight; }
    }

    public void computePaths(Vertex source) {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            for (Edge e : u.adjacencies) {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }
    public void initialVertex(){
        A.minDistance = Double.POSITIVE_INFINITY;
        A.previous = null;
        B.minDistance = Double.POSITIVE_INFINITY;
        B.previous = null;
        C.minDistance = Double.POSITIVE_INFINITY;
        C.previous = null;
        D.minDistance = Double.POSITIVE_INFINITY;
        D.previous = null;
        E.minDistance = Double.POSITIVE_INFINITY;
        E.previous = null;
        F.minDistance = Double.POSITIVE_INFINITY;
        F.previous = null;
    }

}
