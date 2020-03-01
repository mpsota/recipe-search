(ns recipe-search.fixture
  (:require [clojure.test :refer :all]
            [recipe-search.database :as db]))

(defn with-db-fixture [f]
  (format "Inicjalizing database")
  (db/init)
  (f))
