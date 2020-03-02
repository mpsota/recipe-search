(ns recipe-search.database
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [recipe-search.spec :as rss]
            [clojure.set :as set]
            [recipe-search.preprocessing :as pre]
            [recipe-search.ranking :as rank]
            [com.stuartsierra.component :as component]))

(defn- read-recipe [file]
  ; Some of the recipes files has user readable format:
  ; <TITLE>
  ; Introduction:
  ; <TEXT>
  ; Ingredients:"
  ; <TEXT>
  ; Method:
  ; <TEXT>
  ; Let's try to use it - first by saving it where possible, later to improve ranking algorithm
  {::rss/id       (.getName file)
   ::rss/raw-text (slurp file)})

(defn- initialize-database
  "Creates database (for simplicity as in memory map) with read and preprocessed recipes"
  [recipes-files]
  (reduce (fn [acc file]
            (assoc acc (.getName file) (-> file
                                           read-recipe
                                           pre/preprocess-recipe)))
          {}
          recipes-files))

(defn- create-index [db]
  "Creates map. Keys are words used in recipes, values are set of recipe-ids using given word"
  #_{"tomato" #{"recip1.txt" "recipe2.txt"}
     "soup"   #{"recipe1.txt"}}
  (reduce (fn [acc recipe]
            (let [id (::rss/id recipe)]
              (reduce (fn [acc word]
                        (assoc acc word (conj (get acc word #{}) id)))
                      acc
                      (::rss/text recipe))))
          {}
          (vals db)))

(defn- init-db
  "Initialize 'database' as map of :db and :words-index which is going to be used as :recipes-db later"
  []
  (let [recipes (->> "./resources/recipes"
                     clojure.java.io/file
                     file-seq
                     (filter #(.isFile %)))
        db (initialize-database recipes)]
    {:db          db                                        ; "Database containing slurped recipes"
     :words-index (create-index db)}))                      ; "Index of all words used in all recipes. Key is word, value is set of recipe-ids with this word"

(defn get-recipe [recipes-db id]
  (get (:db recipes-db) id))

(defn get-recipes-with-word [recipes-db word]
  (get (:words-index recipes-db) word))

;; Component

(defrecord RecipesDb []
  component/Lifecycle
  (start [component]
    (merge component (init-db)))
  (stop [component]
    (-> component
        (dissoc :db)
        (dissoc :words-index))))

(defn new-recipes-db []
  (map->RecipesDb {}))