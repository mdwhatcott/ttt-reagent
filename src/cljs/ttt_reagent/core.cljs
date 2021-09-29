(ns ttt-reagent.core
  (:require [goog.dom :as gdom]
            [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [ttt-reagent.components :as components]))

(defn screen []
  [:div
   [components/arena]
   [components/start-over]])

(defn ^:export main []
  (println "Hello, from main!")
  (rdom/render
    [screen]
    (js/document.getElementById "app")))

(main)