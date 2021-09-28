(ns ttt-reagent.core-spec
  (:require-macros [speclj.core :refer [describe context it should=]])
  (:require [speclj.core]
            [ttt-reagent.components :as components]))

(describe "arena component"
  (context "rendering"
    (context "empty 3x3 grid"
      (it "renders boxes ready to be clicked"
        (let [rendered (components/arena)]
          (should= :svg (first rendered))
          (should= {:view-box "0 0 3 3"
                    :width    500
                    :height   500} (second rendered))
          (doseq [box (drop 2 rendered)]
            (let [tag     (first box)
                  config  (second box)
                  x       (:x config)
                  y       (:y config)
                  clicker (:on-click config)]
              (should= :rect tag)
              (should= 0.9 (:width config))
              (should= 0.9 (:height config))
              (should= "grey" (:fill config))
              (should= [x y] (clicker))))))
      )
    )
  )

(describe "hello-world component"
  (it "says hello to the entire world"
    (should= [:h1 "Hello, world!"] (components/hello-world))))