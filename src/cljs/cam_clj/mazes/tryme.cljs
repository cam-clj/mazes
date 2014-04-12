(ns cam-clj.mazes.tryme
  (:require [monet.canvas :as canvas]))

(comment
(def dom-canvas (.getElementById js/document "maze-canvas"))

(set! (.-height dom-canvas) 200)
(set! (.-width dom-canvas) 200)

(def ctx (canvas/get-context dom-canvas "2d"))

(canvas/fill-style ctx "#FFFFFF")

(canvas/fill-rect ctx {:x 0 :y 0 :w 200 :h 200})

(canvas/move-to ctx 0 0)
(canvas/line-to ctx 0 20)
(canvas/stroke ctx)

(canvas/move-to ctx 0 0)
(canvas/line-to ctx 20 0)
(canvas/stroke ctx))
