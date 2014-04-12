(ns cam-clj.mazes.draw
  (:require [monet.canvas :as canvas]))

(defprotocol AMaze
  (num-rows [this] "Return the number of rows in the maze.")
  (num-cols [this] "Return the number of columns in the maze.")
  (neighbours [this row col] "Return a list of neighbours of the cell at [row,col]."))

(defrecord Maze [nrows ncols cells]
  AMaze
  (num-rows [_] nrows)
  (num-cols [_] ncols)
  (neighbours [_ row col] (get-in cells [row col])))

(defn cells->maze
  [cells]
  (->Maze (count cells) (count (first cells)) cells))

(def delta {:north [-1 0] :east [0 1] :south [1 0] :west [0 -1]})

(defn walls
  [maze row col]
  (let [neighbour? (set (neighbours maze row col))]
    (reduce (fn [walls direction]
              (let [[delta-row delta-col] (delta direction)
                    neighbour-row         (+ row delta-row)
                    neighbour-col         (+ col delta-col)]
                (if (neighbour? [neighbour-row neighbour-col])
                  walls
                  (conj walls direction))))
            #{}
            (keys delta))))

(defn draw-cell
  [ctx maze row col cell-size]
  (let [wall? (walls maze row col)
        x     (* col cell-size)
        y     (* row cell-size)
        x'    (+ x cell-size)
        y'    (+ y cell-size)]
    (println "Drawing" [row col] cell-size)
    (when (and (= row 0) (wall? :north))
      (println "Drawing north wall" [x y] "->" [x' y])
      (canvas/move-to ctx x y)
      (canvas/line-to ctx x' y)
      (canvas/stroke ctx))
    (when (and (= col 0) (wall? :west))
      (println "Drawing west wall" [x y] "->" [x y'])
      (canvas/move-to ctx x y)
      (canvas/line-to ctx x y')
      (canvas/stroke ctx))
    (when (wall? :south)
      (println "Drawing south wall" [x y'] "->" [x' y'])
      (canvas/move-to ctx x y')
      (canvas/line-to ctx x' y')
      (canvas/stroke ctx))
    (when (wall? :east)
      (println "Drawing east wall" [x' y] "->" [x' y'])
      (canvas/move-to ctx x' y)
      (canvas/line-to ctx x' y')
      (canvas/stroke ctx))))

(defn draw-maze
  [dom-canvas maze & {:keys [cell-size] :or {cell-size 20}}]
  (let [h (* (num-rows maze) cell-size)
        w (* (num-cols maze) cell-size)]
    (set! (.-width dom-canvas) w)
    (set! (.-height dom-canvas) h)
    (let [ctx (canvas/get-context dom-canvas "2d")]
      ;;(canvas/fill-style ctx "#FFFFFF")
      ;;(canvas/fill-rect ctx {:x 0 :y 0 :h h :w w})
      ;;(canvas/stroke-style ctx "#FF0000")
      (canvas/stroke-width ctx 4)
      (doseq [row (range (num-rows maze)) col (range (num-cols maze))]
        (draw-cell ctx maze row col cell-size))
      ctx)))
