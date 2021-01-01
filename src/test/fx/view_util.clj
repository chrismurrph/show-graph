(ns fx.view-util
  (:require
    [cljfx.api :as fx]
    [com.rpl.specter :as sp]
    [clojure.pprint :as pprint]))

(defn pp
  ([n x]
   (binding [pprint/*print-right-margin* n]
     (-> x pprint/pprint)))
  ([x]
   (pp 180 x)))

(defn has-nils? [x]
  (seq (sp/select (sp/walker nil?) x)))

(defn show-and-throw [something ^String msg]
  (pp something)
  (throw (Error. msg)))

(defn see-something-1 [something]
  (assert something "Nothing to see")
  (if (has-nils? something)
    (show-and-throw something "nil values not allowed")
    (fx/on-fx-thread
      (fx/create-component
        {:fx/type    :stage
         :min-height 400
         :min-width  600
         :showing    true
         :scene      {:fx/type :scene
                      :root    something}}))))

(defn see-something-2 [something]
  (assert something "Nothing to see")
  (if (has-nils? something)
    (show-and-throw something "nil values not allowed")
    (fx/mount-renderer
      (atom {})
      (fx/create-renderer
        :middleware (fx/wrap-map-desc (fn [_]
                                        {:fx/type    :stage
                                         :min-height 400
                                         :min-width  600
                                         :showing    true
                                         :scene      {:fx/type :scene
                                                      :root    something}}))))))

