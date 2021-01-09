(ns graph.layout.ham-test
  (:require
    [au.com.seasoft.graph.layout.ham :as ham]
    [clojure.test :refer :all])
  (:import (au.com.seasoft.ham InteropEdge InteropNode)))

(deftest interop-node-id
  (let [^InteropNode node (ham/node->interop-node 1)]
    (is (= 1 (.getId node)))))

(deftest interop-edge-ids
  (let [^InteropEdge edge (ham/edge->interop-edge [1 3])
        ^InteropNode source-node (.getSourceNode edge)
        ^InteropNode target-node (.getTargetNode edge)]
    (is (= 1 (.getId source-node)))
    (is (= 3 (.getId target-node)))))

(comment
  (run-tests)
  )

