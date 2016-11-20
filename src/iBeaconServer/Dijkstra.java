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
    private Vertex A = null, B = null, C = null, D = null, E = null, F = null, G = null ;
    private Vertex H = null, I = null, J = null, K = null, L = null, M = null ;
    private String lastPoint = "";

    public Dijkstra() {
        A = new Vertex("A");
        B = new Vertex("B");
        C = new Vertex("C");
        D = new Vertex("D");
        E = new Vertex("E");
        F = new Vertex("F");
        G = new Vertex("G");
        H = new Vertex("H");
        I = new Vertex("I");
        J = new Vertex("J");
        K = new Vertex("K");
        L = new Vertex("L");
        M = new Vertex("M");

//        A.adjacencies = new Edge[]{ new Edge(B, 2), new Edge(D, 1) };
//        B.adjacencies = new Edge[]{ new Edge(A, 2), new Edge(G, 2) };
//        C.adjacencies = new Edge[]{ new Edge(D, 2), new Edge(E, 2) };
//        D.adjacencies = new Edge[]{ new Edge(A, 1), new Edge(C, 2), new Edge(F, 1) };
//        E.adjacencies = new Edge[]{ new Edge(C, 2), new Edge(F, 2), new Edge(H, 2) };
//        F.adjacencies = new Edge[]{ new Edge(D, 1), new Edge(E, 2), new Edge(G, 2), new Edge(I, 2) };
//        G.adjacencies = new Edge[]{ new Edge(B, 2), new Edge(F, 2), new Edge(J, 2) };
//        H.adjacencies = new Edge[]{ new Edge(E, 2), new Edge(I, 2), new Edge(K, 2) };
//        I.adjacencies = new Edge[]{ new Edge(F, 2), new Edge(H, 2), new Edge(J, 2), new Edge(L, 2) };
//        J.adjacencies = new Edge[]{ new Edge(G, 2), new Edge(I, 2), new Edge(M, 2) };
//        K.adjacencies = new Edge[]{ new Edge(H, 2), new Edge(L, 2) };
//        L.adjacencies = new Edge[]{ new Edge(I, 2), new Edge(K, 2), new Edge(M, 2) };
//        M.adjacencies = new Edge[]{ new Edge(J, 2), new Edge(L, 2) };

        A.adjacencies = new Edge[]{ new Edge(B, 2), new Edge(D, 1) };
        B.adjacencies = new Edge[]{ new Edge(A, 2), new Edge(C, 2) };
        C.adjacencies = new Edge[]{ new Edge(B, 2), new Edge(D, 2), new Edge(F, 2) };
        D.adjacencies = new Edge[]{ new Edge(A, 1), new Edge(C, 2), new Edge(E, 1) };
        E.adjacencies = new Edge[]{ new Edge(D, 2), new Edge(F, 2), new Edge(H, 2) };
        F.adjacencies = new Edge[]{ new Edge(C, 1), new Edge(E, 2), new Edge(G, 2) };
        G.adjacencies = new Edge[]{ new Edge(F, 2), new Edge(H, 2) };
        H.adjacencies = new Edge[]{ new Edge(E, 2), new Edge(G, 2) };
    }

    public String getPath(String start, String end){
        Vertex startPoint = getVertex(start) ;
        Vertex endPoint = getVertex(end) ;

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
        if( lastPoint.equals("") ){
            carCommand = "F";
        }

        lastPoint = carPath.substring( carPath.length() - 2, carPath.length() - 1 );
        //System.out.println("lastPoint: " + lastPoint);

        if( carPath.length() >= 3){
            for(i = 0 ; i <= carPath.length() - 3 ; i ++ ){
                carCommand = carCommand + getCommand(carPath.substring( i , i + 3)) ;
            }
        }
        else{
            if(carPath.equals("AD")){
                carCommand = "F";
            }
            else if(carPath.equals("AB")){
                carCommand = "R";
            }
        }

        initialVertex();
        return carCommand ;
    }
    public String getCommand(String path){
        switch (path){
//            case "BGJ":case "GJM":case "ADF":case "DFI":case "FIL":case "CEH":case "EHK":case "EFG":case "HIJ":case "KLM":
//            case "JGB":case "MJG":case "FDA":case "IFD":case "LIF":case "HEC":case "KHE":case "GFE":case "JIH":case "MLK":
//                return "F";
//            case "BAD":case "DFG":case "FGB":case "GBA":case "EFD":case "FDC":case "DCE":case "CEF":
//            case "HIF":case "IFE":case "FEH":case "EHI":case "IJG":case "JGF":case "GFI":case "FIJ":
//            case "KLI":case "LIH":case "IHK":case "HKL":case "LMJ":case "MJI":case "JIL":case "ILM":case"CDA":
//                return "R";
//            case "DAB":case "GFD":case "BGF":case "ABG":case "DFE":case "CDF":case "ECD":case "FEC":
//            case "FIH":case "EFI":case "HEF":case "IHE":case "GJI":case "FGJ":case "IFG":case "JIF":
//            case "ILK":case "HIL":case "KHI":case "LKH":case "JML":case "IJM":case "LIJ":case "MLI":case"ADC":
//                return "L";
//            case "ABA":case "ADA":case "BAB":case "BGB":case "CDC":case "CEC":case "DAD":case "DCD":case "DFD":
//            case "ECE":case "EFE":case "EHE":case "FDF":case "FEF":case "FGF":case "FIF":case "GBG":case "GFG":case "GJG":
//            case "HEH":case "HIH":case "HKH":case "IFI":case "IHI":case "IJI":case "ILI":case "JGJ":case "JIJ":case "JMJ":
//            case "KHK":case "KLK":case "LIL":case "LKL":case "LML":case "MLM":case "MJM":
//                return "B";
//            default:
//                return "";

            case "ADE":case "EDA":case "DEH":case "HED":
            case "BCF":case "FCB":case "CFG":case "GFC":
                return "F";
            case "CBA":case "DCB":case "ADC":case "BAD":
            case "FCD":case "EFC":case "DEF":case "CDE":
            case "GFE":case "HGF":case "EHG":case "FEH":
                return "R";
            case "ABC":case "BCD":case "CDA":case "DAB":
            case "DCF":case "CFE":case "FED":case "EDC":
            case "EFG":case "FGH":case "GHE":case "HEF":
                return "L";
            case "ABA":case "ADA":case "BAB":case "BCB":
            case "CBC":case "CDC":case "CFC":
            case "DAD":case "DCD":case "DED":
            case "EDE":case "EFE":case "EHE":
            case "FCF":case "FEF":case "FGF":
            case "GFG":case "GHG":case "HEH":case "HGH":
                return "B";
            default:
                return "";
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

    public Vertex getVertex(String loc){
        switch (loc){
//            case "A":case "入口":
//                return A;
//            case "B":case "麵包":
//                return B;
//            case "C":case "盥洗用品":
//                return C;
//            case "D":case "電腦":
//                return D;
//            case "E":case "化妝品":
//                return E;
//            case "F":case "電視&手機":
//                return F;
//            case "G":case "冷藏飲料":
//                return G;
//            case "H":case "生活用品B":
//                return H;
//            case "I":case "廚具&電風扇":
//                return I;
//            case "J":case "冰品":
//                return J;
//            case "K":case "餅乾":
//                return K;
//            case "L":case "調理食品":
//                return L;
//            case "M":case "沖泡飲品":
//                return M;
//            default:
//                return null;

            case "A":case "入口":
                return A;
            case "B":case "麵包":
                return B;
            case "C":case "冷藏飲料":
                return C;
            case "D":case "化妝品":
                return D;
            case "E":case "生活用品A":
                return E;
            case "F":case "冰品":
                return F;
            case "G":case "沖泡飲品":
                return G;
            case "H":case "餅乾":
                return H;
            default:
                return null;
        }
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
        G.minDistance = Double.POSITIVE_INFINITY;
        G.previous = null;
        H.minDistance = Double.POSITIVE_INFINITY;
        H.previous = null;
        I.minDistance = Double.POSITIVE_INFINITY;
        I.previous = null;
        J.minDistance = Double.POSITIVE_INFINITY;
        J.previous = null;
        K.minDistance = Double.POSITIVE_INFINITY;
        K.previous = null;
        L.minDistance = Double.POSITIVE_INFINITY;
        L.previous = null;
        M.minDistance = Double.POSITIVE_INFINITY;
        M.previous = null;
    }

}
