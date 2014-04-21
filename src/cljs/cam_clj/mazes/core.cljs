(ns cam-clj.mazes.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan put! <!]]
            [monet.canvas :as canvas]
            [om.core :as om]
            [om.dom :as dom]
            [cam-clj.mazes.cellular-automata :as cellular-automata]))

(def app-state (atom {:nrows 20 :ncols 20 :cell-size 16 :margin 4 :builder cellular-automata/init}))

(defn parse-int
  [v]
  (when (and v (re-find #"^\d+$" v))
    (js/parseInt v)))

(defn update-grid-dimension
  [app owner dim]
  (when-let [v (-> (om/get-node owner (name dim))
                   .-value
                   parse-int)]
    (om/update! app [dim] v)))

(defn update-dim-button
  [app owner {:keys [dim label]}]
  (reify
    om/IRender
    (render
     [_]
     (dom/div nil
              (dom/label #js {:htmlFor (name dim)} label)
              (dom/input #js {:id (name dim)
                              :ref (name dim)
                              :defaultValue (app dim)
                              :onKeyPress (fn [e] (when (== (.-keyCode e) 13)
                                                    (update-grid-dimension app owner dim)))
                              :onBlur (fn [_] (update-grid-dimension app owner dim))})))))

(defn maze-builder
  [{:keys [nrows ncols cell-size margin builder] :as app} owner]
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
       (om/set-state! owner :monet-canvas mc)
       (builder mc (select-keys app [:nrows :ncols :cell-size :margin]))
       (go (loop []
             (when-let [action (<! ch)]
               (case action
                 :stop (canvas/stop mc)
                 :restart (canvas/restart mc)
                 :stop-updating (canvas/stop-updating mc)
                 :start-updating (canvas/start-updating mc)
                 :refresh (do
                            (canvas/clear! mc)
                            ((:builder @app) mc (select-keys @app [:nrows :ncols :cell-size :margin]))))
               (recur))))))
    om/IDidUpdate
    (did-update
     [_ prev-props prev-state]
     (let [mc (om/get-state owner [:monet-canvas])]
       ((:builder app) mc (select-keys app [:nrows :ncols :cell-size :margin]))))
    om/IRenderState
    (render-state
     [_ {:keys [ch]}]
     (dom/div nil
              (dom/canvas #js {:height (+ (* 2 margin) (* nrows cell-size))
                               :width  (+ (* 2 margin) (* ncols cell-size))
                               :ref "canvas"})
              (dom/hr nil)
              (om/build update-dim-button app {:opts {:dim :nrows :label "Rows"}})
              (om/build update-dim-button app {:opts {:dim :ncols :label "Columns"}})
              (dom/button #js {:onClick (fn [_] (put! ch :stop))} "Stop")
              (dom/button #js {:onClick (fn [_] (put! ch :restart))} "Restart")
              (dom/button #js {:onClick (fn [_] (put! ch :stop-updating))} "Stop Updating")
              (dom/button #js {:onClick (fn [_] (put! ch :start-updating))} "Start Updating")
              (dom/button #js {:onClick (fn [_] (put! ch :refresh))} "Refresh")))))

(om/root
 maze-builder
 app-state
 {:target (. js/document (getElementById "app"))})
