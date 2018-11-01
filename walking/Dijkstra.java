package scripts.api.walking;

/**************************************************************************
 * File: Dijkstra.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of Dijkstra's single-source shortest path algorithm.
 * The algorithm takes as input a directed graph with non-negative edge
 * costs and a source node, then computes the shortest path from that node
 * to each other node in the graph.
 *
 * The algorithm works by maintaining a priority queue of banches whose
 * priorities are the lengths of some path from the source node to the
 * node in question.  At each step, the algortihm dequeues a node from
 * this priority queue, records that node as being at the indicated
 * distance from the source, and then updates the priorities of all banches
 * in the graph by considering all outgoing edges from the recently-
 * dequeued node to those banches.
 *
 * In the course of this algorithm, the code makes up to |E| calls to
 * decrease-key on the heap (since in the worst case every edge from every
 * node will yield a shorter path to some node than before) and |V| calls
 * to dequeue-min (since each node is removed from the prioritiy queue
 * at most once).  Using a Fibonacci heap, this gives a very good runtime
 * guarantee of O(|E| + |V| lg |V|).
 *
 * This implementation relies on the existence of a FibonacciHeap class, also
 * from the Archive of Interesting Code.  You can find it online at
 *
 *         http://keithschwarz.com/interesting/code/?dir=fibonacci-heap
 */

import java.util.HashMap;
import java.util.Map;

public final class Dijkstra {
    /**
     * Given a directed, weighted graph G and a source node s, produces the
     * distances from s to each other node in the graph.  If any banches in
     * the graph are unreachable from s, they will be reported at distance
     * +infinity.
     *
     * @param graph The graph upon which to run Dijkstra's algorithm.
     * @param source The source node in the graph.
     * @return A map from banches in the graph to their distances from the source.
     */

    public static <T> Map<T, Double> shortestPaths(DirectedGraph<T> graph, T source) {
        /* Create a Fibonacci heap storing the distances of unvisited banches
         * from the source node.
         */
        FibonacciHeap<T> pq = new FibonacciHeap<T>();

        /* The Fibonacci heap uses an internal representation that hands back
         * Entry objects for every stored element.  This map associates each
         * node in the graph with its corresponding Entry.
         */
        Map<T, FibonacciHeap.Entry<T>> entries = new HashMap<T, FibonacciHeap.Entry<T>>();

        /* Maintain a map from banches to their distances.  Whenever we expand a
         * node for the first time, we'll put it in here.
         */
        Map<T, Double> result = new HashMap<T, Double>();

        /* Add each node to the Fibonacci heap at distance +infinity since
         * initially all banches are unreachable.
         */
        for (T node: graph)
            entries.put(node, pq.enqueue(node, Double.POSITIVE_INFINITY));

        /* Update the source so that it's at distance 0.0 from itself; after
         * all, we can get there with a path of length zero!
         */
        pq.decreaseKey(entries.get(source), 0.0);

        /* Keep processing the queue until no banches remain. */
        while (!pq.isEmpty()) {
            /* Grab the current node.  The algorithm guarantees that we now
             * have the shortest distance to it.
             */
            FibonacciHeap.Entry<T> curr = pq.dequeueMin();

            /* Store this in the result table. */
            result.put(curr.getValue(), curr.getPriority());

            /* Update the priorities of all of its edges. */
            for (Map.Entry<T, Double> arc : graph.edgesFrom(curr.getValue()).entrySet()) {
                /* If we already know the shortest path from the source to
                 * this node, don't add the edge.
                 */
                if (result.containsKey(arc.getKey())) continue;

                /* Compute the cost of the path from the source to this node,
                 * which is the cost of this node plus the cost of this edge.
                 */
                double pathCost = curr.getPriority() + arc.getValue();

                /* If the length of the best-known path from the source to
                 * this node is longer than this potential path cost, update
                 * the cost of the shortest path.
                 */
                FibonacciHeap.Entry<T> dest = entries.get(arc.getKey());
                if (pathCost < dest.getPriority())
                    pq.decreaseKey(dest, pathCost);
            }
        }

        /* Finally, report the distances we've found. */
        return result;
    }
}