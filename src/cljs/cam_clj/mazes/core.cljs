(ns cam-clj.mazes.core
  (:require [cam-clj.mazes.draw :refer [cells->maze draw-maze]]))

(enable-console-print!)

(def maze (cells->maze [[[[1,0]]       [[0,2] [1,1]] [[0,1] [1,2]]]
                        [[[0,0] [2,0]] [[0,1] [2,1]] [[0,2] [2,2]]]
                        [[[1,0] [2,1]] [[2,0] [1,1]] [[1,2]]]]))

(def dom-canvas (.getElementById js/document "maze-canvas"))

(draw-maze dom-canvas maze)
