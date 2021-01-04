(ns au.com.seasoft.graph.util)

(defn kw->number [kw]
  (-> kw name Long/parseLong))

(defn kw->string [kw]
  (when kw (assert (keyword? kw)))
  (and kw (subs (str kw) 1)))

