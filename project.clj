(defproject recipe-search "0.1.0-SNAPSHOT"
  :description "Recipe-search tech test"
  :url "http://github.com/mpsota"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.danielsz/system "0.4.1"]
                 [me.raynes/fs "1.4.6"]
                 [clojure-stemmer "0.1.0"]
                 [snowball-stemmer "0.1.0"]]
  :repl-options {:init-ns recipe-search.core}
  :profiles {:uberjar       {:aot :all}
             :dev           [:project/dev :profiles/dev]
             :test          [:project/test :profiles/test]
             :profiles/dev  {}
             :profiles/test {}
             :project/dev   {:aot :all}
             :project/test  {:aot :all}})
