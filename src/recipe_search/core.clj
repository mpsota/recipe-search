(ns recipe-search.core
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [recipe-search.spec :as rss]
            [environ.core :refer [env]]
            [taoensso.timbre :as log]
            [clojure.set :as set]
            [recipe-search.systems :refer [dev-system]]
            [recipe-search.database :as db]
            [recipe-search.preprocessing :as pre]
            [recipe-search.ranking :as rank]
            [system.repl :refer [set-init! start system]]))

(defn -main [& args]
  (set-init! #'dev-system)
  (start)
  (log/info (format "API Server started at: %s:%s" (env :api-host) (env :api-port)))
  )