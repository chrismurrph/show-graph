(ns au.com.seasoft.graph.util)

(def max-visible-len 30)

(defn kw->string [kw]
  (->> (subs (str kw) 1)
       (take max-visible-len)
       (apply str)))

(defn ->string
  "A node can be any type, as long as it can be turned into a string with this function!"
  [x]
  (cond
    (nil? x) nil
    (keyword? x) (kw->string x)
    :else (->> (str x)
               (take max-visible-len)
               (apply str))))

