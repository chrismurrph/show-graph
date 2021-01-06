(ns fx.example-layouts
  (:require
    [fx.view-util :as view-util]
    [au.com.seasoft.graph.example-data :as example-data]
    [au.com.seasoft.fx.layout-view :as layout]))

(defn x-3 []
  (let [g example-data/ints-nodes-graph]
    (view-util/see-something-simply (layout/graph->component g))))
