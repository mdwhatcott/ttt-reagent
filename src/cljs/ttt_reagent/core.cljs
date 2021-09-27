(ns ttt-reagent.core
  (:require [goog.dom :as gdom]
            [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]))

(defonce state (atom {}))

