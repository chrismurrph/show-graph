(ns au.com.seasoft.graph.layout.graph-key-swap
  "The HAM only works with int? keys yet we accept any keys"
  (:require
    [com.fulcrologic.guardrails.core :refer [>defn => | ?]]
    [clojure.spec.alpha :as s]
    [au.com.seasoft.general.dev :as dev]))

(>defn convert-targets
  [m k->int]
  [(s/or :map map? :vector vector?) map? => map?]
  (->> m
       (map (fn [[k v]]
              [(dev/safe-get k->int k) v]))
       (into {})))

(defn ->strict-graph [g k->int]
  (->> g
       (map (fn [[k v]]
              [(dev/safe-get k->int k) (convert-targets v k->int)]))
       (into {})))

(defn ->graph [g]
  (let [k->int (->> (dev/safe-keys g 3)
                    (map-indexed
                      (fn [idx k] [k idx]))
                    (into {}))
        strict-graph (->strict-graph g k->int)
        int->k (->> k->int
                    (map (fn [[k v]] [v k]))
                    (into {}))]
    [strict-graph int->k]))
