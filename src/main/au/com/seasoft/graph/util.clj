(ns au.com.seasoft.graph.util)

(def max-visible-len 3)

(defn kw->string [kw]
  (->> (subs (str kw) 1)
       (take max-visible-len)
       (apply str)))

(defn ->string [x]
  (cond
    (nil? x) nil
    (keyword? x) (kw->string x)
    :else (->> (str x)
               (take max-visible-len)
               (apply str))))

