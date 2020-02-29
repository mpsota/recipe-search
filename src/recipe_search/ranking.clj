(ns recipe-search.ranking)


(defn rank
  "Simple rank function which sums number of occurences of all words"
  [words preprocessed-recipe]
  (let [freqs (:text-frequencies preprocessed-recipe)]
      (reduce + (map #(get freqs %) words))))


(defn sort-by-rank [recipes words]
  ;(rank/rank (get-recipe "venison-ragu.txt") (pre/preprocess-words ["tomatoes" "olives" "onion"]))
  (sort recipes (fn [a b] ))

  )

; (time (sort-by  #(rank/rank (get-recipe %) (pre/preprocess-words ["tomatoes" "olives" "onion"])) (search ["tomatoes" "olives" "onion"])))
;

; (time (let [preprocessed-words (pre/preprocess-words ["tomatoes" "olives" "onion"])]
;  (reduce (fn [acc id] (assoc acc id  (rank/rank (get-recipe id) preprocessed-words))) {} (search ["tomatoes" "olives" "onion"]))))
;