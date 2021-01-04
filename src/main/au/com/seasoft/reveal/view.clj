(ns au.com.seasoft.reveal.view
  (:require
    [au.com.seasoft.fx.layout-view :as layout-view]
    [au.com.seasoft.fx.grid-view :as grid-view]
    [au.com.seasoft.fx.pane-view :as pane-view]
    [au.com.seasoft.graph.graph :as graph]
    [vlaaad.reveal.action :as action]
    [vlaaad.reveal.ext :as rx]
    [au.com.seasoft.general.dev :as dev]
    [au.com.seasoft.layout.ham :as ham]))

(defn graph [{:keys [data]}]
  (layout-view/graph->component data))

;; Works fine but we need to follow the chess example
#_(action/defaction ::view:graph [x]
                    (when (graph/graph? x)
                      (constantly
                        {:fx/type graph
                         :data    x})))

(action/defaction ::view:graph [x]
                  (when (graph/graph? x)
                    (fn []
                      (let [g x
                            state (atom (ham/graph->coords g))]
                        {:fx/type rx/observable-view
                         :ref     state
                         :fn      (fn [coords]
                                    (if coords
                                      (do
                                        (layout-view/coords->component g coords)
                                        #_pane-view/chess-view
                                        )
                                      layout-view/error-message))}))))



