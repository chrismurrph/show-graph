(ns au.com.seasoft.graph.util
  (:require [clojure.pprint :as pprint]))

(defn kw->number [kw]
  (-> kw name Long/parseLong))

