/******************************************************************************
 *  Compilation:  javac SymbolDigraph.java
 *  Execution:    java SymbolDigraph
 *  Dependencies: HashMap.java Digraph.java In.java
 *  Data files:   http://algs4.cs.princeton.edu/42digraph/routes.txt
 *  
 *  %  java SymbolDigraph routes.txt " "
 *  JFK
 *     MCO
 *     ATL
 *     ORD
 *  ATL
 *     HOU
 *     MCO
 *  LAX
 *
 ******************************************************************************/

/**
 *  The {@code SymbolDigraph} class represents a digraph, where the
 *  vertex names are arbitrary strings.
 *  By providing mappings between string vertex names and integers,
 *  it serves as a wrapper around the
 *  {@link Digraph} data type, which assumes the vertex names are integers
 *  between 0 and <em>V</em> - 1.
 *  It also supports initializing a symbol digraph from a file.
 *  <p>
 *  This implementation uses an {@link HashMap} to map from strings to integers,
 *  an array to map from integers to strings, and a {@link Digraph} to store
 *  the underlying graph.
 *  The <em>indexOf</em> and <em>contains</em> operations take time 
 *  proportional to log <em>V</em>, where <em>V</em> is the number of vertices.
 *  The <em>nameOf</em> operation takes constant time.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SymbolCustomDigraph {
    private HashMap<String, Integer> st;  // string -> index
    private Map<String, Long> totalRelationshipUsages;
    //Key of the first map is the name of vertex.
    //Total cost with adjacent vertexes calc as map value.
    private Map<String, Long> costByVertex;
    private Map<String, Integer> totalVertexRelationshipOcurrences;
    private BigInteger totalProjectCost = BigInteger.ZERO;
    private String[] keys;           // index  -> string
    private Digraph graph;           // the underlying digraph

    /**
     * Initializes a digraph from a file using the specified delimiter.
     * Each line in the file contains
     * the name of a vertex, followed by a list of the names
     * of the vertices adjacent to that vertex, separated by the delimiter.
     * @param filename the name of the file
     */
    public SymbolCustomDigraph(String filename) {
        st = new HashMap<String, Integer>();
        totalRelationshipUsages = new HashMap<>();
        totalVertexRelationshipOcurrences = new HashMap<>();
        costByVertex = new HashMap<>();

        // First pass builds the index by reading strings to associate
        // distinct strings with an index
        In in = new In(filename);

        Integer totalVertexes = 0;

        try {
             totalVertexes = Integer.valueOf(in.readLine());
        }catch (Exception e){
            throw new IllegalArgumentException("Erro ao armazenar total de vertices: Não é um número.");
        }

        int i = 0;
        String[] aux = new String[totalVertexes];
        String[] vertexNameAndCost = getInitialVertex(filename);
        aux[0] = vertexNameAndCost[0];
        costByVertex.put(aux[0], Long.valueOf(vertexNameAndCost[1].trim()));

        i++;
        int j = 0;

        while (j < totalVertexes) {
            j++;
            vertexNameAndCost = in.readLine().split(" ");

            if(vertexNameAndCost[0].trim().equals(aux[0])){
                continue;
            }

            aux[i] = vertexNameAndCost[0].trim();
            costByVertex.put(aux[i],Long.valueOf(vertexNameAndCost[1].trim()));
            i++;
        }

        for (i = 0; i < totalVertexes; i++) {
            if (!st.containsKey(aux[i]))
                st.put(aux[i], i);
        }

        // inverted index to get string keys in an aray
        keys = new String[st.size()];
        for (String name : st.keySet()) {
            keys[st.get(name)] = name;
        }

        // second pass builds the digraph by connecting first vertex on each
        // line to all others
        graph = new Digraph(totalVertexes);

        Integer totalEdges = 0;
        try {
            totalEdges = Integer.valueOf(in.readLine().trim());
        }catch (Exception e){
            throw new IllegalArgumentException("Erro ao armazenar total de vertices: Não é um número.");
        }

        while (in.hasNextLine()) {
            String[] a = in.readLine().split(" ");
            int v = st.get(a[0].trim());
            int w = st.get(a[1].trim());

            graph.addEdge(v, w);

            Integer count = totalVertexRelationshipOcurrences.containsKey(a[0].trim()) ? totalVertexRelationshipOcurrences.get(a[0].trim()) + 1 : 1;

            totalVertexRelationshipOcurrences.put(a[0].trim(), count);

            totalRelationshipUsages.put(a[0]+"-"+a[1].trim(),Long.valueOf(a[2].trim()));
        }
    }

    public String[] getInitialVertex(String fileName){
        Map<String,Integer> count = new HashMap<>();
        Integer greater =  0;
        String vertexName = null;

        try(Stream<String> lines = Files.lines(Paths.get(fileName))){
            BufferedReader bf = Files.newBufferedReader(Paths.get(fileName));
            Integer linesToSkip = Integer.valueOf(bf.readLine().trim());

            int i = 0;
            for(String line : lines.skip(linesToSkip + 1).collect(Collectors.toList())){
                if(i == 0){
                    i++;
                    continue;
                }

                String mainVertexName =  line.split(" ")[0];

                count.put(mainVertexName, count.containsKey(mainVertexName) ? count.get(mainVertexName) +1 : 1);

                i++;
            }

            for(Map.Entry<String,Integer> entry : count.entrySet()){
                if(entry.getValue() > greater){
                    vertexName = entry.getKey();
                    greater = entry.getValue();
                }
            }

            BufferedReader bf2 = Files.newBufferedReader(Paths.get(fileName));
            Integer totalVertexes  = Integer.valueOf(bf2.readLine().trim());

            while (totalVertexes > 0){
                String[] currentVertexNameAndCost = bf2.readLine().trim().split(" ");

                if(currentVertexNameAndCost[0].trim().equals(vertexName)){
                    return currentVertexNameAndCost;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Does the digraph contain the vertex named {@code s}?
     * @param s the name of a vertex
     * @return {@code true} if {@code s} is the name of a vertex, and {@code false} otherwise
     */
    public boolean contains(String s) {
        return st.containsKey(s);
    }

    /**
     * Returns the integer associated with the vertex named {@code s}.
     * @param s the name of a vertex
     * @return the integer (between 0 and <em>V</em> - 1) associated with the vertex named {@code s}
     * @deprecated Replaced by {@link #indexOf(String)}.
     */
    @Deprecated
    public int index(String s) {
        return st.get(s);
    }

    /**
     * Returns the integer associated with the vertex named {@code s}.
     * @param s the name of a vertex
     * @return the integer (between 0 and <em>V</em> - 1) associated with the vertex named {@code s}
     */
    public Integer indexOf(String s) {
        return st.get(s);
    }

    /**
     * Returns the name of the vertex associated with the integer {@code v}.
     * @param  v the integer corresponding to a vertex (between 0 and <em>V</em> - 1) 
     * @return the name of the vertex associated with the integer {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @deprecated Replaced by {@link #nameOf(int)}.
     */
    @Deprecated
    public String name(int v) {
        validateVertex(v);
        return keys[v];
    }

    /**
     * Returns the name of the vertex associated with the integer {@code v}.
     * @param  v the integer corresponding to a vertex (between 0 and <em>V</em> - 1) 
     * @return the name of the vertex associated with the integer {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public String nameOf(int v) {
        validateVertex(v);
        return keys[v];
    }

    /**
     * Returns the digraph assoicated with the symbol graph. It is the client's responsibility
     * not to mutate the digraph.
     *
     * @return the digraph associated with the symbol digraph
     * @deprecated Replaced by {@link #digraph()}.
     */
    @Deprecated
    public Digraph G() {
        return graph;
    }

    /**
     * Returns the digraph assoicated with the symbol graph. It is the client's responsibility
     * not to mutate the digraph.
     *
     * @return the digraph associated with the symbol digraph
     */
    public Digraph digraph() {
        return graph;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
         int V = graph.V();
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    public Map<String, Long> getTotalRelationshipUsages() {
        return totalRelationshipUsages;
    }

    public Map<String, Long> getCostByVertex() {
        return costByVertex;
    }

    public void sumProjectCost(BigInteger cost){
        totalProjectCost = totalProjectCost.add(cost);
    }

    public BigInteger getTotalProjectCost() {
        return totalProjectCost;
    }

    /**
     * Unit tests the {@code SymbolDigraph} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
    }
}
