(ns au.com.seasoft.graph.example-data
  "Example data used in tests and example")

;; TODO
;; Put some of these under example and some under test, even replicating if necessary. Have tests for all so
;; that all have a purpose (or get rid of them).
;; Hmm - Actually nice to have example graphs for testing Reveal when only have this library

(def simple-graph-1
  "Canonical form now using. Easier to update and order of the target nodes is not important so using a map
  represents that fact better. It is a ::gr/graph"
  {:1 {:2 {:weight 1} :3 {:weight 2}}
   :2 {:4 {:weight 4}}
   :3 {:4 {:weight 2}}
   :4 {}
   })

(def full-graph
  {:1 {:4 {:weight 10}}
   :2 {:1 {:weight 10} :3 {:weight 10} :4 {:weight 10}}
   :3 {:1 {:weight 10} :4 {:weight 10}}
   :4 {}
   })

(def unreachable-nodes-graph
  ":2 and :4 can't be reached from any other nodes except :2 and :4. Despite this :2 and :4 are not on an island
  on their own"
  {:12 {}
   :11 {:12 {:weight 10}}
   :10 {:12 {:weight 3}}
   :4  {:2 {:weight 4}}
   :7  {:6 {:weight 1} :8 {:weight 11}}
   :1  {:3 {:weight 5}}
   :8  {:9 {:weight 20}}
   :9  {:10 {:weight 17} :11 {:weight 5}}
   :2  {:1 {:weight 10}}
   :5  {:6 {:weight 9} :7 {:weight 3}}
   :3  {:5 {:weight 6} :8 {:weight 2}}
   :6  {}})

(def nodes-graph-1
  {:12 {}
   :11 {:12 {:weight 10}}
   :10 {:12 {:weight 3}}
   :4  {:2 {:weight 4} :7 {:weight 5}}
   :7  {:6 {:weight 1} :8 {:weight 11}}
   :1  {:3 {:weight 5}}
   :8  {:9 {:weight 20}}
   :9  {:10 {:weight 17} :11 {:weight 5}}
   :2  {:1 {:weight 10}}
   :5  {:6 {:weight 9} :7 {:weight 3}}
   :3  {:4 {:weight 7} :5 {:weight 6} :8 {:weight 2}}
   :6  {}})

;;
;; There's no reason for weight to be in a map. So test this
;;
(def nodes-graph-2
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

(def ints-nodes-graph-1
  {12 {}
   11 {12 {:weight 10}}
   10 {12 {:weight 3}}
   4  {2 {:weight 4} 7 {:weight 5}}
   7  {6 {:weight 1} 8 {:weight 11}}
   1  {3 {:weight 5}}
   8  {9 {:weight 20}}
   9  {10 {:weight 17} 11 {:weight 5}}
   2  {1 {:weight 10}}
   5  {6 {:weight 9} 7 {:weight 3}}
   3  {4 {:weight 7} 5 {:weight 6} 8 {:weight 2}}
   6  {}})

(def ints-nodes-graph-2
  {12123 {}
   11 {12123 10}
   10 {12123 3}
   4  {2 4 7 5}
   7  {6 1 8 11}
   1  {3 5}
   8  {"word" 20}
   "word"  {10 17 11 5}
   2  {1 10}
   5  {6 9 7 3}
   3  {4 7 5 6 8 2}
   6  {}})

(def simple-graph-2
  [[:1 {:2 {:weight 1} :3 {:weight 2}}]
   [:2 {:4 {:weight 4}}]
   [:3 {:4 {:weight 2}}]
   [:4 {}]
   ])

(def simple-graph-3
  [[:1 [[:2 1] [:3 2]]]
   [:2 [[:4 4]]]
   [:3 [[:4 2]]]
   [:4 []]
   ])

(def ints-nodes-graph-3
  [[12123 {}]
   [11 {12123 10}]
   [10 {12123 3}]
   [4 {2 4 7 5}]
   [7 {6 1 8 11}]
   [1 {3 5}]
   [8 {"word" 20}]
   ["word" {10 17 11 5}]
   [2 {1 10}]
   [5 {6 9 7 3}]
   [3 {4 7 5 6 8 2}]
   [6 {}]])

(def ints-nodes-graph-4
  [[12123 []]
   [11 [[12123 10]]]
   [10 [[12123 3]]]
   [4 [[2 4] [7 5]]]
   [7 [[6 1] [8 11]]]
   [1 [[3 5]]]
   [8 [["word" 20]]]
   ["word" [[10 17] [11 5]]]
   [2 [[1 10]]]
   [5 [[6 9] [7 3]]]
   [3 [[4 7] [5 6] [8 2]]]
   [6 []]])

(def letter-nodes-graph-1
  {:ll {}
   :k {:ll {:weight 10}}
   :j {:ll {:weight 3}}
   :d  {"A really long name" {:weight 4} :gggg {:weight 5}}
   :gggg  {:f {:weight 1} :hh {:weight 11}}
   :aaa  {:c {:weight 5}}
   :hh  {:i {:weight 20}}
   :i  {:j {:weight 17} :k {:weight 5}}
   "A really long name"  {:aaa {:weight 10}}
   :e  {:f {:weight 9} :gggg {:weight 3}}
   :c  {:d {:weight 7} :e {:weight 6} :hh {:weight 2}}
   :f  {}})

(def letter-nodes-graph-2
  {:l {}
   :k {:l 10}
   :j {:l 3}
   :d {:b 4 :g 5}
   :g {:f 1 :h 11}
   :a {:c 5}
   :h {:i 20}
   :i {:j 17 :k 5}
   :b {:a 10}
   :e {:f 9 :g 3}
   :c {:d 7 :e 6 :h 2}
   :f {}})

(def not-connected-graph
  ":8 does not go to :9, hence there are 2 islands. Can't be drawn by the algorithm we use so the error message
  is elicited."
  {:12 {}
   :11 {:12 {:weight 10}}
   :10 {:12 {:weight 3}}
   :4  {:2 {:weight 4} :7 {:weight 5}}
   :7  {:6 {:weight 1} :8 {:weight 11}}
   :1  {:3 {:weight 5}}
   :8  {}
   :9  {:10 {:weight 17} :11 {:weight 5}}
   :2  {:1 {:weight 10}}
   :5  {:6 {:weight 9} :7 {:weight 3}}
   :3  {:4 {:weight 7} :5 {:weight 6} :8 {:weight 2}}
   :6  {}})

(def grouped-by-graph-map-entry
  [:4 [[:4 {:2 {:weight 4}}]
       [:4 {:3 {:weight 2}}]]])

(comment
  (tap> nodes-graph-1))