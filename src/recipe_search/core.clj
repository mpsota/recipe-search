(ns recipe-search.core
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [recipe-search.preprocessing :as pre]
            [recipe-search.ranking :as rank]))

(def db "Database containg slurped recipes" (atom {}) )
(def words-index "Index of all words used in all recipes. Key is word, value is set of recipe-ids with this word" (atom {}))

(def recipes (atom {}))

(defn read-recipe [file]
  ; Some of the recipes files has user readable format:
  ; <TITLE>
  ; Introduction:
  ; <TEXT>
  ; Ingredients:"
  ; <TEXT>
  ; Method:
  ; <TEXT>
  ; Let's try to use it - first by saving it where possible, later to improve ranking algorithm
  {:id       (.getName file)
   :raw-text (slurp file)})

(defn initialize-database
  "Creates database (for simplicity as in memory map) with read and preprocessed recipes"
  [recipes-files]
  (reduce (fn [acc file]
            (assoc acc (.getName file) (-> file
                                           read-recipe
                                           pre/preprocess-recipe)))
          {}
          recipes-files))

(defn create-index [db]
  (reduce (fn [acc recipe]
            (let [id (:id recipe)]
              (reduce (fn [acc word]
                        ;(print acc word)
                        (assoc acc word (conj (get acc word #{}) id)))
                      acc
                      (:text recipe))))
          {}
          (vals db)))

(defn search-in [words]
  "Return set of recipe-ids which contains all requested words"
  (reduce set/intersection (map #(get @words-index %) (pre/preprocess-words words))))

(defn get-recipe [id]
  (get @db id))

(defn search [words]
  (let [preprocessed-words (pre/preprocess-words words)
        matched-recipes (map get-recipe (search-in words))]
    (take 10 (sort-by (partial rank/rank preprocessed-words) > matched-recipes))))

; (time (map :id (search ["tomatoes" "olives" "onion"])))

(defn init
  "Initialize 'database'"
  []
  (reset! recipes (->> "./resources/recipes"
                       clojure.java.io/file
                       file-seq
                       (filter #(.isFile %))))
  (reset! db (initialize-database @recipes))
  (reset! words-index (create-index @db))
  true)

#_(defn -main [& args]
  (init)
  )
