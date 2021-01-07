(ns au.com.seasoft.graph.util)

(defn kw->string [kw]
  (subs (str kw) 1))

(defn ->string [x]
  (cond
    (nil? x) nil
    (keyword? x) (kw->string x)
    :else (str x)))

