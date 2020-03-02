(ns recipe-search.database
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [recipe-search.spec :as rss]
            [clojure.set :as set]
            [recipe-search.preprocessing :as pre]
            [recipe-search.ranking :as rank]
            [com.stuartsierra.component :as component]))


#_(def db "Database containg slurped recipes" (atom {}))
#_(def words-index "Index of all words used in all recipes. Key is word, value is set of recipe-ids with this word" (atom {}))
#_(def recipes (atom {}))

(defn get-recipe [recipes-db id]
  (get (:db recipes-db) id))

(defn get-recipes-with-word [recipes-db word]
  (get (:words-index recipes-db) word))

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
  {::rss/id       (.getName file)
   ::rss/raw-text (slurp file)})

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
            (let [id (::rss/id recipe)]
              (reduce (fn [acc word]
                        ;(print acc word)
                        (assoc acc word (conj (get acc word #{}) id)))
                      acc
                      (::rss/text recipe))))
          {}
          (vals db)))

(defn init-db
  "Initialize 'database'"
  []
  (let [recipes (->> "./resources/recipes"
                     clojure.java.io/file
                     file-seq
                     (filter #(.isFile %)))
        db (initialize-database recipes)]
    {:db          db
     :words-index (create-index db)}))

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