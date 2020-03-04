(ns recipe-search.ranking
  (:require [clojure.spec.alpha :as s]
            [taoensso.timbre :as log]
            [recipe-search.spec :as rss]))

;;; For now just one ranking function
;;; But it is easily possible to improve the program by adding additional functions or comparators which are selectable from the UI

(defn rank
  "Simple rank function which sums number of occurrences of all words"
  [words preprocessed-recipe]
  #_{:pre [(s/valid? ::rss/recipe preprocessed-recipe)
           (s/valid? ::rss/words words)]}
  (let [freqs (::rss/text-frequencies preprocessed-recipe)]
    (reduce + (map #(get freqs %) words))))

(defn- count-positions [words ingredients]
  "Returns sum of positions and number of not found words"
  (reduce (fn [acc word]
            (let [index (.indexOf ingredients word)]
              (if (neg? index)                              ;; when not found just count it as not-found, otherwise, increase sum of positions by position
                (update acc :not-found-count inc)
                (update acc :positions-sum #(+ % index))))) {:positions-sum 0 :not-found-count 0} words))

(defn rank-by-ingredients-fn [words x y]
  "Rank function using the sum of position of all occurences of all words.
  If not found, the recipe with less not found elements is first."
  (let [x-ingredients (::rss/ingredients x)
        y-ingredients (::rss/ingredients y)
        {x-not-found-count :not-found-count x-positions-sum :positions-sum} (count-positions words x-ingredients)
        {y-not-found-count :not-found-count y-positions-sum :positions-sum} (count-positions words y-ingredients)]
    (cond
      ;; the less not found elements, the better
      (< x-not-found-count y-not-found-count) true
      (< y-not-found-count x-not-found-count) false
      ;; if equal not-founds then the lower sum, the better
      (< x-positions-sum y-positions-sum) true
      :else false)))

(defn sort-by-rank [rank-by-option preprocessed-words recipes]
  "Sorts list of recipes based on rank-by option and words which are being searched by the used"
  {:pre [(s/valid? ::rss/rank-by-option rank-by-option)]}
  (case rank-by-option
    ::rss/rank-by-words-occurrences (sort-by (partial rank preprocessed-words) > recipes)
    ::rss/rank-by-ingredients-position (sort #(rank-by-ingredients-fn preprocessed-words %1 %2) recipes)))

#_(time (do (sort-by #(rank (pre/preprocess-words ["tomatoes" "olives" "onion"]) %)
                     (recipe-search.search/search (:recipes-db system.repl/system) ["tomatoes" "olives" "onion"]))
            nil))