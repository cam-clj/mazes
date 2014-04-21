(ns cam-clj.mazes.random-prim
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [monet.canvas :as canvas]
            [cljs.core.async :refer [<! timeout]]))

(enable-console-print!)

(def margin 8)
(def cell-size 12)
(def wall-thickness 4)

(defn top-left
  [[row col]]
  [(+ margin (* col cell-size))
   (+ margin (* row cell-size))])

(def direction->delta {:north [-1 0] :east [0 1] :south [1 0] :west [0 -1]})

(defn adjacent-cells
  [nrows ncols [row col]]
  (for [[delta-row delta-col] (vals direction->delta)
        :let [row' (+ row delta-row)
              col' (+ col delta-col)]
        :when (and (< -1 row' nrows)
                   (< -1 col' ncols))]
    [row' col']))

(defn cell-walls
  [nrows ncols cell]
  (map (fn [neighbour] #{cell neighbour}) (adjacent-cells nrows ncols cell)))

(defn add-cell-to-canvas
  [ctx cell]
  (let [[x y] (top-left cell)]
    (canvas/fill-rect ctx {:x (+ x wall-thickness)
                           :y (+ y wall-thickness)
                           :h (- cell-size wall-thickness)
                           :w (- cell-size wall-thickness)})))

(defn remove-east-wall-from-canvas
  [ctx cell]
  (let [[x y] (top-left cell)]
    (canvas/fill-rect ctx {:x (+ x cell-size)
                           :y (+ y wall-thickness)
                           :h (- cell-size wall-thickness)
                           :w wall-thickness})))

(defn remove-south-wall-from-canvas
  [ctx cell]
  (let [[x y] (top-left cell)]
    (canvas/fill-rect ctx {:x (+ x wall-thickness)
                           :y (+ y cell-size)
                           :h wall-thickness
                           :w (- cell-size wall-thickness)})))

(defn remove-wall-from-canvas
  [ctx [r1 c1] [r2 c2]]
  (if (= r1 r2)
    (if (< c1 c2)
      (remove-east-wall-from-canvas ctx [r1 c1])
      (remove-east-wall-from-canvas ctx [r2 c2]))
    (if (< r1 r2)
      (remove-south-wall-from-canvas ctx [r1 c1])
      (remove-south-wall-from-canvas ctx [r2 c2]))))

(defn random-prim
  [dom-canvas nrows ncols]
  (let [h (+ (* nrows cell-size) wall-thickness)
        w (+ (* ncols cell-size) wall-thickness)]
    (set! (.-width dom-canvas) (+ (* 2 margin) w))
    (set! (.-height dom-canvas) (+ (* 2 margin) h))
    (let [ctx (canvas/get-context dom-canvas "2d")]
      (canvas/fill-style ctx "#000000")
      (canvas/fill-rect ctx {:x margin :y margin :h h :w w})
      (canvas/fill-style ctx "#FFFFFF")
      (let [starting-cell [(rand-int nrows) (rand-int ncols)]]
        (add-cell-to-canvas ctx starting-cell)
        (go
         (loop [cells #{starting-cell} walls (into #{} (cell-walls nrows ncols starting-cell))]
           (<! (timeout 1))
           (when (not-empty walls)
             (let [wall          (rand-nth (vec walls))
                   [cell1 cell2] (vec wall)]
                (if (and (cells cell1) (cells cell2))
                  (recur cells (disj walls wall))
                  (let [[cell new-cell] (if (cells cell1) [cell1 cell2] [cell2 cell1])]
                      (add-cell-to-canvas ctx new-cell)
                      (remove-wall-from-canvas ctx cell new-cell)
                      (recur (conj cells new-cell)
                             (into (disj walls wall) (cell-walls nrows ncols new-cell))))))))))
      ctx)))

(def dom-canvas (.getElementById js/document "maze-canvas"))

(def ctx (random-prim dom-canvas 40 40))


