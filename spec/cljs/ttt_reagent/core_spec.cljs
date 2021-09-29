(ns ttt-reagent.core-spec
  (:require-macros [speclj.core :refer [describe context it should=]])
  (:require [speclj.core]
            [ttt-reagent.components :as components]))

(describe "arena component"
  (context "rendering"
    (context "empty 3x3 grid"
      (it "renders empty boxes ready to be clicked"
        (let [rendered        (components/arena)
              root            (first rendered)
              root-attributes (second rendered)
              boxes           (drop 2 rendered)]
          (should= :svg root)
          (should= "0 0 3 3" (:view-box root-attributes))
          (should= 500 (:width root-attributes))
          (should= 500 (:height root-attributes))

          (doseq [box boxes]
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

    (context "after first turn by X on 3x3 grid"
      (it "renders the selected box without an on-click handler and with a different background"
        (components/place-on-grid! :X 0)
        (let [rendered    (components/arena)
              clicked-box (first (drop 2 rendered))
              tag         (first clicked-box)
              config      (second clicked-box)]
          (should= :rect tag)
          (should= 0.9 (:width config))
          (should= 0.9 (:height config))
          (should= "white" (:fill config))
          (should= nil (:on-click config)))))
    )
  )


(describe "hello-world component"
  (it "says hello to the entire world"
    (should= [:h1 "Hello, world!"] (components/hello-world))))