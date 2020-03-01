(ns recipe-search.core
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [recipe-search.spec :as rss]
            [clojure.set :as set]
            [recipe-search.database :as db]
            [recipe-search.preprocessing :as pre]
            [recipe-search.ranking :as rank]))

(defn search-in [words]
  "Return set of recipe-ids which contains all requested words"
  (reduce set/intersection (map db/get-recipes-with-word (pre/preprocess-words words))))

(defn search [words]
  (let [preprocessed-words (pre/preprocess-words words)
        matched-recipes (map db/get-recipe (search-in words))]
    (take 10 (sort-by (partial rank/rank preprocessed-words) > matched-recipes))))

; (time (map ::rss/id (search ["tomatoes" "olives" "onion"])))

(defn -main [& args]
  (db/init)
  )