(ns fx.layout-view-test
  (:require
    [fx.view-util :as view-util]
    [au.com.seasoft.fx.layout-view :as layout-view]
    [clojure.test :refer :all]
    [au.com.seasoft.graph.example-data :as example-data]))

(deftest vertex-view-index
  (is (= (-> (layout-view/vertex-view 3 {:x 10 :y 10})
             layout-view/vertex-view->index-number)
         3)))

(deftest coords->edge-position
  (is (= [39.39339828220179 39.39339828220179] (layout-view/arrow-position [0 0] [50 50]))))

(defn see-arrow []
  (view-util/see-something-1 (layout-view/edge-view-arrow [20 50] 90)))

(defn see-graph []
  (view-util/see-something-1 (layout-view/graph->component example-data/simple-graph)))

(comment
  (see-graph)
  (see-arrow)
  (run-tests)
  )

