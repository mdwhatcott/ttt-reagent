(ns ttt-reagent.components
  (:require
    [ttt.grid]
    [reagent.core :as reagent]))

(def COLOR
  {:winner "darkseagreen"
   :loser "lightsalmon"
   :empty "lightsteelblue"})

(defn new-game [grid-width]
  {:grid (ttt.grid/new-grid grid-width)
   :mark :X})

(defonce game (reagent/atom (new-game 3)))

(defn other-mark [mark]
  (if (= mark :X) :O :X))

(defn switch-mark! []
  (swap! game update :mark other-mark))

(defn cartesian->index [width x y]
  (+ x (* width y)))

(defn place-on-grid! [mark on]
  (swap! game update :grid #(ttt.grid/place mark on %)))

(defn make-on-click-for-box [x y]
  (fn rect-click [_e]
    (let [mark  (:mark @game)
          index (cartesian->index 3 x y)]
      (place-on-grid! mark index)
      (switch-mark!)
      [x y])))

(defn yet-to-be-clicked? [x y]
  (let [grid        (:grid @game)
        width       (:width grid)
        empty-cells (:empty-cells grid)]
    (contains? empty-cells (cartesian->index width x y))))

(defn box-fill-color [x y grid]
  (let [winner          (:winner grid)
        index           (cartesian->index 3 x y)
        placed          (get (:filled-by-cell grid) index)
        is-winning-box? (and (some? winner) (= winner placed))
        is-losing-box?  (and (some? winner) (some? placed) (not= winner placed))]
    (cond is-winning-box? (:winner COLOR)
          is-losing-box?, (:loser COLOR)
          :else,,,,,,,,,, (:empty COLOR))))

(defn make-grid-box [x y grid]
  (let [pending-click?  (yet-to-be-clicked? x y)]
    [:rect {:width    0.9
            :height   0.9
            :x        x
            :y        y
            :fill     (box-fill-color x y grid)
            :on-click (when pending-click? (make-on-click-for-box x y))}]))

(defn make-mark [cell mark]
  (let [x (rem cell 3)
        y (quot cell 3)]
    [:text {:x           (+ 0.1 x)
            :y           (+ 0.8 y)
            :font-size   "1px"
            :font-family "monospaced"} (name mark)]))

(defn make-boxes [grid]
  (let [width (:width grid)]
    (for [y (range width)
          x (range width)]
      (make-grid-box x y grid))))

(defn make-marks [grid]
  (for [[cell mark] (:filled-by-cell grid)]
    (make-mark cell mark)))

(defn make-svg [_grid]
  [:svg {:view-box "0 0 3 3"
         :width    500
         :height   500}])

(defn arena []
  (let [grid (:grid @game)]
    (into
      (make-svg grid)
      (concat
        (make-boxes grid)
        (make-marks grid)))))
