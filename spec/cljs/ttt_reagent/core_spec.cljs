(ns ttt-reagent.core-spec
  (:require-macros [speclj.core :refer [describe context before it should=]])
  (:require [speclj.core]
            [ttt-reagent.components :as components]
            [cljs.pprint :as pprint]))

(defn parse-arena [arena grid-width]
  #_(pprint/pprint arena)
  (let [box-count (* grid-width grid-width)]
    {:root       (first arena)
     :attributes (second arena)
     :boxes      (->> arena (drop 2) (take box-count) vec)
     :marks      (->> arena (drop 2) (drop box-count) vec)}))

(defn click-box [index]
  (let [before     (components/arena)
        parsed     (parse-arena before 3)
        box        (get (:boxes parsed) index)
        attributes (second box)
        on-click   (:on-click attributes)]
    (on-click)))

(describe "arena component"
  (context "rendering - 3x3"

    (before (reset! components/game (components/new-game 3)))

    (context "empty grid"

      (it "renders empty boxes ready to be clicked"
        (let [rendered        (components/arena)
              parsed          (parse-arena rendered 3)
              root-attributes (:attributes parsed)]
          (should= :svg (:root parsed))
          (should= "0 0 3 3" (:view-box root-attributes))
          (should= 500 (:width root-attributes))
          (should= 500 (:height root-attributes))

          (doseq [box (:boxes parsed)]
            (let [tag            (first box)
                  box-attributes (second box)
                  x              (:x box-attributes)
                  y              (:y box-attributes)
                  clicker        (:on-click box-attributes)]
              (should= :rect tag)
              (should= 0.9 (:width box-attributes))
              (should= 0.9 (:height box-attributes))
              (should= "grey" (:fill box-attributes))
              (should= [x y] (clicker))))))
      )

    (context "after first turn by X"
      (before (click-box 0))

      (it "renders the selected box without an on-click handler"
        (let [rendered    (components/arena)
              parsed      (parse-arena rendered 3)
              clicked-box (first (:boxes parsed))
              tag         (first clicked-box)
              config      (second clicked-box)]
          (should= :rect tag)
          (should= 0.9 (:width config))
          (should= 0.9 (:height config))
          (should= "white" (:fill config))
          (should= nil (:on-click config))))

      (it "switches the player/mark"
        (should= :O (:mark @components/game)))

      (it "renders an 'X' in the clicked box"
        (let [rendered    (components/arena)
              parsed      (parse-arena rendered 3)
              clicked-box (first (:boxes parsed))
              box-config  (second clicked-box)

              mark        (first (:marks parsed))
              tag         (first mark)
              attributes  (second mark)
              text        (get mark 2)]
          (should= :text tag)
          (should= (int (:x attributes)) (:x box-config))
          (should= (int (:y attributes)) (:y box-config))
          (should= "X" text)))
      )

    (context "After X and O both take a turn"
      (before (click-box 0)
              (click-box 1))

      (it "the player/mark gets switched back to 'X'"
        (should= :X (:mark @components/game)))

      (it "renders an 'O' in the box that was clicked second"
        (let [rendered    (components/arena)
              parsed      (parse-arena rendered 3)
              clicked-box (second (:boxes parsed))
              box-config  (second clicked-box)

              mark        (second (:marks parsed))
              tag         (first mark)
              attributes  (second mark)
              text        (get mark 2)]
          (should= :text tag)
          (should= (int (:x attributes)) (:x box-config))
          (should= (int (:y attributes)) (:y box-config))
          (should= "O" text)))
      )
    )
  )
