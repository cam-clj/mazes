(ns cam-clj.mazes.graph)

;; Note: this is not completely general, as the add-edge and remove-edge
;; signatures do not allow for multiple edges between two nodes.
(defprotocol PGraph
  (nodes [this] "Return the set of nodes belonging to this graph.")
  (edges [this] "Return the set of edges belonging to this graph.")
  (add-node [this node] "Add a node to the graph.")
  (remove-node [this node] "Remove a node from the graph.")
  (add-edge [this node1 node2] "Add an edge connecting `node1` and `node2`")
  (remove-edge [this node1 node2] "Remove an edge connecting `node1` and `node2`")
  (neighbours [this node] "Return the set of neighbours of `node` in this graph."))

(declare ->NeighbourMapGraph)

;; A simple undirected graph represented by a map from each node to its
;; neighbours. Note that this does not support multiple edges between nodes.
(defrecord NeighbourMapGraph [neighbour-map]
  PGraph

  (nodes
   [_]
   (keys neighbour-map))

  (edges
   [_]
   (reduce (fn [edges [node1 node2]]
             (if (contains? edges [node2 node1])
               edges
               (conj edges [node1 node2])))
           #{}
           (for [[node1 neighbours] neighbour-map node2 neighbours] (vector node1 node2))))

  (add-node
   [_ node]
   (->NeighbourMapGraph (assoc neighbour-map node #{})))

  (remove-node
   [_ node]
   (->NeighbourMapGraph (reduce (fn [m n] (update-in m [n] disj node))
                                (dissoc neighbour-map node)
                                (neighbour-map node))))

  (add-edge
   [_ node1 node2]
   (->NeighbourMapGraph (-> neighbour-map
                            (update-in [node1] conj node2)
                            (update-in [node2] conj node1))))

  (remove-edge
   [_ node1 node2]
   (->NeighbourMapGraph (-> neighbour-map
                            (update-in [node1] disj node2)
                            (update-in [node2] disj node1))))

  (neighbours
   [_ node]
   (neighbour-map node)))

(def EMPTY_GRAPH (->NeighbourMapGraph {}))


;; (def G (-> EMPTY_GRAPH
;;            (add-node :a)
;;            (add-node :b)
;;            (add-node :c)
;;            (add-node :d)
;;            (add-edge :a :b)
;;            (add-edge :a :c)
;;            (add-edge :c :d)))

;; (edges G)

;; (nodes G)

;; (neighbours (add-edge G :b :d) :d)



