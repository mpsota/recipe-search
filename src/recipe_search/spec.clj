(ns recipe-search.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::word string?)
(s/def ::words (s/* ::word))

(s/def ::recipe (s/keys :req [::id ::raw-text ::text]))

