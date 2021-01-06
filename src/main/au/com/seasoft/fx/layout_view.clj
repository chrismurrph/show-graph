(ns au.com.seasoft.fx.layout-view
  (:require
    [au.com.seasoft.layout.ham :as ham]
    [au.com.seasoft.graph.graph :as gr]
    [au.com.seasoft.graph.util :as util]
    [au.com.seasoft.layout.math :as math]
    [vlaaad.reveal.ext :as rx])
  (:import [javafx.scene.paint Color]))

(def colour-options
  {::vertex-fill-colour  Color/BROWN
   ::vertex-rim-colour   Color/BLACK
   ::vertex-label-colour Color/WHITE
   ::edge-colour         Color/DARKCYAN})

#_(defn vertex-view->index-number
  "The thing on the screen can be identified by a number"
  [{:keys [children] :as vertex-view}]
  (let [{:keys [text] :as label-child} (first (filter (comp #{:label} :fx/type) children))]
    (Long/parseLong text)))

(defn vertex-view->props [view]
  (->> view
       :children
       (some :value)))

(defn vertex-view->id [view]
  (let [res (-> view
                vertex-view->props
                :id
                util/kw->string)]
    (assert (string? res) ["Not found id from props" (vertex-view->props view)])
    res))

(defn vertex-view
  [{:keys [x y id] :as props}]
  (assert (keyword? id) ["Props must have an :id that's a keyword" props])
  (let [radius (::ham/radius ham/options)
        {:keys [::vertex-fill-colour ::vertex-label-colour ::vertex-rim-colour]} colour-options]
    ;(println (str "In vertex-view for props" props))
    {:fx/type  :stack-pane
     :layout-x x
     :layout-y y
     ;; Important that the label comes last so the user can visually tab to it
     :children [{:fx/type :circle
                 :fill    vertex-fill-colour
                 :stroke  vertex-rim-colour
                 :radius  radius}
                {:fx/type rx/popup-view
                 :value   props
                 :desc    {:fx/type   :label
                           :text-fill vertex-label-colour
                           :text      (util/kw->string id)}}]}))

(defn ->vertex-views
  [coords]
  (->> coords
       (map (fn [[k v]]
              (let [view (vertex-view v)]
                view)))))

(defn edge-view-arrow [[x y :as central-point] rotate-by-degrees]
  (let [triangle-x-radius 4
        triangle-y-radius 5
        ;; If central point was [5 7] we would want no 'transform' at all. We are moving a triangle that's in the
        ;; top left corner to the central point.
        transform-x (- x triangle-x-radius)
        transform-y (- y triangle-y-radius)
        {:keys [::edge-colour]} colour-options]
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
        up-to-y-delta (* proportion y-delta)]
    [(+ from-x up-to-x-delta) (+ from-y up-to-y-delta)]))

(defn triangle-view [[from-x from-y :as from] [to-x to-y :as to]]
  (-> (arrow-position from to)
      (edge-view-arrow (+ 90 (math/line-slope from to)))))

(defn edge-view [[from-x from-y :as from] [to-x to-y :as to]]
  (let [{:keys [::edge-colour]} colour-options]
    {:fx/type  :path
     :stroke   edge-colour
     :elements [{:fx/type :move-to :x from-x :y from-y}
                {:fx/type :line-to :x to-x :y to-y}]}))

(defn shift-point [amount {:keys [x y] :as props}]
  (assert (number? x) ["Expected a map with :x in it" props])
  (assert (number? y) ["Expected a map with :y in it" props])
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

(defn pane-of-vertices-and-edges-1
  "Makes sure the edges come before the vertices"
  [children]
  {:fx/type  :pane
   :children (vec (sort-by (fn [view]
                             (cond
                               (edge-view? view)
                               -1

                               (vertex-view? view)
                               (vertex-view->id view)))
                           children))})

#_(defn pane-of-vertices-and-edges-2
  "Makes sure the edges come before the vertices"
  [children]
  {:fx/type  :pane
   :children (vec (sort (fn [view-a view-b]
                             (cond
                               (edge-view? view)
                               -1

                               (vertex-view? view)
                               (vertex-view->id view)))
                           children))})

(def error-message
  {:fx/type  :stack-pane
   :children [{:fx/type :label
               :text    "Was not able to quickly create a nicely aligned graph. Is the graph connected?"
               :style   {:-fx-font-weight :bold}}]})

(defn coords->component [g coords]
  (let [view-vertices (->vertex-views coords)
        view-edges (->edge-views g coords)
        view-arrows (->arrow-views g coords)
        widgets (concat view-vertices view-edges view-arrows)]
    (pane-of-vertices-and-edges-1 widgets)))

(defn graph->component [g]
  (let [coords (ham/graph->coords g)]
    (if coords
      (coords->component g coords)
      error-message)))
