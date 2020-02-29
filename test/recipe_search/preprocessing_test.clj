(ns recipe-search.preprocessing-test
  (:require [clojure.test :refer :all]))


(remove-stop-words ["I'm" "a" "chef"])

(remove-stop-words (downcase-text ["I'm" "a" "chef"]))
(remove-quotes (remove-stop-words (remove-interpunction-chars (str/split  "fo'()`’o,. it's" #"\s"))))
; (split-words "oranges;pepper or        salt&pepper;orange")


; (rank (get db "psb-squash-and-spelt-salad.txt") ["minut" "sherri" "balsam"])