(ns graph.fx.layout-view-test
  (:require
    [graph.fx.view-util :as view-util]
    [au.com.seasoft.graph.example-data :as example-data]
    [au.com.seasoft.graph.fx.layout-view :as layout-view]
    [clojure.test :refer :all]
    [au.com.seasoft.general.dev :as dev]))

(deftest vertex-view-index
  (is (= (-> (layout-view/vertex-view-simple {:x 10 :y 10 :id :3})
             layout-view/vertex-view->ordered-int)
         3)))

(deftest coords->edge-position
  (is (= [39.39339828220179 39.39339828220179] (layout-view/arrow-position [0 0] [50 50]))))

(defn see-arrow []
  (view-util/see-component-simply (layout-view/edge-view-arrow [20 50] 90)))

(defn see-graph []
  (view-util/see-component-simply (layout-view/graph->component example-data/simple-graph-2 false)))

(defn see-graph-data-structure []
  (dev/pp (->> (layout-view/graph->component example-data/simple-graph-2 false)
               :content
               :children
               (filterv (fn [{:keys [alignment] :fx/keys [type]}]
                          (and (= type :stack-pane)
                               (= alignment :center-left)
                               ))))))

(comment
  (see-graph-data-structure)
  (see-graph)
  (see-arrow)
  (run-tests)
  )

