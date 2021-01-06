(ns au.com.seasoft.graph.util)

(def interop (atom {:last-id 0
                    :id->kw  {}
                    :kw->id  {}}))

(defn kw->number [kw]
  (try
    (-> kw name Long/parseLong)
    (catch NumberFormatException ex
      (let [kw->id-1 (-> interop deref :kw->id)
            id (get kw->id-1 kw)]
        (if id
          id
          (:last-id (swap! interop (fn [{:keys [last-id id->kw kw->id]}]
                                     (let [new-id (inc last-id)
                                           new-id->kw (assoc id->kw id kw)
                                           new-kw->id (assoc kw->id kw id)]
                                       {:last-id new-id
                                        :id->kw  new-id->kw
                                        :kw->id  new-kw->id})))))))))

(defn kw->string [kw]
  (when kw (assert (keyword? kw)))
  (and kw (subs (str kw) 1)))

(def kw->interop-id kw->number)

