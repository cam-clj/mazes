(ns cam-clj.mazes.common)

(defprotocol AMaze
  (num-rows [this] "Return the number of rows in the maze.")
  (num-cols [this] "Return the number of columns in the maze.")
  (neighbours [this row col] "Return a list of neighbours of the cell at [row,col]."))

(def delta {:north [-1 0] :east [0 1] :south [1 0] :west [0 -1]})

(defn opposite
  [direction]
  (case direction
    :north :south
    :east  :west
    :south :north
    :west  :east))
