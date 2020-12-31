(ns au.com.seasoft.graph.graph
  "Clojure specs used by graph orientated functions, as well as graph orientated functions that are not metrics"
  (:require
    [au.com.seasoft.graph.example-data :as example]
    [com.fulcrologic.guardrails.core :refer [>defn => | ?]]
    [clojure.spec.alpha :as s]
    ))

;;
;; A node on a graph
;;
(s/def ::vertex keyword?)

;;
;; The cost/penalty/distance for going from one vertex to another
;;
(s/def ::weight int?)

;;
;; An edge is on a graph, whereas a pair is just [::vertex ::vertex]. The first a source and the second
;; a target, even if just potentially
;;
(s/def ::pair (s/tuple (s/nilable ::vertex) (s/nilable ::vertex)))

;;
;; We say that each vertex of a graph has many targets even thou we don't directly use the target spec here
;; (i.e. a tuple (::target) is equivalent to a map-entry (what have here under s/map-of))
;;
(s/def ::graph (s/map-of ::vertex (s/map-of ::vertex ::weight)))

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
      (into acc (map (fn [target-node]
                       [source-node target-node])
                     (keys v))))
    #{}
    g))

(defn kw->number [kw]
  (try
    (some-> kw name Long/parseLong)
    (catch Throwable th nil)))

(defn nodes-in-edges [g]
  (set (mapcat (fn [m]
                 (keys m)) (vals g))))

(defn graph? [x]
  (let [nodes (-> x keys set)]
    (and (map? x)
         (-> x vals first map?)
         (every? kw->number nodes)
         (clojure.set/subset? (nodes-in-edges x) nodes))))
