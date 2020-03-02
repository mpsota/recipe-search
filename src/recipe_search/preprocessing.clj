(ns recipe-search.preprocessing
  (:require [clojure.string :as str]
            [clojure-stemmer.porter.stemmer :as stemmer]
            [clojure.spec.alpha :as s]
            [recipe-search.spec :as rss]
            [taoensso.timbre :as log]))

(def stop-words "List of all english stop-words which we want to remove"
  (-> "./resources/stop-words.txt"
      slurp
      (str/split #"\s")))

(defn remove-stop-words [text]
  (remove (fn [word]
            (some #{word} stop-words))
          text))

(defn downcase-text [text]
  (map #(.toLowerCase %) text))

(defn stem-text [text]
  "USe stemming library to steam words"
  (map stemmer/stemming text))

(defn remove-interpunction-chars [text]
  (map #(str/replace % #"[,\.\:\(\)]" "") text))

(defn remove-quotes [text]
  (map #(str/replace % #"[`’']" "") text))

(defn preprocess-words [words]
  ;{:pre [(s/valid? ::rss/words words)]}
  (-> words
      downcase-text
      remove-interpunction-chars
      remove-stop-words                                     ;; remove stop words before quotes, as some stop words contain them (e.g 'it's')
      remove-quotes
      ((partial remove empty?))
      stem-text))

(defn split-words
  "Split text on spaces and few other special characters which are sometimes used without spaces around them"
  [text]
  ;{:pre [(s/valid? string? text)]}
  (str/split text #"[\s+\&\;]"))

(defn preprocess-text [text]
  "Split text into words and preprocess those words"
  (-> text
      split-words
      preprocess-words))

(defn preprocess-recipe
  "Split by lines raw-text of recipe. Keep preprocessed text in :text attribute, and also splited by category in :title, :introduction, :ingredients, :method where possible.
  We may want to use those additional fields for better rank functions, costs us nothing at this stage.
  Be aware that not all recipes are correctly structured, in that case we store just :text.
  :text-frequencies is frequency of words in the main text."
  [recipe]
  ;{:pre [(s/valid? ::rss/recipe recipe)]}
  (let [lines (str/split-lines (::rss/raw-text recipe))
        preprocessed-lines (map preprocess-text lines)
        preprocessed-text (reduce concat preprocessed-lines)
        [title _intro-text introduction _ingr-text ingredients _method-text method] preprocessed-lines]
    (cond
      (not= (.length lines) 7) (do
                                 (log/debug (::rss/id recipe) "recipe is not correctly structured.")
                                 (assoc recipe
                                   ::rss/text preprocessed-text
                                   ::rss/text-frequencies (frequencies preprocessed-text)))
      :else (assoc recipe
              ;;Fields not used for now, but maybe we should use them to improve our rank algorithms?
              ::rss/text preprocessed-text
              ::rss/text-frequencies (frequencies preprocessed-text)
              ::rss/title title
              ::rss/introduction introduction
              ::rss/ingredients ingredients
              ::rss/method method))))