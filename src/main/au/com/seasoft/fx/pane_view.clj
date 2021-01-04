(ns au.com.seasoft.fx.pane-view
  (:require [vlaaad.reveal.ext :as rx]))

;;
;; All in one place but you can definitely tab to them, all wanting to show here
;; Conclusion: panes within a pane are tab-able (I thought only grid)
;;

(defn pane [children]
  {:fx/type  :pane
   :children children})

(def piece->symbol
  {:queen  "♛"
   :king   "♚"
   :rook   "♜"
   :bishop "♝"
   :knight "♞"
   :pawn   "♟"})

(def chess-view
  (let [board {[0 4] {:username "alice"
                      :color    :white
                      :piece    :queen}
               [2 2] {:username "bob"
                      :color    :white
                      :piece    :knight}
               [4 6] {:username "cecilia"
                      :color    :black
                      :piece    :rook}
               [3 0] {:username "david"
                      :color    :black
                      :piece    :knight}}]
    {:fx/type :pane
     :children
              [
               (pane (for [[[x y] player :as coordinate+player] board]
                       {:fx/type  rx/popup-view
                        :layout-x x
                        :layout-y y
                        :value    coordinate+player
                        :desc     {:fx/type :label
                                   :style   {:-fx-font-size 20
                                             :-fx-text-fill (:color player)}
                                   :text    (piece->symbol (:piece player))}
                        }))
               (pane
                 (for [x (range 8)
                       y (range 8)]
                   {:fx/type  :region
                    :layout-x x
                    :layout-y y
                    :style    {:-fx-background-color (if (even? (+ x y)) "#888" "#999")}}))]}))
