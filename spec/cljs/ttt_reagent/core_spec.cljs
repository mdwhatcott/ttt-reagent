(ns ttt-reagent.core-spec
  (:require-macros [speclj.core :refer [describe context before it should=]])
  (:require [speclj.core]
            [ttt-reagent.components :as components]))

(defn parse-arena [arena grid-width]
  (let [box-count (* grid-width grid-width)]
    {:root       (first arena)
     :attributes (second arena)
     :boxes      (->> arena (drop 2) (take box-count))
     :marks      (->> arena (drop 2) (drop box-count))}))

(describe "arena component"
  (context "rendering - 3x3"

    (before (reset! components/game (components/new-game 3)))

    (context "empty grid"

      (it "renders empty boxes ready to be clicked"
        (let [rendered        (components/arena)
              parsed          (parse-arena rendered @components/grid-width)
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
      (components/place-on-grid! :X 0)

      (it "renders the selected box without an on-click handler and with a different background"
        (let [rendered    (components/arena)
              parsed      (parse-arena rendered @components/grid-width)
              clicked-box (first (:boxes parsed))
              tag         (first clicked-box)
              config      (second clicked-box)]
          (should= :rect tag)
          (should= 0.9 (:width config))
          (should= 0.9 (:height config))
          (should= "white" (:fill config))
          (should= nil (:on-click config))))

      )
    )
  )
