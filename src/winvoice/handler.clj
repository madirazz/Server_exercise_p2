(ns winvoice.handler
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response wrap-json-body]]
            [ring.middleware.format-params :refer [wrap-restful-params]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [compojure.handler :as handler]
            [ring.util.response :refer [response header status]]
            [winvoice.query :as query]
            [org.httpkit.server :refer [run-server]]
            [validateur.validation :refer :all]
            [winvoice.utils :as utl]))


(defroutes app-routes
  ;;http://127.0.0.1:3000/api/winvoice/active-seller/080757700
  (GET "/api/winvoice/active-seller/:iva" [iva]
    (utl/build-response-map (query/get-active-sellers iva)))

  ;;http://127.0.0.1:3000/api/winvoice/buyers-starting-with/St
  (GET "/api/winvoice/buyers-starting-with/:start" [start]
    (utl/build-response-map (query/get-buyers-starting-with start)))

  ;;http://127.0.0.1:3000/api/winvoice/get-debtors-rs/SELLb2b7c9d9-28d0-4c4d-b8f5-e62d8846a3fd
  (GET "/api/winvoice/get-debtors-rs/:seller-id" [seller-id]
    (utl/build-response-map (query/get-debtors-rs seller-id)))

  ;;http://127.0.0.1:3000/api/winvoice/get-fatture-due/SELL1c2dd604-d3a1-43e2-bb66-66ee283e91f6
  (GET "/api/winvoice/get-fatture-due/:seller-id" [seller-id]
    (utl/build-response-map (query/get-fatture-due seller-id)))
  
  ;;http://127.0.0.1:3000/api/winvoice/get-invoice-amount-anticipata/1523549/2523549
  (GET "/api/winvoice/get-invoice-amount-anticipata/:min/:max" [min max]
    (utl/build-response-map  (query/get-invoice-amount-anticipata (Integer/parseInt min) (Integer/parseInt max))))

  ;;http://127.0.0.1:3000/api/winvoice/update-anticipo-richiesto
  ;;{"id": "FAT_e2d36a44-3837-4415-a18d-f30dbbcd0d3c", "percentage": "10000"}
  (PUT "/api/winvoice/update-anticipo-richiesto" [id percentage]
    (utl/build-response-map (if (empty? (utl/update-anticipo-richiesto-validation {:id id :percentage (Integer/parseInt percentage)}))
                          (query/update-anticipo-richiesto id (Integer/parseInt percentage))
                          (utl/update-anticipo-richiesto-validation {:id id :percentage (Integer/parseInt percentage)}))))
  
  ;;http://127.0.0.1:3000/api/winvoice/new-seller
  ;; {"ragione_sociale": "Raz", "iva": "", "iva2": ""}
  (POST "/api/winvoice/new-seller" [ragione_sociale iva iva2]
     (utl/build-response-map (if (empty? (utl/new-seller-validation {:ragione_sociale ragione_sociale :iva iva :iva2 iva2}))
                            (query/add-new-seller ragione_sociale iva iva2)
                            (utl/new-seller-validation {:ragione_sociale ragione_sociale :iva iva :iva2 iva2}))))
  
  (GET "/" [] "It is working :D")
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (-> (handler/api app-routes)
      (wrap-json-body)
      (wrap-json-params)
      (wrap-json-response)))


(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (run-server #'app {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))