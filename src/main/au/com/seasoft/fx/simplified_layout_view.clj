(ns au.com.seasoft.fx.simplified-layout-view
  (:require
    [au.com.seasoft.layout.ham :as ham]
    [au.com.seasoft.graph.graph :as gr]
    [au.com.seasoft.graph.util :as util]
    [au.com.seasoft.layout.math :as math]
    [cljfx.ext.node :as fx.ext.node]
    [vlaaad.reveal.ext :as rx])
  (:import [javafx.scene.paint Color]))

(def options {::vertex-fill-colour  Color/BROWN
              ::vertex-rim-colour   Color/BLACK
              ::vertex-label-colour Color/WHITE
              ::edge-colour         Color/DARKCYAN
              ;; Sometimes get:
              ;; WARNING: CSS Error parsing '*{-fx-background-color: 0xd3d3d3ff}: Unexpected token '0xd' at [1,24]
              ;; In which case the bg will be white
              ;::background-colour   Color/LIGHTGRAY
              })

;; Found out that if circle goes in front of label can't see the label being focused on, even thou it is being
;; focused on.
(defn vertex-view-4
  [index [x y :as point]]
  (let [radius (::ham/radius ham/options)
        {:keys [::vertex-fill-colour ::vertex-label-colour ::vertex-rim-colour]} options]
    (println (str "In vertex-view-4 for point" point))
    {:fx/type  :stack-pane
     :layout-x x
     :layout-y y
     :children [{:fx/type :circle
                 :fill    vertex-fill-colour
                 :stroke  vertex-rim-colour
                 :radius  radius}
                {:fx/type rx/popup-view
                 :value   point
                 :desc    {:fx/type   :label
                           :text-fill vertex-label-colour
                           :text      (str index)}}]}))

(defn ->vertex-views
  [coords]
  (->> coords
       (mapv (fn [[k v]]
               (let [[x y] v]
                 (vertex-view-4 (util/kw->number k) [x y]))))))

(defn vertex-view? [{:fx/keys [type]}]
  (= :stack-pane type))

(defn pane-of-vertices-and-edges
  "Makes sure the edges come before the vertices"
  [children]
  {:fx/type  :pane
   :children children})

(def error-message
  {:fx/type  :stack-pane
   :children [{:fx/type :label
               :text    "Was not able to quickly create a nicely aligned graph. Is the graph connected?"
               :style   {:-fx-font-weight :bold}}]})

(defn coords->component [g coords]
  (let [view-vertices (->vertex-views coords)]
    (pane-of-vertices-and-edges view-vertices)))

(defn graph->component [g]
  (let [coords (ham/graph->coords g)]
    (if coords
      (coords->component g coords)
      error-message)))
