(ns au.com.seasoft.graph.fx.layout-view
  (:require
    [au.com.seasoft.graph.layout.ham :as ham]
    [au.com.seasoft.graph.graph :as gr]
    [au.com.seasoft.graph.util :as util]
    [au.com.seasoft.graph.layout.math :as math]
    [vlaaad.reveal.ext :as rx]
    [au.com.seasoft.general.dev :as dev])
  (:import [javafx.scene.paint Color]))

(def colour-options
  {::vertex-fill-colour  Color/BROWN
   ::vertex-rim-colour   Color/BLACK
   ::vertex-label-colour Color/WHITE
   ::edge-colour         Color/DARKCYAN})

(defn vertex-view->props [view]
  (let [children (:children view)]
    (or (some :value children)
        ;; In a simple view a node doesn't have props as such. Also note that props can be anything, even a scalar,
        ;; so we say 'props' (with single quotes)
        (some :text children))))

(defn vertex-view->ordered-int
  "When displayed it is what the user gave as the id, as a string, with the kw not showing the colon.
  See util/->string
  So if the keywords were actually numbers then tabbing will go 1 10 11 12 2..., which is wrong.
  So here, just for the purposes of ordering, we return the underlying int? if that's what it is"
  [view]
  (let [props (vertex-view->props view)
        _ (assert props ["No 'props' found in view" view])
        id (or (:id props) props)
        id-as-int (cond
                    (keyword? id)
                    (let [s (util/kw->string id)]
                      (try (Long/parseLong s)
                           (catch Exception ex
                             nil)))
                    ;; Just in case the user provides string keys when underlying they are all numbers. Perhaps going
                    ;; too far here...
                    (string? id)
                    (try (Long/parseLong id)
                         (catch Exception ex
                           nil)))]
    (or id-as-int id)))

(defn vertex-view-simple
  [{:keys [x y id] :as props}]
  (let [text (util/->string id)
        len (count text)
        radius (::ham/radius ham/options)
        {:keys [::vertex-fill-colour ::vertex-label-colour ::vertex-rim-colour]} colour-options]
    (cond-> {:fx/type  :stack-pane
             :layout-x x
             :layout-y y
             :children [{:fx/type :circle
                         :fill    vertex-fill-colour
                         :stroke  vertex-rim-colour
                         :radius  radius}
                        {:fx/type   :label
                         :text-fill vertex-label-colour
                         :text      text}]}
            (> len 3) ((fn [stack-pane]
                         (-> stack-pane
                             (assoc :alignment :center-left)
                             (assoc-in [:children 1 :style] {:-fx-padding [0 0 0 4]})))))))

(defn vertex-view-reveal
  [{:keys [x y id] :as props}]
  (let [text (util/->string id)
        len (count text)
        radius (::ham/radius ham/options)
        {:keys [::vertex-fill-colour ::vertex-label-colour ::vertex-rim-colour]} colour-options]
    (cond-> {:fx/type  :stack-pane
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
                                   :text      text}}]}
            (> len 3) ((fn [stack-pane]
                         (-> stack-pane
                             (assoc :alignment :center-left)
                             (assoc-in [:children 1 :desc :style] {:-fx-padding [0 0 0 4]})))))))

(defn ->vertex-views
  [coords reveal?]
  (->> coords
       (map (fn [[k v]]
              (let [view (if reveal?
                           (vertex-view-reveal v)
                           (vertex-view-simple v))]
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
                           transform-y
                           transform-x
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
                [(dev/safe-get coords source) (dev/safe-get coords target)]))
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

(defn -edge-view? [{:fx/keys [type]}]
  (= :path type))

(defn vertex-view? [{:fx/keys [type]}]
  (= :stack-pane type))

(defn -arrow-view? [{:keys [rotate] :fx/keys [type]}]
  (and rotate (= :group type)))

(def background-view? (some-fn -edge-view? -arrow-view?))

(defn ->type-order
  "Only used when the comparison is between 2 different types"
  [x]
  (cond
    (string? x) 3
    (keyword? x) 2
    (number? x) 1
    :else 0))

(defn diff-types-order
  "string? comes before keyword? comes before number?"
  [a b]
  (let [a-order (->type-order a)
        b-order (->type-order b)]
    (- b-order a-order)))

(defn pane-of-vertices-and-edges
  "Makes sure the edges come before the vertices"
  [children]
  {:fx/type  :pane
   :children (vec (sort (fn [view-a view-b]
                          (cond
                            ;; Edge views come first so vertex views can draw over them
                            (and (background-view? view-a) (vertex-view? view-b))
                            -1
                            (and (vertex-view? view-a) (background-view? view-b))
                            1

                            ;; No need for an opinion on the ordering of edge views
                            (and (background-view? view-a) (background-view? view-b))
                            0

                            ;; If its a number underneath, lets use that ordering
                            (and (vertex-view? view-a) (vertex-view? view-b))
                            (let [a (vertex-view->ordered-int view-a)
                                  b (vertex-view->ordered-int view-b)]
                              (if (= (type a) (type b))
                                (compare a b)
                                (diff-types-order a b)))

                            :else
                            0
                            ))
                        children))})

(def error-message
  {:fx/type  :stack-pane
   :children [{:fx/type :label
               :text    "Was not able to quickly create a nicely aligned graph. Is the graph connected?"
               :style   {:-fx-font-weight :bold}}]})

(defn coords->component [g coords reveal?]
  (let [g (cond->> g
                   ((complement map?) g) (into {}))
        view-vertices (->vertex-views coords reveal?)
        view-edges (->edge-views g coords)
        view-arrows (->arrow-views g coords)
        widgets (concat view-vertices view-edges view-arrows)]
    {:fx/type      :scroll-pane
     :fit-to-width true
     :content      (pane-of-vertices-and-edges widgets)}))

(defn graph->component [g reveal?]
  (let [g (cond->> g
                   ((complement map?) g) (into {}))
        coords (ham/graph->coords g)]
    (if coords
      (coords->component g coords reveal?)
      error-message)))
