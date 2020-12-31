(ns fx.layout-view-test
  (:require
    [au.com.seasoft.fx.layout-view :as layout-view]
    [clojure.test :refer :all]
    [au.com.seasoft.graph.example-data :as example-data]
    [cljfx.api :as fx]
    [au.com.seasoft.graph.util :as util]
    [com.rpl.specter :as sp]
    [clojure.pprint :as pprint]))

(deftest vertex-view-index
  (is (= (-> (layout-view/vertex-view 3 [10 10])
             layout-view/vertex-view->index-number)
         3)))

(deftest coords->edge-position
  (is (= [39.39339828220179 39.39339828220179] (layout-view/arrow-position [0 0] [50 50]))))

(defn pp
  ([n x]
   (binding [pprint/*print-right-margin* n]
     (-> x pprint/pprint)))
  ([x]
   (pp 180 x)))

(defn see-something [something]
  (assert something "Nothing to see")
  (if (seq (sp/select (sp/walker nil?) something))
    (do
      (pp something)
      (throw (Error. "nil values not allowed")))
    (fx/on-fx-thread
      (fx/create-component
        {:fx/type    :stage
         :min-height 400
         :min-width  600
         :showing    true
         :scene      {:fx/type :scene
                      :root    something}}))))

(defn see-arrow []
  (see-something (layout-view/edge-view-arrow [20 50] 90)))

(defn see-graph []
  (see-something (layout-view/graph->component example-data/simple-graph)))

(comment
  (see-graph)
  (see-arrow)
  (run-tests)
  )

