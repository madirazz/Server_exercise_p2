(ns winvoice.handler
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response wrap-json-body]]
            [ring.middleware.format-params :refer [wrap-restful-params]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [compojure.handler :as handler]
            [ring.util.response :refer [redirect response]]
            [winvoice.query :as query]
            [org.httpkit.server :refer [run-server]]
;;[ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            ))


(defroutes app-routes
  ;;http://127.0.0.1:3000/api/winvoice/active-seller/022512
  (GET "/api/winvoice/active-seller/:iva" [iva]
    {:status 200
     :header {"Content-Type" "text/json"}
     :body (json/write-str (query/get-active-sellers iva))})

  ;;http://127.0.0.1:3000/api/winvoice/buyers-starting-with/
  (GET "/api/winvoice/buyers-starting-with/:start" [start]
  {:status 200
     :header {"Content-Type" "text/json"}
     :body (json/write-str (query/get-buyers-starting-with start))})

  ;;http://127.0.0.1:3000/api/winvoice/get-debtors-rs/SELLb2b7c9d9-28d0-4c4d-b8f5-e62d8846a3fd
  (GET "/api/winvoice/get-debtors-rs/:seller-id" [seller-id]
    {:status 200
     :header {"Content-Type" "text/json"}
     :body (json/write-str (query/get-debtors-rs seller-id))})

  ;;http://127.0.0.1:3000/api/winvoice/get-fatture-due/SELL1c2dd604-d3a1-43e2-bb66-66ee283e91f6
  (GET "/api/winvoice/get-fatture-due/:seller-id" [seller-id]
   {:status 200
     :header {"Content-Type" "text/json"}
     :body (json/write-str (query/get-fatture-due seller-id))})

  ;;http://127.0.0.1:3000/api/winvoice/get-invoice-amount-anticipata/1523549/2523549
  (GET "/api/winvoice/get-invoice-amount-anticipata/:min/:max" [min max]
    {:status 200
     :header {"Content-Type" "text/json"}
     :body (json/write-str (let [num1 (read-string min) num2 (read-string max)]
      (query/get-invoice-amount-anticipata num1 num2)))})

  ;;http://127.0.0.1:3000/api/winvoice/update-anticipo-richiesto
  ;;{"id": "FAT_e2d36a44-3837-4415-a18d-f30dbbcd0d3c", "percentage": "10000"}
  (PUT "/api/winvoice/update-anticipo-richiesto" [id percentage]
    {:status 200
     :header {"Content-Type" "text/json"}
     :body   (json/write-str (let [num1 id
                                   num2 (read-string percentage)] (query/update-anticipo-richiesto num1 num2)))})

  ;;http://127.0.0.1:3000/api/winvoice/new-seller
  ;; {"ragionale-sociale": "100", "iva": "101", "iva2": "103"}
  (POST "/api/winvoice/new-seller" [ragionale-sociale iva iva2]
    {:status 200
     :header {"Content-Type" "text/json"}
     :body (json/write-str (let [num1 (read-string ragionale-sociale)
                                 num2 (read-string iva)
                                 num3 (read-string iva2)] (query/add-new-seller num1 num2 num3)))})
  (GET "/" [] "hello")
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