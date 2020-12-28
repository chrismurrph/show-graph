(ns user
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.stacktrace :as st]
    [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs]]
    [clojure.spec.alpha :as s]
    [reveal.e03-chess-server-popups]))

(comment
  "reveal has some of this"
  (set! *warn-on-reflection* true))
(comment
  "reveal has a lot of this"
  (set! *unchecked-math* :warn-on-boxed))
(s/check-asserts true)

;; The refer is not seen
(defn print-stack-trace [one two]
  (st/print-cause-trace one two))

(set-refresh-dirs "src/dev" "src/main" "src/test")

(defn refresh [& args]
  (tools-ns/refresh))

(defn refresh-all [& args]
  (tools-ns/refresh-all))

;; You should have a key binding that sends a snippet to the REPL
(comment
  ;; first snippet, will bring up the Reveal window
  (do
    (require 'vlaaad.reveal)
    (add-tap (vlaaad.reveal/ui)))
  ;; second snippet
  (tap> {:fx/type :web-view
         :url     "http://www.seasoft.com.au"})
  (tap> {:a 1})
  (tap> {:apples  10
         :oranges 20})
  (tap> (all-ns))
  (tap> (ns-interns 'clojure.core))
  (tap> (the-ns 'cljfx.api)))