(ns cam-clj.mazes.draw
  (:require [monet.canvas :as canvas]
            [cam-clj.mazes.common :as common]))

(defn walls
  [maze row col]
  (let [neighbour? (set (common/neighbours maze row col))]
    (reduce (fn [walls direction]
              (let [[delta-row delta-col] (common/delta direction)
                    neighbour-row         (+ row delta-row)
                    neighbour-col         (+ col delta-col)]
                (if (neighbour? [neighbour-row neighbour-col])
                  walls
                  (conj walls direction))))
            #{}
            (keys common/delta))))

(defn draw-cell
  [ctx maze row col cell-size]
  (let [wall? (walls maze row col)
        x     (* col cell-size)
        y     (* row cell-size)
        x'    (+ x cell-size)
        y'    (+ y cell-size)]
    (when (and (= row 0) (wall? :north))
      (canvas/move-to ctx x y)
      (canvas/line-to ctx x' y))
    (when (and (= col 0) (wall? :west))
      (canvas/move-to ctx x y)
      (canvas/line-to ctx x y'))
    (when (wall? :south)
      (canvas/move-to ctx x y')
      (canvas/line-to ctx x' y'))
    (when (wall? :east)
      (canvas/move-to ctx x' y)
      (canvas/line-to ctx x' y'))))

(defn draw-maze
  [dom-canvas maze & {:keys [cell-size line-width] :or {cell-size 20 line-width 4}}]
  (let [h (* (common/num-rows maze) cell-size)
        w (* (common/num-cols maze) cell-size)]
    (set! (.-width dom-canvas) w)
    (set! (.-height dom-canvas) h)
    (let [ctx (canvas/get-context dom-canvas "2d")]
      (canvas/fill-style ctx "#ffffff")
      (canvas/fill-rect ctx {:x 0 :y 0 :h h :w w})
      (canvas/stroke-width ctx line-width)
      (canvas/stroke-cap ctx "square")
      (canvas/begin-path ctx)
      (doseq [row (range (common/num-rows maze)) col (range (common/num-cols maze))]
        (draw-cell ctx maze row col cell-size))
      (canvas/stroke ctx)
      ctx)))
