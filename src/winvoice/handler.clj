(ns winvoice.handler
  (:require [compojure.core :refer :all]
            [clojure.string :as str]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :refer [redirect response]]
            [winvoice.query :as query]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]))


(defroutes app-routes
  ;;http://127.0.0.1:3000/api/winvoice/active-seller/
  (GET "/api/winvoice/active-seller/:iva" [iva]
    (response (query/get-active-sellers iva)))
  
  ;;http://127.0.0.1:3000/api/winvoice/buyers-starting-with/
  (GET "/api/winvoice/buyers-starting-with/:start" [start] 
    (response (query/get-buyers-starting-with start)))
  
  ;;http://127.0.0.1:3000/api/winvoice/get-debtors-rs/SELLb2b7c9d9-28d0-4c4d-b8f5-e62d8846a3fd
  (GET "/api/winvoice/get-debtors-rs/:seller-id" [seller-id] 
    (response (query/get-debtors-rs seller-id)))
  
  ;;http://127.0.0.1:3000/api/winvoice/get-fatture-due/SELL1c2dd604-d3a1-43e2-bb66-66ee283e91f6
  (GET "/api/winvoice/get-fatture-due/:seller-id" [seller-id]
    (response (query/get-fatture-due seller-id)))
  
  ;;http://127.0.0.1:3000/api/winvoice/get-invoice-amount-anticipata/1523549/2523549
  (GET "/api/winvoice/get-invoice-amount-anticipata/:min/:max" [min max]
    (let [num1 (read-string min) num2 (read-string max)]
      (response (query/get-invoice-amount-anticipata num1 num2))))
  
  
  (GET "/" [] "hello")
  
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (wrap-json-params)
      (wrap-json-response)))

(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))