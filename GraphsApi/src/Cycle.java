/******************************************************************************
 *  Compilation:  javac Cycle.java
 *  Execution:    java Cycle
 *  Dependencies: Graph.java
 *
 *  Detects whether a graph has a cycle
 *  
 ******************************************************************************/

/**
 *  The {@code Cycle} class allows the detection of cycles in any non-directed graph
 *  The sample main method read the graph from a file and checks it
 *  
 *  @author Marcelo Cohen
 */
import java.util.HashSet;
import java.util.Set;

public class Cycle {

	private Set<String> edgeSet;
	private boolean[] marked;
	private boolean cyclic;

	public Cycle(Graph g) {
		edgeSet = new HashSet<String>();
		marked = new boolean[g.V()];
		// cyclic = false;
		cyclic = dfs(g, 0);		
	}

	private boolean dfs(Graph g, int s) {
		boolean hasCycle = false;
		//System.out.println("Mark: " + s);
		marked[s] = true;
		//System.out.print("Adj: ");
		//for (int w : g.adj(s))
		//	System.out.print(w + " ");
		//System.out.println();
		for (int w : g.adj(s)) {
			String aux = "";
			if(s>w)
				aux = w+"-"+s;
			else
				aux = s+"-"+w;						
			//System.out.println("Try: "+aux);
			if (!marked[w]) {
				//System.out.println("Adding " + aux);
				edgeSet.add(aux);
				hasCycle = dfs(g, w);
			} else {
				//System.out.println("Visited: " + s + "-" + w);
				if (!edgeSet.contains(aux)) {
					//System.out.println("Edge set doesn't contain: " + aux);
					hasCycle = true;
					break;
				}
			}
		}
		return hasCycle;
	}

	public boolean hasCycle() {
		return cyclic;
	}	

	public static void main(String[] args) {
		In in = new In("meuGrafo.txt");
		Graph g = new Graph(in);
		
		System.out.println(g.toDot());
		System.out.println();

		Cycle cycleDetector = new Cycle(g);				
		System.out.println("Has cycle? "+cycleDetector.hasCycle());
		
	}
}

