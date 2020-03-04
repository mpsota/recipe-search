(ns recipe-search.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::word string?)
(s/def ::words (s/* ::word))

(s/def ::recipe (s/keys :req [::id ::raw-text ::text]
                        :opt-un []))
(s/def ::recipes (s/* ::recipe))

(s/def ::rank-by-option #{::rank-by-words-occurrences ::rank-by-ingredients-position})
