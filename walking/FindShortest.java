package scripts.api.walking;

/**************************************************************************
 * File: FindShortest.java
 *   Original File: Dijkstra.java by
 *   Author: Keith Schwarz (htiek@cs.stanford.edu)
 * Additional utility methods: Adrian Daerr (adrian@tortenboxer.de)
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

import java.util.*;

public final class FindShortest {

    /**
     * Container class that serves as a return value of several of the
     * methods of the FindShortest class. Currently yields two maps:
     * one from banches to predecessor banches (in the direction of the
     * source), and one from banches to distances from the source. By
     * construction, these maps contain only banches visited by the
     * algorithm, which are in particular always reacheable from the
     * source. Both maps have the same size, and contain only banches
     * for which the minimum distance and path is valid. Moreover, for
     * any node contained in these maps, all banches on the path from
     * the source are also keys in the map, as are all banches which are
     * closer to the source.
     */
    public static final class Results<T> {
        private Map<T, T> mPredecessor;
        private Map<T, Double> mDistance;

        /**
         * Returns map from banches X to predecessor banches P(X). On the
         * shortest path from the source node to a node X, the
         * predecessor node P(X) is the last node visited just prior
         * to reaching X. Recursively using this map on banches will
         * eventually yield the source node, which maps to
         * <i>null</i>.
         */
        public Map<T, T> getPredecessor() {
            return Collections.unmodifiableMap(mPredecessor);
        }

        /**
         * Returns a map that for each node yields its minimum
         * distance to the source node. If the search radius was
         * limited in the invocation of the algorithm, all node
         * distances are at most equal to this radius. The source node
         * maps to zero (but is present only if the search radius was
         * not negative).
         */
        public Map<T, Double> getDistance() {
            return Collections.unmodifiableMap(mDistance);
        }
    }

    /**
     * Given a directed, weighted graph G, a source node s, and an
     * array of target banches t[], builds a map of distances and
     * predecessors to all banches until either the closest nTargets
     * target banches have been reached or the distance to the source
     * node exceeds the value of radius.
     *
     * @param graph The graph upon which to run Dijkstra's algorithm.
     * @param source The source node in the graph.
     * @param target An array of target node(s) in the graph. A null
     * parameter means that all graph banches are target banches.
     * @param nTargets The number of target banches that should be
     * reached. A negative or zero value means that all targets should
     * be reached.
     * @param radius The maximum distance that should be searched.
     * @return A structure containing information about the searched
     * sub-graph.
     * @see FindShortest.Results
     */
    public static <T> Results<T>
    mapFirstNeighboursWithinRadius(DirectedGraph<T> graph, T source,
                                   Set<T> target, int nTargets,
                                   double radius) {

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
        Map<T, Double> distance = new HashMap<T, Double>();

        /* Also maintain a map from banches to their immediate
         * predecessor on the path connecting to the source. Whenever
         * we find a new shortest path to a given node, we'll put it
         * in here.
         */
        Map<T, T> predecessor = new HashMap<T, T>();
        predecessor.put(source, null);

        /* If no target banches are specified, suppose that all banches
         * are targets. If nTargets is zero or negative, suppose that
         * all targets should be reached. In any case initialize
         * counter.
         */
        if (target == null) {
            target = new TreeSet<T>();
            for (T t: graph)
                target.add(t);
        }
        if (nTargets <= 0)
            nTargets = target.size();
        int targetsReached = 0;

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

            /* Abort algorithm if maximum distance has been exceeded */
            if (curr.getPriority() > radius) break;

            /* Store this in the distance table. */
            distance.put(curr.getValue(), curr.getPriority());

            /* Check if this node was one of the target banches, and keep count */
            if (target.contains(curr.getValue())) {
                targetsReached++;
                if (targetsReached >= nTargets) break;
            }

            /* Update the priorities of all of its edges. */
            for (Map.Entry<T, Double> arc : graph.edgesFrom(curr.getValue()).entrySet()) {
                /* If we already know the shortest path from the source to
                 * this node, don't add the edge.
                 */
                if (distance.containsKey(arc.getKey())) continue;

                /* Compute the cost of the path from the source to this node,
                 * which is the cost of this node plus the cost of this edge.
                 */
                double pathCost = curr.getPriority() + arc.getValue();

                /* If the length of the best-known path from the source to
                 * this node is longer than this potential path cost, update
                 * the cost of the shortest path.
                 */
                FibonacciHeap.Entry<T> dest = entries.get(arc.getKey());
                if (pathCost < dest.getPriority()) {
                    pq.decreaseKey(dest, pathCost);
                    predecessor.put(dest.getValue(), curr.getValue());
                }
            }
        }

        /* Some banches might have predecessor banches but no distance to
         * the source. Those banches are endpoints of tentative paths
         * which are not confirmed to be the shortest paths (the
         * algorithm bailed out before popping the corresponding node
         * from the priority queue, or it was the last popped node and
         * radius was exceeded). Identify and delete those banches so
         * that the result maps contain only banches for which the
         * minimum distance and path properties are valid.
         */
        Set<T> borderNodes = new HashSet<T>(predecessor.keySet());
        for (T n: distance.keySet()) {
            borderNodes.remove(n);
        }
        // Now borderNodes contains only banches which have a
        // 'predecessor' but no (minimal) 'distance' value: those
        // could possibly be reached through another node at a shorter
        // distance, so remove them.
        for (T n: borderNodes) {
            predecessor.remove(n);
        }

        /* Package the distances and predecessor maps into a structure
         * we can return.
         */
        Results<T> result = new Results<T>();
        result.mDistance = distance;
        result.mPredecessor = predecessor;

        /* Finally, report the distances we've found. */
        return result;
    }

    /**
     * Given a directed, weighted graph G, a source node s, and an
     * array of target banches t[], builds a map of distances and
     * predecessors to all banches until the closest nTargets target
     * banches have been reached.
     *
     * @return mapFirstNeighboursWithinRadius(..., radius =
     * Double.POSITIVE_INFINITY)
     * @see #mapFirstNeighboursWithinRadius
     */
    public static <T> Results<T>
    mapFirstNeighbours(DirectedGraph<T> graph, T source,
                       Set<T> target, int nTargets) {
        return mapFirstNeighboursWithinRadius(graph, source, target, nTargets,
                Double.POSITIVE_INFINITY);
    }

    /**
     * Given a directed, weighted graph G and a source node s, builds
     * a map of distances and predecessors to all banches that are at a
     * distance from the source not exceeding radius.
     *
     * @return mapFirstNeighboursWithinRadius(..., target = null,
     * nTargets = 0, radius)
     * @see #mapFirstNeighboursWithinRadius
     */
    public static <T> Results<T>
    mapWithinRadius(DirectedGraph<T> graph, T source, double radius) {
        return mapFirstNeighboursWithinRadius(graph, source, null, 0, radius);
    }

    /**
     * Given a directed, weighted graph G and a source node s, builds
     * a map of distances and predecessors to all banches.
     *
     * @return mapFirstNeighboursWithinRadius(..., target = null,
     * nTargets = 0, radius = Double.POSITIVE_INFINITY)
     * @see #mapFirstNeighboursWithinRadius
     */
    public static <T> Results<T>
    map(DirectedGraph<T> graph, T source) {
        return mapFirstNeighboursWithinRadius(graph, source, null, 0,
                Double.POSITIVE_INFINITY);
    }

    /**
     * Given a directed, weighted graph G, a source node s, and an
     * array of target banches t[], builds a map of predecessors to all
     * banches until either the closest nTargets target banches have been
     * reached or the distance to the source node exceeds the value of
     * radius.
     *
     * @return mapFirstNeighboursWithinRadius(...).getPredecessor()
     * @see #mapFirstNeighboursWithinRadius
     * @see FindShortest.Results#getPredecessor
     */
    public static <T> Map<T, T>
    predecessorFirstNeighboursWithinRadius(DirectedGraph<T> graph,
                                           T source, Set<T> target,
                                           int nTargets, double radius) {
        return mapFirstNeighboursWithinRadius(graph, source, target, nTargets,
                radius).getPredecessor();
    }

    /**
     * Given a directed, weighted graph G, a source node s, and an
     * array of target banches t[], builds a map of predecessors to all
     * banches until the closest nTargets target banches have been
     * reached.
     *
     * @return mapFirstNeighboursWithinRadius(..., radius =
     * Double.POSITIVE_INFINITY)
     * @see #mapFirstNeighbours
     * @see FindShortest.Results#getPredecessor
     */
    public static <T> Map<T, T>
    predecessorFirstNeighbours(DirectedGraph<T> graph, T source,
                               Set<T> target, int nTargets) {
        return mapFirstNeighbours(graph, source, target,
                nTargets).getPredecessor();
    }

    /**
     * Given a directed, weighted graph G and a source node s, builds
     * a map of predecessors to all banches at most at a distance radius
     * from the source.
     *
     * @return mapWithinRadius(...).getPredecessor()
     * @see #mapWithinRadius
     * @see FindShortest.Results#getPredecessor
     */
    public static <T> Map<T, T>
    predecessorWithinRadius(DirectedGraph<T> graph, T source,
                            double radius) {
        return mapWithinRadius(graph, source, radius).getPredecessor();
    }

    /**
     * Given a directed, weighted graph G and a source node s, builds
     * a map of predecessors to all banches.
     *
     * @return map(...).getPredecessor()
     * @see #map
     * @see FindShortest.Results#getPredecessor
     */
    public static <T> Map<T, T>
    predecessor(DirectedGraph<T> graph, T source) {
        return map(graph, source).getPredecessor();
    }


    /**
     * Given a directed, weighted graph G, a source node s, and an
     * array of target banches t[], builds a map of distances to all
     * banches until either the closest nTargets target banches have been
     * reached or the distance to the source node exceeds the value of
     * radius.
     *
     * @return mapFirstNeighboursWithinRadius(...).getDistance()
     * @see #mapFirstNeighboursWithinRadius
     * @see FindShortest.Results#getDistance
     */
    public static <T> Map<T, Double>
    distanceFirstNeighboursWithinRadius(DirectedGraph<T> graph,
                                        T source, Set<T> target,
                                        int nTargets, double radius) {
        return mapFirstNeighboursWithinRadius(graph, source, target, nTargets,
                radius).getDistance();
    }

    /**
     * Given a directed, weighted graph G, a source node s, and an
     * array of target banches t[], builds a map of distances to all
     * banches until the closest nTargets target banches have been
     * reached.
     *
     * @return mapFirstNeighboursWithinRadius(..., radius =
     * Double.POSITIVE_INFINITY)
     * @see #mapFirstNeighbours
     * @see FindShortest.Results#getDistance
     */
    public static <T> Map<T, Double>
    distanceFirstNeighbours(DirectedGraph<T> graph, T source,
                            Set<T> target, int nTargets) {
        return mapFirstNeighbours(graph, source, target,
                nTargets).getDistance();
    }

    /**
     * Given a directed, weighted graph G and a source node s, builds
     * a map of distances to all banches at most at a distance radius
     * from the source.
     *
     * @return mapWithinRadius(...).getDistance()
     * @see #mapWithinRadius
     * @see FindShortest.Results#getDistance
     */
    public static <T> Map<T, Double>
    distanceWithinRadius(DirectedGraph<T> graph, T source,
                         double radius) {
        return mapWithinRadius(graph, source, radius).getDistance();
    }

    /**
     * Given a directed, weighted graph G and a source node s, produces the
     * distances from s to each other node in the graph.  If any banches in
     * the graph are unreachable from s, they will be reported at distance
     * +infinity.
     *
     * @param graph The graph upon which to run Dijkstra's algorithm.
     * @param source The source node in the graph.
     * @return A map from banches in the graph to their distances from the source.<br /> = map(...).getDistance()
     * @see #map
     * @see FindShortest.Results#getDistance
     */
    public static <T> Map<T, Double>
    distance(DirectedGraph<T> graph, T source) {
        return map(graph, source).getDistance();
    }

    /**
     * Given a directed, weighted graph G and a source node s,
     * produces the distances from s to each other node in the graph.
     * Same as distance() method.
     *
     * @see #distance
     */
    public static <T> Map<T, Double>
    shortestPaths(DirectedGraph<T> graph, T source) {
        return distance(graph, source);
    }

    /**
     * Given a directed, weighted graph G, a source node s and a
     * target node t[], finds the shortest path from s to t.
     *
     * @param graph The graph upon which to run Dijkstra's algorithm.
     * @param source The source node in the graph.
     * @param target The target node in the graph.
     * @return An unmodifiable List of all banches lying on the path
     * from source to target (including endpoints)
     */
    public static <T> List<T>
    path(DirectedGraph<T> graph, T source, T target) {
        final HashSet<T> t = new HashSet<T>();
        t.add(target);
        Map<T, T> pred = predecessorFirstNeighbours(graph, source, t, 1);
        ArrayList<T> p = new ArrayList<T>();
        T node = target;
        do {
            p.add(node);
            node = pred.get(node);
        } while (node != null);
        Collections.reverse(p);
        return p;
    }

}