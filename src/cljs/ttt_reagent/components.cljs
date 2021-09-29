(ns ttt-reagent.components
  (:require
    [ttt.grid]
    [reagent.core :as reagent]))

(defonce game
  (reagent/atom
    {:grid (ttt.grid/new-grid 3)
     :mark :X}))

; TODO: figure out why defining this from the test code causes compilation failure. "Caused by: clojure.lang.ExceptionInfo: No such namespace: ttt-grid, could not locate ttt_grid.cljs, ttt_grid.cljc, or JavaScript source providing "ttt-grid" (Please check that namespaces with dashes use underscores in the ClojureScript file name) in file spec/cljs/ttt_reagent/core_spec.cljs {:tag :cljs/analysis-error}"
(defn place-on-grid! [mark on]
  (let [grid (:grid @game)]
    (swap! game assoc :grid (ttt.grid/place mark on grid))))

(defn make-on-click-for-box [x y]
  (fn rect-click [e]
    [x y]))

(defn cartesian->index [width x y]
  (+ x (* width y)))

(defn yet-to-be-clicked? [x y]
  (let [grid        (:grid @game)
        width       (:width grid)
        empty-cells (:empty-cells grid)]
    (empty-cells (cartesian->index width x y))))

(defn make-grid-box [x y pending-click?]
  [:rect {:width    0.9
          :height   0.9
          :x        x
          :y        y
          :fill     (if pending-click? "grey" "white")
          :on-click (if pending-click? (make-on-click-for-box x y))}])

(defn arena []
  (let [grid  (:grid @game)
        width (:width grid)]
    (into
      [:svg {:view-box "0 0 3 3"                            ; TODO: derive from (:width grid)
             :width    500
             :height   500}]
      (for [y (range width)
            x (range width)]
        (make-grid-box x y (yet-to-be-clicked? x y))))))

(defn hello-world []
  [:h1 "Hello, world!"])
