(ns au.com.seasoft.graph.util)

(defn kw->number [kw]
  (-> kw name Long/parseLong))

