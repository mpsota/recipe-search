(ns recipe-search.api
  (:require [compojure.core :refer [routes ANY defroutes GET PUT POST DELETE]]
            [compojure.route :as route]
            [liberator.core :refer [defresource log!]]
            [liberator.dev :refer [wrap-trace]]
            [recipe-search.search :as search]
            [recipe-search.spec :as rss]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]))

(defn- basic-error-handler [ctx]
  (.getMessage (:exception ctx)))

(defresource index-html
  :handle-exception basic-error-handler
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok "Use /api/search endpoint.")

(defresource search [query]
  :handle-exception basic-error-handler
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :initialize-context (fn [_] {:words (str/split query #"\s+")})
  :malformed? (fn [ctx] (or (empty? query)
                            (not (s/valid? ::rss/words (:words ctx)))
                            (empty? (:words ctx))))
  :handle-ok (fn [ctx] (search/search (:words ctx) [::rss/id ::rss/raw-text]))
  )

(defn app-routes [_]
  (routes
    (ANY "/" [] index-html)
    (GET "/api/search" [query] (search query))
    (route/resources "/")
    (route/not-found "Page not found")))