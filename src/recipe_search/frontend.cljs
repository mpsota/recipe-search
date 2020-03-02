(ns recipe-search.frontend
  (:require [recipe-search.ui :as ui]
            [reagent.dom :as rdom]))

(defn ^:export start-frontend []
  (rdom/render [ui/main-page] (js/document.getElementById "app")))