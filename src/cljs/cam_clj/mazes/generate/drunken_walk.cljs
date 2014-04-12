(ns cam-clj.mazes.generate.drunken-walk
  (:require [cam-clj.mazes.common :as common]))

(def init-cell #{:north :east :south :west})

(defn init-cells
  [nrows ncols]
  (into [] (repeatedly nrows #(into [] (repeat ncols init-cell)))))

(defn remove-wall
  [cells row col direction]
  (let [[delta-row delta-col] (common/delta direction)
        row' (+ row delta-row)
        col' (+ col delta-col)]
    (-> cells
        (update-in [row col] disj direction)
        (update-in [row' col'] disj (common/opposite direction)))))

(defn neighbours
  [row col nrows ncols]
  (for [[direction [delta-row delta-col]] common/delta
        :let [row' (+ row delta-row)
              col' (+ col delta-col)]
        :when (and (< -1 row' nrows)
                   (< -1 col' ncols))]
    {:direction direction :cell [row' col']}))

(defn drunken-walk
  [nrows ncols]
  (let [num-cells (* nrows ncols)]
    (loop [[row col] [0 0] visited #{} cells (init-cells nrows ncols)]
      (println "Visiting" [row col])
      (let [next-visited (conj visited [row col])]
        (if (= (count next-visited) num-cells)
          cells
          (let [{:keys [direction cell]} (rand-nth (vec (neighbours row col nrows ncols)))
                next-cells            (if (visited cell) cells (remove-wall cells row col direction))]
            (println "Travelling" direction)
            (recur cell next-visited next-cells)))))))
