(ns au.com.seasoft.graph.layout.ham
  (:require
    [au.com.seasoft.graph.layout.graph-key-swap :as graph-key-swap]
    [au.com.seasoft.graph.graph :as gr]
    [com.fulcrologic.guardrails.core :refer [>defn => | ?]]
    [clojure.core.async :as async :refer [>! chan go go-loop alts!! timeout]]
    [au.com.seasoft.general.dev :as dev]
    [au.com.seasoft.graph.util :as util])
  (:import
    [au.com.seasoft.ham GenericGraph InteropNode InteropEdge InteropHAM]
    [com.syncleus.dann.math Vector]
    (java.util Map)))

(defn debug [& s]
  (apply println "view:graph -" s))

(def options {::radius             10
              ::margin             10
              ::alignment-attempts 200
              ::silent?            true
              ::number-of-tries    40
              ::max-user-wait-time 2000
              })

(defn node->interop-node [node]
  (assert (int? node) ["HAM only works when vertices are ints"])
  (InteropNode. node))

(defn edge->interop-edge [[source-node target-node]]
  (InteropEdge.
    (node->interop-node source-node) (node->interop-node target-node)))

(defn graph->interop-graph [strict-graph]
  (let [result (GenericGraph/create)
        nodes (->> strict-graph
                   gr/nodes
                   (map node->interop-node))
        edges (->> strict-graph
                   gr/pair-edges
                   (map edge->interop-edge))]
    (doseq [node nodes]
      (.addNode result node))
    (doseq [edge edges]
      (.addEdge result edge))
    result))

(defn interop-coords->coords-hof [orig-graph-as-m int->k]
  (fn [^Map interop-coords]
    (reduce
      (fn [m map-entry]
        (assert (map? m) ["Not a map" m])
        (let [^InteropNode interop-node (.getKey map-entry)
              int-id (.getId interop-node)
              node-id (dev/safe-get int->k int-id)
              node-props (dev/safe-get orig-graph-as-m node-id)
              targets-map (cond
                            (map? node-props) node-props
                            (seq node-props) (into {} node-props)
                            (= [] node-props) {}
                            :else (throw (ex-info "Got impossible! result from original graph"
                                                  {:node-id         node-id
                                                   :result          node-props
                                                   :orig-graph-as-m orig-graph-as-m})))
              ^Vector v (.getValue map-entry)
              _ (assert (= 2 (.getDimensions v)))
              x (.getCoordinate v 1)
              y (.getCoordinate v 2)]
          (assoc m node-id (merge targets-map {:id node-id :x x :y y}))))
      {}
      (.entrySet interop-coords))))

(defn coords->smallest-x-and-y
  "Finds the distance of the nodes closest to the top and closest to the left edge. Not returning a coordinate here!
  Useful for putting the graph view into the top left corner"
  [coords]
  (reduce
    (fn [[min-x min-y] [k {:keys [x y]}]]
      [(min min-x x) (min min-y y)])
    [Double/MAX_VALUE Double/MAX_VALUE]
    coords))

(defn scale-coords
  "origin-shift has to be 10 to always get the whole graph into the +ive quadrant. There might be an x or a y of -10.
  Just going off what HyperassociativeMap returns.
  To get it bang in the top left corner we will need to find the top-most and left-most nodes.
  Apply a margin after that."
  [coords]
  (let [origin-shift 10
        {:keys [::radius ::margin]} options
        magnify (max 20 (count coords))
        coords (->> coords
                    (map (fn [[k v]]
                           (let [{:keys [x y]} v
                                 _ (assert x ["No x and y in v" v])
                                 new-x (+ radius (* (+ x origin-shift) magnify))
                                 new-y (+ radius (* (+ y origin-shift) magnify))]
                             [k (assoc v :x new-x :y new-y)]))))
        [left-shift raise-shift] (coords->smallest-x-and-y coords)]
    (->> coords
         (map (fn [[k v]]
                (let [{:keys [x y]} v
                      new-x (- x left-shift (- margin))
                      new-y (- y raise-shift (- margin))]
                  [k (assoc v :x new-x :y new-y)])))
         (into {}))))

(defn interop-graph->coords
  "Will take some time and may not succeed. When fails returns nil. When succeeds returns how many aligns/advances
  it took and the coordinates (where to place the nodes)"
  [interop-graph]
  (let [{:keys [::alignment-attempts ::silent?]} options
        interop-ham (InteropHAM/create interop-graph 2)
        perhaps-aligned-ham (InteropHAM/attemptToAlign interop-ham alignment-attempts silent?)]
    (when (.isAligned perhaps-aligned-ham)
      [(.getCounter perhaps-aligned-ham) (.getCoordinates perhaps-aligned-ham)])))

(defn ordinal-text [n]
  (case n
    1 "first"
    2 "second"
    3 "third"
    4 "forth"
    5 "fifth"
    6 "sixth"
    7 "seventh"
    (str "ordinal " n)))

(>defn graph->coords [g1]
  [::gr/graph => any?]
  (let [{:keys [::number-of-tries ::max-user-wait-time]} options
        _ (assert (int? number-of-tries) ["number-of-tries not int?" number-of-tries])
        _ (assert (int? max-user-wait-time) ["max-user-wait-time not int?" max-user-wait-time])
        timeout-chan (timeout max-user-wait-time)
        cs (repeatedly number-of-tries chan)
        g2 (util/ensure-is-map g1)
        [strict-graph int->k] (graph-key-swap/->graph g2)
        interop-graph (graph->interop-graph strict-graph)]
    (go-loop [cs cs
              ordinal 1]
      (when (seq cs)
        (if-let [[counted coords :as res] (interop-graph->coords interop-graph)]
          (do
            (debug "Aligned" (ordinal-text ordinal) "after" counted "advances")
            (>! (first cs) (conj res ordinal)))
          (do
            ;; Here we couldn't reach alignment after exhausting all the attempts/advances allowed,
            ;; so we start afresh on the next ordinal
            (recur (next cs) (inc ordinal))))))
    (let [interop-coords->coords (interop-coords->coords-hof g2 int->k)
          [[counted interop-coords ordinal] c] (alts!! (conj cs timeout-chan))]
      (if (not= c timeout-chan)
        (do
          (debug "Winner is the" (ordinal-text ordinal) "aligned after" counted "advances")
          (->> interop-coords
               interop-coords->coords
               scale-coords))
        (debug "Timed out at" max-user-wait-time "before any of the" number-of-tries "channels could align")))))
