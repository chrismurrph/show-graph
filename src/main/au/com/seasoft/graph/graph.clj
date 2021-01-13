(ns au.com.seasoft.graph.graph
  "Clojure specs used by graph orientated functions, as well as graph orientated functions that are not metrics"
  (:require
    [au.com.seasoft.general.dev :as dev]
    [com.fulcrologic.guardrails.core :refer [>defn => | ?]]
    [clojure.spec.alpha :as s]
    [au.com.seasoft.graph.util :as util]))

;;
;; A node on a graph
;;
(s/def ::vertex any?)

;;
;; An edge is on a graph, whereas a pair is just [::vertex ::vertex]. The first a source and the second
;; a target, even if just potentially
;;
(s/def ::pair (s/tuple any? any?))

;;
;; We say that each vertex of a graph has many targets even thou we don't directly use the target spec here
;; (i.e. a tuple (::target) is equivalent to a map-entry (what have here under s/map-of))
;;
(s/def ::graph (s/map-of ::vertex (s/map-of ::vertex any?)))

(>defn nodes
  [g]
  [::graph => (s/coll-of ::vertex :kind set)]
  (-> g keys set))

(>defn pair-edges
  "All the edges on a graph, without weight"
  [g]
  [::graph => (s/coll-of ::pair :kind set)]
  (reduce
    (fn [acc [source-node v]]
      (if (seq v)
        (->> (dev/safe-keys (util/ensure-is-map v) 1)
             (map (fn [target-node]
                    [source-node target-node]))
             (into acc))
        acc))
    #{}
    g))

(defn nodes-in-edges [g]
  (set (mapcat (fn [m]
                 (if (seq m)
                   (dev/safe-keys m 2)
                   []))
               (vals g))))

;; TODO
;; Have the subset code below tested for both sides of or
(defn graph?
  "Is it a reasonable graph, suitable for display? (by Reveal usually)"
  [x]
  (or (s/valid? (s/coll-of ::pair) x)
      (let [nodes (-> x keys set)
            res (and (map? x)
                     (-> x vals first map?)
                     (s/valid? ::graph x)
                     (clojure.set/subset? (nodes-in-edges x) nodes))]
        res)))