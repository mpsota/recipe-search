(defproject recipe-search "0.1.0-SNAPSHOT"
  :description "Recipe-search tech test"
  :url "http://github.com/mpsota"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.danielsz/system "0.4.1"]
                 [clj-http "3.10.0"]
                 [cheshire "5.9.0"]                         ;; for :as :json in clj-http
                 [environ "1.1.0"]
                 [compojure "1.6.1"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring-middleware-format "0.7.3"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-cors "0.1.12"]
                 [ring/ring-mock "0.4.0"]                   ;; testing
                 [liberator "0.15.2"]
                 [clojure-stemmer "0.1.0"]
                 [snowball-stemmer "0.1.0"]]
  :plugins [[lein-environ "1.1.0"]
            [lein-figwheel "0.5.18"]]
  :main ^:skip-aot recipe-search.core
  :repl-options {:init-ns dev}
  :ring {:handler recipe-search.systems/api-handler}
  :profiles {:uberjar       {:aot :all}
             :dev           [:project/dev :profiles/dev]
             :test          [:project/test :profiles/test]
             :profiles/dev  {}
             :profiles/test {}
             :project/dev   {:aot :all
                             :env {:api-url      "http://127.0.0.1:8080"
                                   :http-port    "8080"
                                   :allow-origin ".*"
                                   }}
             :project/test  {:aot :all
                             :env {:api-url      "http://127.0.0.1:8080"
                                   :http-port    "8080"
                                   :allow-origin ".*"
                                   }}})
