(ns ttt-reagent.components
  (:require
    [ttt.grid]
    [reagent.core :as reagent]))

(defonce game (reagent/atom {:grid (ttt.grid/new-grid 3)}))

(defn make-on-click [x y]
  (fn rect-click [e]
    (println "clicked!" x y)
    [x y]))

(defn arena []
  (let [grid  (:grid @game)
        width (:width grid)]
    (into
      [:svg {:view-box "0 0 3 3"
             :width    500
             :height   500}]
      (for [y (range width)
            x (range width)]
        [:rect {:width    0.9
                :height   0.9
                :fill     "grey"
                :x        x
                :y        y
                :on-click (make-on-click x y)}]))))

(defn hello-world []
  [:h1 "Hello, world!"])
