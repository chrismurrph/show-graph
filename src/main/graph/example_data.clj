(ns graph.example-data
  "Example data used in tests")

(def simple-graph
  "Canonical form now using. Easier to update and order of the target nodes is not important so using a map
  represents that fact better. It is a ::gr/graph"
  {:1 {:2 1 :3 2}
   :2 {:4 4}
   :3 {:4 2}
   :4 {}
   })

(def full-graph
  {:1 {:4 10}
   :2 {:1 10 :3 10 :4 10}
   :3 {:1 10 :4 10}
   :4 {}
   })

(def unreachable-nodes-graph
  ":2 and :4 can't be reached from any other nodes except :2 and :4. Despite this :2 and :4 are not on an island
  on their own"
  {:12 {}
   :11 {:12 10}
   :10 {:12 3}
   :4  {:2 4}
   :7  {:6 1 :8 11}
   :1  {:3 5}
   :8  {:9 20}
   :9  {:10 17 :11 5}
   :2  {:1 10}
   :5  {:6 9 :7 3}
   :3  {:5 6 :8 2}
   :6  {}})

(def nodes-graph
  {:12 {}
   :11 {:12 10}
   :10 {:12 3}
   :4  {:2 4 :7 5}
   :7  {:6 1 :8 11}
   :1  {:3 5}
   :8  {:9 20}
   :9  {:10 17 :11 5}
   :2  {:1 10}
   :5  {:6 9 :7 3}
   :3  {:4 7 :5 6 :8 2}
   :6  {}})

(def grouped-by-graph-map-entry
  [:4 [[:4 {:2 4}]
       [:4 {:3 2}]]])

(comment
  (tap> nodes-graph))