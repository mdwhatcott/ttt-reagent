(ns ttt-reagent.components
  (:require
    [goog.string :as string]
    [goog.string.format]
    [clojure.string]
    [ttt.grid]
    [reagent.core :as reagent]))

(defonce grid (reagent/atom (ttt.grid/new-grid 3)))
(defonce mark (reagent/atom :X))

(defn new-game! [width]
  (reset! grid (ttt.grid/new-grid width))
  (reset! mark :X))

(defn other-mark [mark]
  (if (= mark :X) :O :X))

(defn switch-mark! []
  (swap! mark other-mark))

(defn cartesian->index [x y]
  (+ x (* (:width @grid) y)))

(defn place-on-grid! [on]
  (swap! grid #(ttt.grid/place @mark on %)))

(defn make-on-click-for-box [x y]
  (fn rect-click [_e]
    (let [index (cartesian->index x y)]
      (place-on-grid! index)
      (switch-mark!) nil)))

(defn yet-to-be-clicked? [x y]
  (let [empty-cells (:empty-cells @grid)]
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

; TODO: move static values to <html><style>?
(defn make-grid-box [x y grid]
  (let [pending-click? (yet-to-be-clicked? x y)]
    [:rect {:x        x
            :y        y
            :rx       0.1
            :ry       0.1
            :width    0.9
            :height   0.9
            :class    (box-class x y grid)
            :on-click (when pending-click? (make-on-click-for-box x y))}]))

; TODO: draw lines for 'x' and circle for 'o'
(defn make-mark [cell mark]
  (let [w (:width @grid)
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

; TODO: move static values to <html><style>?
(defn make-svg [grid]
  (let [width    (:width grid)
        view-box (string/format "0 0 %d %d" width width)]
    [:svg {:view-box view-box
           :width    "100%"
           :height   "100%"}]))

(defn arena []
  (into
    (make-svg @grid)
    (concat
      (make-boxes @grid)
      (make-marks @grid))))

(defn start-over []
  (let [on-click (fn start-over-button-click [_e] (new-game! (:width @grid)))]
    [:button {:on-click on-click} "New Game"]))

(defn radio-id-value [name value]
  (-> (string/format "%s--%s" name value)
      (clojure.string/replace " " "-")
      (clojure.string/lower-case)))

(defn radio [name value checked? on-click]
  (let [id-value   (radio-id-value name value)
        ; TODO: is :value necessary?
        attributes {:type :radio, :id id-value, :name name, :value id-value, :on-click on-click}
        ; TODO: get this working (but without the need for a double-click)
        #_attributes #_(if checked? (assoc attributes :checked "true") attributes)]
    [:div
     [:input attributes]
     [:label {:for id-value} value]]))

(defn set-grid-width [n]
  (fn [_e]
    (when (not= n (:width @grid))
      (new-game! n))))

(defn grid-size-selection []
  [:div
   [:p "Grid Size:)"]
   (radio "grid-size-selection" "3x3" true (set-grid-width 3))
   (radio "grid-size-selection" "4x4" false (set-grid-width 4))])

