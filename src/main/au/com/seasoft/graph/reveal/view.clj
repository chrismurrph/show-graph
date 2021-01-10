(ns au.com.seasoft.graph.reveal.view
  (:require
    [au.com.seasoft.graph.fx.layout-view :as layout-view]
    [au.com.seasoft.graph.graph :as graph]
    [vlaaad.reveal.action :as action]
    [vlaaad.reveal.ext :as rx]
    [au.com.seasoft.graph.layout.ham :as ham]))

(action/defaction ::view:graph [x]
                  (when (graph/graph? x)
                    (fn []
                      (let [g x
                            state (atom (ham/graph->coords g))]
                        {:fx/type rx/observable-view
                         :ref     state
                         :fn      (fn [coords]
                                    (if coords
                                      (layout-view/coords->component g coords true)
                                      layout-view/error-message))}))))



