(ns cam-clj.mazes.cellular-automata
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [monet.canvas :as canvas]
            [cljs.core.async :refer [<! timeout]]))

(enable-console-print!)

(def nrows 30)
(def ncols 30)
(def cell-size 8)
(def margin 4)

(defn init-grid
  []
  (into #{} (for [r (range nrows) c (range ncols) :when (< (rand) 0.5)] [r c])))

(defn add-delta
  [[x y] [delta-x delta-y]]
  [(mod (+ x delta-x) ncols)
   (mod (+ y delta-y) nrows)])

(defn neighbours
  [cell]
  (map (partial add-delta cell) [[-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [-1 1] [1 0] [1 1]]))

(defn born?
  [num-neighbours]
  (= num-neighbours 3))

(defn survives?
  [num-neighbours]
  (<= 1 num-neighbours 4))

(defn update-grid
  [living?]
  (reduce (fn [new-grid cell]
            (let [num-neighbours (count (filter living? (neighbours cell)))]
              (if (or (and (living? cell) (survives? num-neighbours))
                      (and (not (living? cell)) (born? num-neighbours)))
                (conj new-grid cell)
                new-grid)))
          #{}
          (for [r (range nrows) c (range ncols)] [r c])))

(defn render-grid
  [ctx cells]
  (canvas/fill-style ctx "#000000")
  (doseq [[row col] cells]
    (canvas/fill-rect ctx {:x (+ margin (* col cell-size))
                           :y (+ margin (* row cell-size))
                           :h cell-size
                           :w cell-size})))

(def dom-canvas (.getElementById js/document "maze-canvas"))
(set! (.-height dom-canvas) (+ (* 2 margin) (* nrows cell-size)))
(set! (.-width dom-canvas) (+ (* 2 margin) (* ncols cell-size)))

(def mc (canvas/init dom-canvas "2d"))

(canvas/add-entity mc :border (canvas/entity {:x margin :y margin :h (* nrows cell-size) :w (* ncols cell-size)}
                                             nil
                                             (fn [ctx border]
                                               (-> ctx
                                                   (canvas/stroke-style "#000000")
                                                   (canvas/stroke-rect border)))))

(canvas/add-entity mc :grid (canvas/entity (init-grid) update-grid render-grid))
