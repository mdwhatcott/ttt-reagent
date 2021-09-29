(ns ttt-reagent.core
  (:require [goog.dom :as gdom]
            [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [ttt-reagent.components :as components]))

(defn screen []
  [:div
   [components/hello-world]
   [components/arena]])

(defn ^:export main []
  (println "Hello, from main!")
  (rdom/render
    [screen]
    (js/document.getElementById "app")))

(main)