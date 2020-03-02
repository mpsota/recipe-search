(ns recipe-search.search
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [recipe-search.spec :as rss]
            [clojure.set :as set]
            [recipe-search.database :as db]
            [recipe-search.preprocessing :as pre]
            [recipe-search.ranking :as rank]))

(defn- get-recipes-with-words [recipes-db words]
  "Return set of recipe-ids which contains all requested words"
  (reduce set/intersection (map #(db/get-recipes-with-word recipes-db %) (pre/preprocess-words words))))

(defn search
  "Main search function. Uses rank algorithm. Optional keyseq argument to narrow the results to given keys"
  ([recipes-db words keyseq]
   (map #(select-keys % keyseq) (search recipes-db words)))
  ([recipes-db words]
   (let [preprocessed-words (pre/preprocess-words words)
         matched-recipes (map #(db/get-recipe recipes-db %) (get-recipes-with-words recipes-db words))]
     (take 10 (sort-by (partial rank/rank preprocessed-words) > matched-recipes)))))

; (search (:recipes-db system.repl/system) ["tomatoes" "olives" "onion"] [::rss/id ::rss/raw-text])
; (time (map ::rss/id (search (:recipes-db system.repl/system) ["tomatoes" "olives" "onion"])))

