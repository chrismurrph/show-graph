(ns graph.layout.graph-key-swap-test
  (:require
    [au.com.seasoft.graph.layout.graph-key-swap :as graph-key-swap]
    [clojure.test :refer :all]
    [au.com.seasoft.general.dev :as dev]
    [au.com.seasoft.graph.example-data :as example-data]))

(defn view-transformation []
  (let [g example-data/nodes-graph-1
        [strict-graph int->k] (graph-key-swap/->graph g)]
    (dev/pp g)
    (dev/pp int->k)
    (dev/pp strict-graph)))

(deftest swapping-works
  (let [g example-data/nodes-graph-1
        [strict-graph int->k] (graph-key-swap/->graph g)]
    (= (int->k keys set) (strict-graph keys set))))

(comment
  (run-tests)
  )

