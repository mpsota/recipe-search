(ns recipe-search.preprocessing-test
  (:require [clojure.test :refer :all]
            [recipe-search.preprocessing :as pre]
            [recipe-search.spec :as rss]
            [recipe-search.fixture :as fixture]
            [clojure.string :as str]
            [recipe-search.database :as db]))

(use-fixtures :once fixture/with-db-fixture)

(deftest remove-stop-words-test
  (is (= ["I'm" "chef" "."]
         (pre/remove-stop-words ["I'm" "the" "chef" "."]))))

(deftest split-words
  (is (= ["1" "" "small" "red" "onion," "finely" "diced" "2" "tbsp" "red," "sherry" "or" "balsamic" "vinegar"]
         (pre/split-words "1; small red onion, finely diced 2 tbsp red, sherry or balsamic vinegar"))))

(deftest preprocess
  (is (= ["1" "small" "red" "onion" "fine" "dice" "2" "tbsp" "red" "sherri" "balsam" "vinegar"]
         (pre/preprocess-words ["1" "" "small" "red" "onion," "finely" "diced" "2" "tbsp" "red," "sherry" "or" "balsamic" "vinegar"])))
  (is (= ["f" "o" "o" "bar"]
         (pre/preprocess-text "f;o'()   `’o,. it's a bar"))))

(def recipe {::rss/id "psb-squash-and-spelt-salad.txt",
             ::rss/raw-text
                 "Purple sprouting broccoli, squash and spelt salad
                         Introduction:
                         A colourful vegetarian winter salad
                         Ingredients:
                         1 small red onion, finely diced 2 tbsp red, sherry or balsamic vinegar 1 tsp caster sugar 200g spelt grain 500g-600g (1 small) squash, deseeded & cut into thin wedges olive oil for roasting 250g purple sprouting broccoli 2 oranges 1 tbsp Dijon mustard 1 tbsp capers, soaked in water for 10 mins, drained & roughly chopped olive oil large handful pitted black olives large handful chopped parsley salt & pepper
                         Method:
                         Preheat oven to 190°C/Gas 5. Put the onion in a large bowl with the vinegar and sugar. Leave to macerate. In a pan of boiling water, cook the spelt for 35-40 minutes, until just tender. Drain and leave to cool. Put the squash on a baking tray and toss in just enough olive oil to coat. Roast for 30-40 minutes, until just tender and starting to colour. In a pan of boiling water, cook the purple sprouting broccoli for 3 minutes. Drain, refresh in cold water, then drain again. Peel the oranges and remove any pith. Cut in between the membrane either side of each segment and remove each orange segment. Squeeze any leftover juice into the bowl with the onion. Add the mustard, capers, 4 tablespoons of olive oil and season. Mix in the spelt, squash, purple sprouting broccoli, orange segments, olives and parsley. Check the seasoning and drizzle over a little extra olive oil to serve."})

(deftest preprocess-recipe
  "Simple test to just compare the most important part of recipe after preprocessing - frequencies"
  (is (= {"leav"   2 "cool" 1 "ingredi" 1 "drain" 4 "salad" 2 "peel" 1 "colour" 2 "mustard" 2 "3" 1 "soak" 1 "min" 1 "dice" 1 "introduct" 1 "4" 1 "season" 2 "sugar" 2
          "35-40"  1 "fine" 1 "500g-600g" 1 "250g" 1 "side" 1 "put" 2 "coat" 1 "pepper" 1 "juic" 1 "spelt" 4 "water" 4 "segment" 3 "toss" 1 "larg" 3 "dijon" 1 "cold" 1
          "method" 1 "bake" 1 "boil" 2 "roughli" 1 "vegetarian" 1 "tbsp" 3 "drizzl" 1 "either" 1 "grain" 1 "tablespoon" 1 "small" 2 "just" 3 "hand" 2 "vinegar" 2 "serv" 1
          "wedg"   1 "pan" 2 "tender" 2 "enough" 1 "refresh" 1 "roast" 2 "parslei" 2 "preheat" 1 "pit" 1 "30-40" 1 "5" 1 "trai" 1 "check" 1 "thin" 1 "deseed" 1 "start" 1
          "caster" 1 "winter" 1 "red" 2 "orang" 4 "190°c/ga" 1 "squeez" 1 "sprout" 4 "macer" 1 "salt" 1 "broccoli" 4 "black" 1 "littl" 1 "onion" 3 "1" 5 "bowl" 2 "purpl"
                   4 "oliv" 7 "oil" 5 "extra" 1 "chop" 2 "add" 1 "remov" 2 "membran" 1 "caper" 2 "mix" 1 "2" 2 "squash" 4 "pith" 1 "leftov" 1 "tsp" 1 "cut" 2 "balsam" 1 "10" 1
          "minut"  3 "sherri" 1 "oven" 1 "cook" 2 "200g" 1}
         (::rss/text-frequencies (pre/preprocess-recipe recipe)))))