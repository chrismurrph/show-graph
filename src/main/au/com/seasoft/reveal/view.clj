(ns au.com.seasoft.reveal.view
  (:require
    [au.com.seasoft.fx.layout-view :as layout-view]
    [au.com.seasoft.graph.graph :as graph]
    [vlaaad.reveal.action :as action]))

(defn graph [{:keys [data]}]
  (layout-view/graph->component data))

(action/defaction ::view:graph [x]
                  (when (graph/graph? x)
                    (constantly {:fx/type graph :data x})))

