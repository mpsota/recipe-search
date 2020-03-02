(ns recipe-search.fixture
  (:require [clojure.test :refer :all]
            [system.repl :refer [set-init! start stop]]
            [clj-http.client :as client]
            [recipe-search.systems :refer [dev-system]]
            [recipe-search.database :as db]
            [taoensso.timbre :as log]))

(defn with-db-fixture [f]
  (log/info "Initializing database")
  (set-init! #'dev-system)
  (start)
  (f)
  (stop))
