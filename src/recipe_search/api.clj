(ns recipe-search.api
  (:require [compojure.core :refer [routes ANY defroutes GET PUT POST DELETE]]
            [compojure.route :as route]
            [liberator.core :refer [defresource log!]]
            [liberator.dev :refer [wrap-trace]]
            [recipe-search.search :as search]
            [recipe-search.spec :as rss]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]))

(s/def ::rank-by-option-api #{"rank-by-words-occurrences" "rank-by-ingredients-position"})

(defn- basic-error-handler [ctx]
  (.getMessage (:exception ctx)))

(defresource index-html
  :handle-exception basic-error-handler
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok "Use /index.html")

(defresource search [recipes-db query rank-by]
  :handle-exception basic-error-handler
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :initialize-context (fn [_]
                        (log! :trace rank-by)
                        {:words   (str/split query #"\s+")
                         :rank-by rank-by})
  :malformed? (fn [ctx] (or (empty? query)
                            (not (s/valid? ::rss/words (:words ctx)))
                            (not (s/valid? ::rank-by-option-api (:rank-by ctx)))
                            (empty? (:words ctx))))
  :handle-ok (fn [ctx]
               (let [words (:words ctx)
                     rank-by (keyword "recipe-search.spec" (:rank-by ctx))]
                 (search/search recipes-db words rank-by [::rss/id ::rss/raw-text]))))

(defn app-routes [{:keys [recipes-db]}]
  (routes
    (ANY "/" [] index-html)
    (GET "/api/search" [query rank-by] (search recipes-db query rank-by))
    (route/resources "/")
    (route/not-found "Page not found")))