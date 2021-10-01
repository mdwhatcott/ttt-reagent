(ns ttt-reagent.components
  (:require
    [goog.string :as string]
    [goog.string.format]
    [clojure.string]
    [ttt.ai]
    [ttt.grid]
    [reagent.core :as reagent]))

(defonce grid (reagent/atom (ttt.grid/new-grid 3)))
(defonce mark (reagent/atom :X))
(defonce players (reagent/atom {:X :human
                                :O :human}))
(defn new-game! [width]
  (reset! grid (ttt.grid/new-grid width))
  (reset! mark :X))

(defn other-mark [mark]
  (if (= mark :X) :O :X))

(defn cartesian->index [x y]
  (+ x (* (:width @grid) y)))

(defn make-move [x y]
  (let [player (@mark @players)]
    (if (= player :human)
      (cartesian->index x y)
      (let [ai (player ttt.ai/players)]
        (ai @mark @grid)))))

(defn make-on-click-for-box [x y]
  (fn rect-click [_e]
    (let [on (make-move x y)]
      (reset! grid (ttt.grid/place @mark on @grid))
      (swap! mark other-mark) nil)))

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

(defn make-grid-box [x y grid]
  (let [pending-click? (yet-to-be-clicked? x y)]
    [:rect {:x        x
            :y        y
            :rx       0.1
            :ry       0.1
            :width    0.9
            :height   0.9
            :class    (box-class x y grid)
            :on-click (when pending-click?
                        (make-on-click-for-box x y))}]))

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
  (let [on-click (fn [_e] (new-game! (:width @grid)))]
    [:button {:on-click on-click} "New Game"]))

(defn radio-id-value [name value]
  (-> (string/format "%s--%s" name value)
      (clojure.string/replace " " "-")
      (clojure.string/lower-case)))

(defn radio [name value on-click]
  (let [name       (clojure.string/lower-case name)
        id-value   (radio-id-value name value)
        attributes {:type     :radio
                    :id       id-value
                    :name     name
                    :on-click on-click}]
    [:div
     [:input attributes]
     [:label {:for id-value} value]]))

(defn set-grid-width [n]
  (fn [_e]
    (when-not (= n (:width @grid))
      (new-game! n))))

(defn grid-size-selection []
  [:div
   [:p "Grid Size:"]
   (radio "grid-size-selection" "3x3" (set-grid-width 3))
   (radio "grid-size-selection" "4x4" (set-grid-width 4))])

(defn set-player [mark player]
  (fn [_e]
    (swap! players assoc mark player)))

(defn player-selection [mark]
  (let [intro (string/format "Player '%s'" (name mark))
        name  (string/format "player-%s-selection" (name mark))]
    [:div
     [:p intro]
     (radio name "Human" (set-player mark :human))
     (radio name "Easy AI" (set-player mark :easy))
     (radio name "Medium AI" (set-player mark :medium))
     (radio name "Hard AI" (set-player mark :hard))]))
