(ns graph.fx.view-util
  (:require
    [cljfx.api :as fx]
    [com.rpl.specter :as sp]
    [clojure.pprint :as pprint]
    [au.com.seasoft.general.dev :as dev]))

(defn pp
  ([n x]
   (binding [pprint/*print-right-margin* n]
     (-> x pprint/pprint)))
  ([x]
   (pp 180 x)))

(defn has-nils? [x]
  (seq (sp/select (sp/walker nil?) x)))

(defn show-and-throw [something ^String msg]
  (dev/log-on "ERROR" msg)
  (pp something)
  (throw (Error. msg)))

(defn see-component-simply
  "Don't use if `component` is a Reveal component, as you will get :cljfx.defaults/unhandled-map-event
  due to the use of rx/popup-view, followed by an NPE. Just remove the rx/popup-view as a workaround.
  Removal of rx/popup-view is now being done automatically so that `component` already has it removed. See use of
  this function, where the result of layout-view/graph->component is passed in"
  [component]
  (assert (map? component) "Nothing to see")
  (if (has-nils? component)
    (show-and-throw component "nil values not allowed")
    (fx/on-fx-thread
      (do
        (dev/pp-hide component)
        (fx/create-component
          {:fx/type    :stage
           :min-height 400
           :min-width  600
           :showing    true
           :scene      {:fx/type :scene
                        :root    component}})))))

(defmulti event-handler :event/type)

(defmethod event-handler :default [e]
  (prn e))

(defn see-something-map-desc
  "Deprecated b/c see-something-simply works fine"
  [something]
  (assert (map? something) "Nothing to see")
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
                                                      :root    something}}))
        :opts {:fx.opt/map-event-handler event-handler}))))