(ns graph.layout.graph-key-swap-test
  (:require
    [au.com.seasoft.graph.layout.graph-key-swap :as graph-key-swap]
    [clojure.test :refer :all]
    [au.com.seasoft.general.dev :as dev]
    [au.com.seasoft.graph.example-data :as example-data]
    [au.com.seasoft.graph.util :as util]))

(defn view-transformation []
  (let [g example-data/simple-graph-3
        g (util/ensure-is-map g)
        [strict-graph int->k] (graph-key-swap/->graph g)]
    (dev/pp g)
    (dev/pp int->k)
    (dev/pp strict-graph)))

(deftest swapping-works-1
  (let [g example-data/nodes-graph-1
        [strict-graph int->k] (graph-key-swap/->graph g)]
    (is (= (int->k keys set) (strict-graph keys set)))))

(deftest swapping-works-2
  (let [g example-data/simple-graph-2
        g (util/ensure-is-map g)
        [strict-graph int->k] (graph-key-swap/->graph g)]
    (is (= (int->k keys set) (strict-graph keys set)))))

(deftest swapping-works-3
  (let [g example-data/simple-graph-3
        g (util/ensure-is-map g)
        [strict-graph int->k] (graph-key-swap/->graph g)]
    (is (= (int->k keys set) (strict-graph keys set)))))

(comment
  (run-tests)
  )

