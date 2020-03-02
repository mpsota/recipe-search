(ns recipe-search.ui
  (:require [reagent.core :as r]
            ["semantic-ui-react" :refer [Grid Grid.Row Grid.Column Divider
                                         Header Header.Content
                                         Button Form Form.Input Form.Field
                                         Table Table.Body Table.Header Table.HeaderCell Table.Cell Table.Row]]
            [ajax.core :as ajax]
            [clojure.string :as string]))

(defonce query (r/atom "Tomatoes Ham Olives"))              ; Keeps current query string
(defonce results (r/atom []))                               ; Keeps query results returned by API

(defn search-form
  "Search field with search button"
  []
  [:> Form
   [:> Form.Field
    [:label "Search"]
    [:> Form.Input {:placeholder @query
                    :onChange    (fn [e]
                                   (reset! query (.-value (.-target e))))}]
    [:> Button {:onClick (fn [e]
                           (ajax/GET "http://localhost:3001/api/search"
                                     {:params          {:query @query}
                                      :handler         #(reset! results %)
                                      :response-format :json
                                      :keywords?       true}
                                     ))
                }
     "Search!"]]])

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
