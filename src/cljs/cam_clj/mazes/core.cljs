(ns cam-clj.mazes.core
  (:require [cam-clj.mazes.common :as common]
            [cam-clj.mazes.generate.drunken-walk :refer [drunken-walk]]
            [cam-clj.mazes.draw :refer [draw-maze]]))

(enable-console-print!)

(def dom-canvas (.getElementById js/document "maze-canvas"))

(defrecord NeighbourMapMaze [cells]
  cam-clj.mazes.common/AMaze
  (num-rows [_] (count cells))
  (num-cols [_] (count (first cells)))
  (neighbours [_ row col] (get-in cells [row col])))

;; +-+---+
;; | |   |
;; | | | |
;; |   | |
;; +---+-+

(def maze1 (->NeighbourMapMaze [[[[1,0]]       [[0,2] [1,1]] [[0,1] [1,2]]]
                                [[[0,0] [2,0]] [[0,1] [2,1]] [[0,2] [2,2]]]
                                [[[1,0] [2,1]] [[2,0] [1,1]] [[1,2]]]]))


(common/num-rows maze1)

;; (draw-maze dom-canvas maze1)

(defrecord WallMapMaze [cells]
  cam-clj.mazes.common/AMaze
  (num-rows [_] (count cells))
  (num-cols [_] (count (first cells)))
  (neighbours
   [_ row col]
   (let [wall? (set (get-in cells [row col]))]
     (mapv (fn [direction]
             (let [[delta-row delta-col] (common/delta direction)]
               [(+ row delta-row) (+ col delta-col)]))
       (filter (complement wall?) (keys common/delta))))))

(def maze2 (->WallMapMaze [[[:north :east :west] [:north :west] [:north :east]]
                           [[:east :west]        [:east :west]  [:east :west]]
                           [[:south :west]       [:south :east] [:south :east :west]]]))

(common/neighbours maze2 0 1)

;;(draw-maze dom-canvas maze2)

;;(def maze3 (->WallMapMaze (drunken-walk 20 20)))

;;(draw-maze dom-canvas maze3)
