(ns au.com.seasoft.reveal.view
  (:require
    [au.com.seasoft.fx.layout-view :as layout-view]
    [au.com.seasoft.graph.graph :as graph]
    [vlaaad.reveal.action :as action]
    [vlaaad.reveal.ext :as rx]
    [au.com.seasoft.layout.ham :as ham]
    [au.com.seasoft.general.dev :as dev]))

(defn graph [{:keys [data]}]
  (layout-view/graph->component data))

(action/defaction ::view:graph [x]
                  (when (graph/graph? x)
                    (fn []
                      (let [g x
                            state (atom (ham/graph->coords g))]
                        (dev/log-on "Its a graph so s/be able to see :view/graph")
                        {:fx/type rx/observable-view
                         :ref     state
                         :fn      (fn [coords]
                                    (if coords
                                      (layout-view/coords->component g coords)
                                      layout-view/error-message))}))))



