(ns ttt-reagent.components
  (:require
    [goog.string :as gstring]
    [goog.string.format]
    [ttt.grid]
    [reagent.core :as reagent]))

(defonce grid-width (reagent/atom 3))

(defn new-game []
  {:grid (ttt.grid/new-grid @grid-width)
   :mark :X})

(defonce game (reagent/atom (new-game)))

(defn other-mark [mark]
  (if (= mark :X) :O :X))

(defn switch-mark! []
  (swap! game update :mark other-mark))

(defn cartesian->index [x y]
  (+ x (* @grid-width y)))

(defn place-on-grid! [mark on]
  (swap! game update :grid #(ttt.grid/place mark on %)))

(defn make-on-click-for-box [x y]
  (fn rect-click [_e]
    (let [mark  (:mark @game)
          index (cartesian->index x y)]
      (place-on-grid! mark index)
      (switch-mark!) nil)))

(defn yet-to-be-clicked? [x y]
  (let [grid        (:grid @game)
        empty-cells (:empty-cells grid)]
    (contains? empty-cells (cartesian->index x y))))

(defn is-winning-box? [winner placed]
  (and (some? winner)
       (= winner placed)))

(defn is-losing-box? [winner placed]
  (and (some? winner)
       (some? placed)
       (not= winner placed)))

(defn box-class [x y grid]
  (let [winner (:winner grid)
        index  (cartesian->index x y)
        mark   (get (:filled-by-cell grid) index)]
    (cond (is-winning-box? winner mark) :winner
          (is-losing-box? winner mark), :loser
          :else,,,,,,,,,,,,,,,,,,,,,,,, :empty)))

(defn make-grid-box [x y grid]
  (let [pending-click? (yet-to-be-clicked? x y)]
    [:rect {:width    0.9
            :height   0.9
            :x        x
            :y        y
            :rx       0.1
            :ry       0.1
            :class    (box-class x y grid)
            :on-click (when pending-click? (make-on-click-for-box x y))}]))

(defn make-mark [cell mark]
  (let [w @grid-width
        x (rem cell w)
        y (quot cell w)]
    [:text {:x         (+ 0.15 x)
            :y         (+ 0.75 y)
            :font-size "1px"} (name mark)]))

(defn make-boxes [grid]
  (let [width (:width grid)]
    (for [y (range width)
          x (range width)]
      (make-grid-box x y grid))))

(defn make-marks [grid]
  (for [[cell mark] (:filled-by-cell grid)]
    (make-mark cell mark)))

(defn make-svg [_grid]
  [:svg {:view-box (gstring/format "0 0 %d %d" @grid-width @grid-width)
         :width    "100%"
         :height   "100%"}])

(defn arena []
  (let [grid (:grid @game)]
    (into
      (make-svg grid)
      (concat
        (make-boxes grid)
        (make-marks grid)))))

(defn start-over []
  (let [on-click (fn start-over-button-click [e]
                   (reset! game (new-game)))]
    [:button {:on-click on-click} "New Game"]))
