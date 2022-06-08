(ns winvoice.utils
  (:require [validateur.validation :as v]
            [clojure.algo.generic.functor :refer [fmap]]
            [compojure.core :refer :all]
            [clojure.data.json :as json]
            [ring.util.response :refer [ response header status]]
            ))

;;iva (fiscal code) sequence of integers of length between 10 and 12 or Italian fiscal code

;;iva2 (vat code) sequence of integers of length between 10 and 12

(defn new-seller-validation
  [params]
  (let [v-set (v/validation-set
               (v/presence-of #{:ragione_sociale :iva :iva2}
                              :message "The fields ragione_sociale, iva and iva2 cannot be blank")
               
               (v/format-of :iva
                            :format #"^[0-9]{10,12}$|^([A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST]{1}[0-9LMNPQRSTUV]{2}[A-Z]{1}[0-9LMNPQRSTUV]{3}[A-Z]{1})$|([0-9]{11})$"
                            :message "Please insert a valid value. Either it is of length 10 to 12 or Italian fiscal code format")
               
               (v/format-of :iva2 
                            :format #"^([0-9]{10,12})$"
                            :message "Must be of length 10 to 12"))]
    (fmap vec (v-set params))))

;;percentage, it's a number range of valeus permitted vary on  [1, 100]
(defn update-anticipo-richiesto-validation
  [params]
  (let [v-set (v/validation-set
               (v/presence-of #{:id :percentage}
                              :message "The fields id and percentage cannot be blank")
               (v/numericality-of :percentage 
                                  :only-integer true 
                                  :gte 100 
                                  :lte 10000
                                  :message "Must be an integer between 100 and 10000"))]
    (fmap vec (v-set params))))


(defn build-response-map [body]
  (-> (response (json/write-str body))
      (header "Content-Type" "text/json")))