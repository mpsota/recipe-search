(ns recipe-search.core-test
  (:require [clojure.test :refer :all]
            [system.repl :refer [system]]
            [recipe-search.core :refer :all]
            [recipe-search.spec :as rss]
            [recipe-search.database :as db]
            [recipe-search.ranking :as rank]
            [recipe-search.preprocessing :as pre]
            [recipe-search.fixture :as fixture]))

(use-fixtures :once fixture/with-db-fixture)

(deftest rank-squash-and-spelt-salad
  (is (= 9
         (rank/rank (pre/preprocess-words ["olive" "salad"]) (db/get-recipe (:recipes-db system) "psb-squash-and-spelt-salad.txt")))))