(ns au.com.seasoft.fx.grid-view
  (:require [vlaaad.reveal.ext :as rx]))

(defn grid [children]
  {:fx/type            :grid-pane
   :hgap               1
   :vgap               1
   :column-constraints (repeat 8 {:fx/type    :column-constraints
                                  :halignment :center
                                  :min-width  30})
   :row-constraints    (repeat 8 {:fx/type    :row-constraints
                                  :valignment :center
                                  :min-height 30})
   :children           children})

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
    {:fx/type :stack-pane
     :children
              [(grid
                 (for [x (range 8)
                       y (range 8)]
                   {:fx/type          :region
                    :grid-pane/column x
                    :grid-pane/row    y
                    :style            {:-fx-background-color (if (even? (+ x y)) "#888" "#999")}}))
               (grid
                 (for [[[x y] player :as coordinate+player] board]
                   {:fx/type          rx/popup-view
                    :grid-pane/column x
                    :grid-pane/row    y
                    :value            coordinate+player
                    :desc             {:fx/type :label
                                       :style   {:-fx-font-size 20
                                                 :-fx-text-fill (:color player)}
                                       :text    (piece->symbol (:piece player))}
                    }))]}))
