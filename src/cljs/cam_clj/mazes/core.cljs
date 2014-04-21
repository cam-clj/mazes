(ns cam-clj.mazes.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan put! <!]]
            [monet.canvas :as canvas]
            [om.core :as om]
            [om.dom :as dom]))

(def app-state (atom {:nrows 10 :ncols 10 :cell-size 16 :margin 4 :wall-thickness 2}))

(defn placeholder
  [{:keys [nrows ncols cell-size margin wall-thickness] :as app} owner]
  (reify
    om/IInitState
    (init-state
     [_]
     {:ch (chan)})
    om/IDidMount
    (did-mount
     [_]
     (let [dom-canvas (om/get-node owner "canvas")
           mc         (canvas/init dom-canvas "2d")
           ch         (om/get-state owner [:ch])]
       (canvas/add-entity mc :border (canvas/entity
                                      {:x margin :y margin :h (* nrows cell-size) :w (* ncols cell-size)}
                                      nil
                                      (fn [ctx border]
                                        (-> ctx
                                            (canvas/stroke-style "#000000")
                                            (canvas/stroke-rect border)))))
       (canvas/add-entity mc :walker (canvas/entity
                                      {:row 0 :col 0}
                                      (fn [{:keys [row col]}]
                                        (let [[delta-row delta-col] (rand-nth [[0 1] [0 -1] [1 0] [-1 0]])]
                                          {:row (mod (+ row delta-row) nrows)
                                           :col (mod (+ col delta-col) ncols)}))
                                      (fn [ctx {:keys [row col]}]
                                        (-> ctx
                                            (canvas/fill-style "red")
                                            (canvas/fill-rect {:x (+ margin (* col cell-size))
                                                               :y (+ margin (* row cell-size))
                                                               :h cell-size
                                                               :w cell-size})))))
       (go (loop []
             (when-let [action (<! ch)]
               (case action
                 :stop (canvas/stop mc)
                 :restart (canvas/restart mc)
                 :stop-updating (canvas/stop-updating mc)
                 :start-updating (canvas/start-updating mc))
               (recur))))))
    om/IRenderState
    (render-state
     [_ {:keys [ch]}]
     (dom/div nil
              (dom/canvas #js {:height (+ (* 2 margin) (* nrows cell-size))
                               :width  (+ (* 2 margin) (* ncols cell-size))
                               :ref "canvas"})
              (dom/button #js {:onClick (fn [_] (put! ch :stop))} "Stop")
              (dom/button #js {:onClick (fn [_] (put! ch :restart))} "Restart")
              (dom/button #js {:onClick (fn [_] (put! ch :stop-updating))} "Stop Updating")
              (dom/button #js {:onClick (fn [_] (put! ch :start-updating))} "Start Updating")))))

(om/root
 placeholder
 app-state
 {:target (. js/document (getElementById "app"))})
