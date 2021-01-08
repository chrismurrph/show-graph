(ns graph.graph-test
  (:require
    [au.com.seasoft.graph.example-data :as example]
    [au.com.seasoft.graph.graph :as graph]
    [clojure.test :refer :all]
    ))

(deftest nodes-in-graphs-edges
  (= #{:12 :11 :10 :4 :7 :1 :8 :9 :2 :5 :3 :6}
     (is = (graph/nodes-in-edges example/nodes-graph-1))))

(deftest all-example-graphs-are-graphs
  (is = (every? graph/graph? [example/simple-graph example/full-graph example/nodes-graph-1 example/unreachable-nodes-graph])))

(comment
  (run-tests)
  )

