(ns layout.ham
  (:require
    [graph.example-data :as example]
    [graph.graph :as gr]
    [graph.util :as util]
    [au.com.seasoft.general.dev :as dev]
    [com.fulcrologic.guardrails.core :refer [>defn => | ?]]
    [clojure.core.async :as async :refer [>! chan go go-loop alts!! timeout]]
    )
  (:import
    [au.com.seasoft.ham GenericGraph InteropNode InteropEdge InteropHAM]
    [com.syncleus.dann.math Vector]
    (java.util Map)))

(def options (tap> {::radius             10
                    ::margin             10
                    ::magnify            20
                    ::alignment-attempts 150
                    ::silent?            true
                    ::number-of-tries    30
                    ::max-user-wait-time 1000
                    }))

(defn node->interop-node [node]
  (InteropNode. (util/kw->number node)))

(defn x-2 []
  (node->interop-node :1))

(defn edge->interop-edge [[source-node target-node]]
  (InteropEdge.
    (node->interop-node source-node) (node->interop-node target-node)))

(defn x-3 []
  (edge->interop-edge [:1 :3]))

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

(defn interop-coords->coords [^Map interop-coords]
  (reduce
    (fn [m map-entry]
      (let [^InteropNode interop-node (.getKey map-entry)
            node-id (-> (.getId interop-node) str keyword)
            ^Vector v (.getValue map-entry)
            _ (assert (= 2 (.getDimensions v)))
            x (.getCoordinate v 1)
            y (.getCoordinate v 2)]
        (assoc m node-id [x y])))
    {}
    (.entrySet interop-coords)))

(defn coords->smallest-x-and-y
  "Finds the distance of the nodes closest to the top and closest to the left edge. Not returning a coordinate here!
  Useful for putting the graph view into the top left corner"
  [coords]
  (reduce
    (fn [[min-x min-y] [k [x y]]]
      [(min min-x x) (min min-y y)])
    [Double/MAX_VALUE Double/MAX_VALUE]
    coords))

(defn scale-coords
  "origin-shift has to be 10 to always get the whole graph into the +ive quadrant. There might be an x or a y of -10.
  Just going off what HyperassociativeMap returns.
  To get it bang in the top left corner we will need to find the top-most and left-most nodes.
  Apply a margin after that."
  [coords]
  (let [origin-shift (::radius options)
        {:keys [::radius ::margin ::magnify]} options
        coords (->> coords
                    (map (fn [[k v]]
                           (let [[x y] v
                                 new-x (+ radius (* (+ x origin-shift) magnify))
                                 new-y (+ radius (* (+ y origin-shift) magnify))]
                             [k [new-x new-y]]))))
        [left-shift raise-shift] (coords->smallest-x-and-y coords)]
    (->> coords
         (map (fn [[k v]]
                (let [[x y] v
                      new-x (- x left-shift (- margin))
                      new-y (- y raise-shift (- margin))]
                  [k [new-x new-y]])))
         (into {}))))

(defn- -graph->coords
  [g]
  (let [{:keys [::alignment-attempts ::silent?]} options
        interop-graph (graph->interop-graph g)
        interop-ham-1 (InteropHAM/create interop-graph 2)
        interop-ham-2 (InteropHAM/attemptToAlign interop-ham-1 alignment-attempts silent?)
        aligned? (.isAligned interop-ham-2)]
    (when aligned?
      [(.getCounter interop-ham-2) (.getCoordinates interop-ham-2)])))

(>defn graph->coords [g]
  [::gr/graph => any?]
  (let [{:keys [::number-of-tries ::max-user-wait-time]} options
        timeout-chan (timeout max-user-wait-time)
        cs (repeatedly number-of-tries chan)]
    (go-loop [cs cs
              ordinal 1]
      (when (seq cs)
        (if-let [[counted coords :as res] (-graph->coords g)]
          (do
            ;; Does an extra unnecessary one before alts picks up the already decided winner. Not a problem that needs
            ;; to be solved
            (dev/log-on "Aligned ordinal" ordinal "after" counted "advances")
            (>! (first cs) (conj res ordinal))
            (recur (next cs) (inc ordinal)))
          (do
            ;; Here we couldn't reach alignment after exhausting all the attempts/advances allowed,
            ;; so we start afresh on the next ordinal
            (recur (next cs) (inc ordinal))))))
    (let [[[counted coords ordinal] c] (alts!! (conj cs timeout-chan))]
      (if (not= c timeout-chan)
        (do
          (dev/log-on "Winner is ordinal" ordinal "aligned after" counted "advances")
          (-> coords
              interop-coords->coords
              scale-coords))
        (dev/log-on "Timed out at" max-user-wait-time "before any of the" number-of-tries "channels could align")))))

(defn x-1 []
  (let [g example/unreachable-nodes-graph
        coords (graph->coords g)]
    (when coords
      (dev/pp coords))))
