(ns ttt-reagent.core
  (:require [reagent.dom :as rdom]
            [ttt-reagent.components :as components]))

(defn screen []
  [:div
   [components/arena]
   [components/start-over]
   [:details
    [:summary "Settings"]
    [components/grid-size-selection]
    [components/player-selection :X]
    [components/player-selection :O]]])

(defn ^:export main []
  (println "Hello, from main!")
  (rdom/render
    [screen]
    (js/document.getElementById "app")))

(main)