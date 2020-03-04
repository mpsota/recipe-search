(ns recipe-search.ui
  (:require [reagent.core :as r]
            ["semantic-ui-react" :refer [Grid Grid.Row Grid.Column Divider
                                         Header Header.Content
                                         Button Form Form.Input Form.Field Form.Dropdown
                                         Table Table.Body Table.Header Table.HeaderCell Table.Cell Table.Row]]
            [ajax.core :as ajax]
            [clojure.string :as string]))

(defonce query (r/atom "Tomatoes Ham Olives"))              ; Keeps current query string
(defonce results (r/atom []))                               ; Keeps query results returned by API
(defonce rank-by-option (r/atom nil))

(defn search-form
  "Search field with search button and dropdown with available rank functions"
  []
  (let [rank-by-options [{:key   "rank-by-words-occurrences" ;; For now just hardcode the options, no endpoint to return them
                          :value "rank-by-words-occurrences"
                          :text  "rank-by-words-occurrences"
                          }
                         {:key   "rank-by-ingredients-position"
                          :value "rank-by-ingredients-position"
                          :text  "rank-by-ingredients-position"
                          }]]
    [:> Form
     [:> Form.Field
      [:label "Search"]
      [:> Form.Input {:placeholder @query
                      :onChange    (fn [_e v]
                                     (reset! query (.-value v)))}]
      [:> Form.Dropdown {:search    true
                         :fluid     true
                         :selection true
                         :options   rank-by-options
                         :onChange  (fn [_e v]
                                      (reset! rank-by-option (.-value v)))}]
      [:> Button {:onClick (fn [e]
                             (ajax/GET "http://localhost:3001/api/search"
                                       {:params          {:query   @query
                                                          :rank-by @rank-by-option}
                                        :handler         #(reset! results %)
                                        :response-format :json
                                        :keywords?       true}
                                       ))
                  }
       "Search!"]]]))

(defn results-table
  "Component to display query results - table with 2 columns"
  []
  [:> Table {:celled true}
   [:> Table.Header
    [:> Table.Row
     [:> Table.HeaderCell {} "Recipe ID"]
     [:> Table.HeaderCell {} "Raw Text"]]]
   [:> Table.Body
    (map (fn [result]
           ^{:key (:id result)}
           [:> Table.Row
            [:> Table.Cell (:id result)]
            [:> Table.Cell [:p (->> (string/split (:raw-text result) "\n")
                                    (interpose [:br]))]]])
         @results)]])

(defn main-page
  "Main page - Grid with 3 columns. Title at the top, Search component at the in the middle row, results component at the bottom."
  []
  [:> Grid {:columns 3
            :style   {:width      "75%"
                      :text-align "center"
                      :margin     "auto"}}
   [:> Grid.Row]
   [:> Grid.Row
    [:> Grid.Column]
    [:> Grid.Column
     [:> Header {:as "h1"} "Recipe search"]]
    [:> Grid.Column]]
   [:> Grid.Row {:columns 1}
    [:> Grid.Column
     [:> Divider]]]
   [:> Grid.Row
    [:> Grid.Column]
    [:> Grid.Column
     [search-form]
     ]
    [:> Grid.Column]
    ]
   [:> Grid.Row {:columns 1}
    [:> Grid.Column]
    [:> Grid.Column
     [results-table]]
    [:> Grid.Column]
    ]])
