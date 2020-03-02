(ns recipe-search.ranking
  (:require [clojure.spec.alpha :as s]
            [recipe-search.spec :as rss]))

;;; For now just one ranking function
;;; But it is easily possible to improve the program by adding additional functions or comparators which are selectable from the UI

(defn rank
  "Simple rank function which sums number of occurences of all words"
  [words preprocessed-recipe]
  #_{:pre [(s/valid? ::rss/recipe preprocessed-recipe)
           (s/valid? ::rss/words words)]}
  (let [freqs (::rss/text-frequencies preprocessed-recipe)]
    (reduce + (map #(get freqs %) words))))

#_(time (do (sort-by #(rank (pre/preprocess-words ["tomatoes" "olives" "onion"]) %)
                     (recipe-search.search/search (:recipes-db system.repl/system) ["tomatoes" "olives" "onion"]))
            nil))
