(ns recipe-search.preprocessing
  (:require [clojure.string :as str]
            [clojure-stemmer.porter.stemmer :as stemmer]))

(def stop-words "List of all english stop-words which we want to remove"
  (-> "./resources/stop-words.txt"
                    slurp
                    (str/split #"\s")))

(defn downcase-text [text]
  (map #(.toLowerCase %) text))

(defn stem-text [text]
  (map stemmer/stemming text))

(defn remove-stop-words [text]
  (remove (fn [word]
            (some #{word} stop-words))
          text))

(defn remove-interpunction-chars [text]
  (map #(str/replace % #"[,\.\:\(\)]" "") text))

(defn remove-quotes [text]
  (map #(str/replace % #"[`’']" "") text))

(defn preprocess-words [words]
  (-> words downcase-text
      remove-interpunction-chars
      remove-stop-words ;; remove stop words before quotes, as some stop words contain them (e.g 'it's')
      remove-quotes
      ((partial remove empty?))
      stem-text))

(defn split-words
  "Split text on spaces and few other special characters which are sometimes used without spaces around them"
  [text]
  (str/split text #"[\s+\&\;]"))

(defn preprocess-text [text]
  (-> text
      split-words
      preprocess-words))

(defn preprocess-recipe
  "Split by lines raw-text of recipe. Keep preprocessed text in :text attribute, and also splited by category in :title, :introduction, :ingredients, :method.
  We may want to use it for better rank functions, costs us nothing at this stage.
  Be aware that not all recipes are correctly structured, in that case we store just :text.
  :text-frequencies is frequency of words in the main text."
  [recipe]
  (let [lines (str/split-lines (:raw-text recipe))
        preprocessed-lines  (map preprocess-text lines)
        preprocessed-text (reduce concat preprocessed-lines)
        [title _intro-text introduction _ingr-text ingredients method-text method] preprocessed-lines]
    (cond
      (not= (.length lines) 7) (do
                                 (print (format "%s not correctly structured\n" (:id recipe)))
                                 (assoc recipe
                                   :text preprocessed-text
                                   :text-frequencies (frequencies preprocessed-text)))
      :else (assoc recipe
              ;;Fields not used for now, but maybe we should use them to improve our rank algorithms?
              :text preprocessed-text
              :text-frequencies (frequencies preprocessed-text)
              :title title
              :introduction introduction
              :ingredients ingredients
              :method method))))