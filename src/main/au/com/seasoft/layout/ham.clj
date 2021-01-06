(ns au.com.seasoft.layout.ham
  (:require
    [au.com.seasoft.graph.graph :as gr]
    [au.com.seasoft.graph.util :as util]
    [com.fulcrologic.guardrails.core :refer [>defn => | ?]]
    [clojure.core.async :as async :refer [>! chan go go-loop alts!! timeout]]
    )
  (:import
    [au.com.seasoft.ham GenericGraph InteropNode InteropEdge InteropHAM]
    [com.syncleus.dann.math Vector]
    (java.util Map)))

(defn debug [& s]
  (apply println "view:graph -" s))

(def options {::radius             10
              ::margin             10
              ::magnify            20
              ::alignment-attempts 150
              ::silent?            true
              ::number-of-tries    30
              ::max-user-wait-time 1000
              })

(defn node->interop-node [node]
  (InteropNode. (util/kw->interop-id node)))

(defn edge->interop-edge [[source-node target-node]]
  (InteropEdge.
    (node->interop-node source-node) (node->interop-node target-node)))

(defn graph->interop-graph [graph]
  (let [result (GenericGraph/create)
        nodes (->> graph
                   gr/nodes
                   (map node->interop-node))
        edges (->> graph
                   gr/pair-edges
                   (map edge->interop-edge))]
    (doseq [node nodes]
      (.addNode result node))
    (doseq [edge edges]
      (.addEdge result edge))
    result))

(defn interop-coords->coords-hof [g]
  (fn [^Map interop-coords]
    (reduce
      (fn [m map-entry]
        (let [^InteropNode interop-node (.getKey map-entry)
              node-id (-> (.getId interop-node) str keyword)
              targets-map (get g node-id)
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
        {:keys [::radius ::margin ::magnify]} options
        coords (->> coords
                    (map (fn [[k v]]
                           (let [{:keys [x y]} v
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

(defn- -graph->coords
  [g]
  (let [{:keys [::alignment-attempts ::silent?]} options
        interop-graph (graph->interop-graph g)
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

(>defn graph->coords [g]
  [::gr/graph => any?]
  (let [{:keys [::number-of-tries ::max-user-wait-time]} options
        _ (assert (int? number-of-tries) ["number-of-tries not int?" number-of-tries])
        _ (assert (int? max-user-wait-time) ["max-user-wait-time not int?" max-user-wait-time])
        timeout-chan (timeout max-user-wait-time)
        cs (repeatedly number-of-tries chan)]
    (go-loop [cs cs
              ordinal 1]
      (when (seq cs)
        (if-let [[counted coords :as res] (-graph->coords g)]
          (do
            ;; Does an extra unnecessary one before alts picks up the already decided winner. Not a problem that needs
            ;; to be solved
            (debug "Aligned" (ordinal-text ordinal) "after" counted "advances")
            (>! (first cs) (conj res ordinal))
            (recur (next cs) (inc ordinal)))
          (do
            ;; Here we couldn't reach alignment after exhausting all the attempts/advances allowed,
            ;; so we start afresh on the next ordinal
            (recur (next cs) (inc ordinal))))))
    (let [interop-coords->coords (interop-coords->coords-hof g)
          [[counted coords ordinal] c] (alts!! (conj cs timeout-chan))]
      (if (not= c timeout-chan)
        (do
          (debug "Winner is" (ordinal-text ordinal) "aligned after" counted "advances")
          (-> coords
              interop-coords->coords
              scale-coords))
        (debug "Timed out at" max-user-wait-time "before any of the" number-of-tries "channels could align")))))
