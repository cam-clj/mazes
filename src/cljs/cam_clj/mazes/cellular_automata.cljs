(ns cam-clj.mazes.cellular-automata
  (:require [monet.canvas :as canvas]))

(enable-console-print!)

(defn init-grid
  [nrows ncols]
  (into #{} (for [r (range nrows) c (range ncols) :when (< (rand) 0.3)] [r c])))

(defn add-delta
  [nrows ncols [x y] [delta-x delta-y]]
  [(mod (+ x delta-x) ncols)
   (mod (+ y delta-y) nrows)])

(defn neighbours
  [cell nrows ncols]
  (map (partial add-delta nrows ncols cell) [[-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [-1 1] [1 0] [1 1]]))

(defn born?
  [num-neighbours]
  (= num-neighbours 3))

(defn survives?
  [num-neighbours]
  (<= 1 num-neighbours 4))

(defn update-grid
  [nrows ncols]
  (fn
    [living?]
    (reduce (fn [new-grid cell]
              (let [num-neighbours (count (filter living? (neighbours cell nrows ncols)))]
                (if (or (and (living? cell) (survives? num-neighbours))
                        (and (not (living? cell)) (born? num-neighbours)))
                  (conj new-grid cell)
                  new-grid)))
            #{}
            (for [r (range nrows) c (range ncols)] [r c]))))

(defn render-grid
  [margin cell-size]
  (fn [ctx cells]
    (canvas/fill-style ctx "#000000")
    (doseq [[row col] cells]
      (canvas/fill-rect ctx {:x (+ margin (* col cell-size))
                             :y (+ margin (* row cell-size))
                             :h cell-size
                             :w cell-size}))))

(defn init
  [mc {:keys [nrows ncols cell-size margin] :as opts}]
  (canvas/add-entity mc :border (canvas/entity {:x margin :y margin :h (* nrows cell-size) :w (* ncols cell-size)}
                                               nil
                                               (fn [ctx border]
                                                 (-> ctx
                                                     (canvas/stroke-style "#000000")
                                                     (canvas/stroke-rect border)))))
  (canvas/add-entity mc :grid (canvas/entity (init-grid nrows ncols)
                                             (update-grid nrows ncols)
                                             (render-grid margin cell-size))))
