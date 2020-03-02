(ns recipe-search.client-test
  (:require [clojure.test :refer :all]
            [recipe-search.fixture :as fixture]
            [environ.core :refer [env]]
            [clj-http.client :as client]
            [clojure.data.json :as json]))


; localhost:3001/api/search?query=tomato   olives lamb rosemary spinach
; localhost:3001/api/search?query=


(use-fixtures :once fixture/with-db-fixture)

(deftest api-test
  (let [search-url (format "%s:%s/%s" (env :api-host) (env :api-port) "api/search")]
    (testing "Correct Query"
      (is (= "mexican-beef-steak-quesadillas.txt"
             (-> (client/get search-url {:query-params {:query "tomatoes lime beef steaks mexican"}
                                         :accept       :json})
                 (:body)
                 (json/read-str :key-fn keyword)
                 first
                 (:id)))))
    (testing "Empty Query"
      (is (= 400
             (-> (client/get search-url {:query-params     {:query ""}
                                         :accept           :json
                                         :throw-exceptions false})
                 (:status)))))))
