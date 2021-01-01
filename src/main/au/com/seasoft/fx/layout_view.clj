(ns au.com.seasoft.fx.layout-view
  (:require
    [au.com.seasoft.layout.ham :as ham]
    [au.com.seasoft.graph.graph :as gr]
    [au.com.seasoft.graph.util :as util]
    [au.com.seasoft.graph.example-data :as example-data]
    [au.com.seasoft.layout.math :as math]
    [cljfx.api :as fx]
    [com.rpl.specter :as sp])
  (:import [javafx.scene.paint Color]))

(def options {::vertex-fill-colour  Color/BROWN
              ::vertex-rim-colour   Color/BLACK
              ::vertex-label-colour Color/WHITE
              ::edge-colour         Color/DARKCYAN
              ::background-colour   Color/LIGHTGRAY
              })

(defn vertex-view->index-number
  "The thing on the screen can be identified by a number"
  [{:keys [children] :as vertex-view}]
  (let [{:keys [text] :as label-child} (first (filter (comp #{:label} :fx/type) children))]
    (Long/parseLong text)))

(defn vertex-view
  [index [x y :as point]]
  (let [radius (::ham/radius ham/options)
        {:keys [::vertex-fill-colour ::vertex-label-colour ::vertex-rim-colour]} options]
    {:fx/type  :stack-pane
     :layout-x x
     :layout-y y
     :children [{:fx/type :circle
                 :fill    vertex-fill-colour
                 :stroke  vertex-rim-colour
                 :radius  radius}
                {:fx/type   :label
                 :text-fill vertex-label-colour
                 :text      (str index)}]}))

(defn ->vertex-views
  [coords]
  (->> coords
       (map (fn [[k v]]
              (let [[x y] v
                    view (vertex-view (util/kw->number k) [x y])]
                view)))))

(defn edge-view-arrow [[x y :as central-point] rotate-by-degrees]
  (let [triangle-x-radius 4
        triangle-y-radius 5
        ;; If central point was [5 7] we would want no 'transform' at all. We are moving a triangle that's in the
        ;; top left corner to the central point.
        transform-x (- x triangle-x-radius)
        transform-y (- y triangle-y-radius)
        {:keys [::edge-colour]} options]
    {:fx/type  :group
     :rotate   rotate-by-degrees
     :children [{:fx/type :polygon
                 :stroke  edge-colour
                 :fill    edge-colour
                 :points  [(+ transform-x triangle-x-radius)
                           (+ transform-y 0)
                           (+ transform-x 0)
                           (+ transform-y (* triangle-y-radius 2))
                           (+ transform-x (* triangle-x-radius 2))
                           (+ transform-y (* triangle-y-radius 2))]}]}))

(defn arrow-position
  "Given an edge, returns where to put the arrow"
  [[from-x from-y :as from] [to-x to-y :as to]]
  (let [radius (::ham/radius ham/options)
        arrowhead-base (/ radius 2)
        x-delta (- to-x from-x)
        y-delta (- to-y from-y)
        length (math/sqrt (+ (math/pow x-delta 2) (math/pow y-delta 2)))
        up-to-arrow-point (- length (+ arrowhead-base radius))
        proportion (/ up-to-arrow-point length)
        up-to-x-delta (* proportion x-delta)
        up-to-y-delta (* proportion y-delta)
        ]
    [(+ from-x up-to-x-delta) (+ from-y up-to-y-delta)]))

(defn triangle-view [[from-x from-y :as from] [to-x to-y :as to]]
  (-> (arrow-position from to)
      (edge-view-arrow (+ 90 (math/line-slope from to)))))

(defn edge-view [[from-x from-y :as from] [to-x to-y :as to]]
  (let [{:keys [::edge-colour]} options]
    {:fx/type  :path
     :stroke   edge-colour
     :elements [{:fx/type :move-to
                 :x       from-x :y from-y}
                {:fx/type :line-to
                 :x       to-x :y to-y}]}))

(defn shift-point [amount [x y]]
  [(+ amount x) (+ amount y)])

(defn ->edge-views
  "Get all the edges from the graph. Then replace the 2 nodes of each with [x y]. Then have enough for an edge-view if
  alter for the radius"
  [graph coords]
  (let [radius (::ham/radius ham/options)]
    (->> (gr/pair-edges graph)
         (map (fn [[source target]]
                [(get coords source) (get coords target)]))
         (map (fn [[from to]]
                (edge-view (shift-point radius from) (shift-point radius to)))))))

(defn ->arrow-views
  "Get all the edges from the graph. Then replace the 2 nodes of each with [x y]. Then have enough for an triangle-view if
  alter for the radius"
  [graph coords]
  (let [radius (::ham/radius ham/options)]
    (->> (gr/pair-edges graph)
         (map (fn [[source target]]
                [(get coords source) (get coords target)]))
         (map (fn [[from to]]
                (triangle-view (shift-point radius from) (shift-point radius to)))))))

(defn edge-view? [{:fx/keys [type]}]
  (= :path type))

(defn vertex-view? [{:fx/keys [type]}]
  (= :stack-pane type))

(defn pane-of-vertices-and-edges
  "Makes sure the edges come before the vertices"
  [children]
  (let [{:keys [::background-colour]} options]
    {:fx/type  :pane
     :style    {:-fx-background-color background-colour}
     :children (sort-by (fn [view]
                          (cond
                            (edge-view? view) -1
                            (vertex-view? view) 1))
                        children)}))

(def error-message
  {:fx/type  :stack-pane
   :children [{:fx/type :label
               :text    "Was not able to quickly create a nicely aligned graph. Is the graph connected?"
               :style   {:-fx-font-weight :bold}}]})

(defn graph->component [g]
  (let [coords (ham/graph->coords g)]
    (if coords
      (let [view-vertices (->vertex-views coords)
            view-edges (->edge-views g coords)
            view-arrows (->arrow-views g coords)
            widgets (concat view-vertices view-edges view-arrows)]
        (pane-of-vertices-and-edges widgets))
      error-message)))
