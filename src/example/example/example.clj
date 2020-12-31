(ns example.example
  (:require
    [au.com.seasoft.graph.example-data :as example-data]))

;;
;; Load these two snippets one after the other in order then press <enter> and select :view/graph to set
;; everything up then see it working.
;; To load snippets you should have a key binding that does 'Send Top Form to REPL'.
;; Note that in the `user` ns we required `[au.com.seasoft.reveal.view]`, enabling :view/graph to be available
;;
(comment
  ;; Bring up the Reveal window
  (do
    (require 'vlaaad.reveal)
    (add-tap (vlaaad.reveal/ui)))
  ;; Put a graph into Reveal
  (tap> example-data/simple-graph)
  ;; Then press <enter> and select :view/graph
  ;; You should now see a nicely laid out visualisation of example-data/simple-graph
  )


