(ns winvoice.database
  (:require [korma.db :as korma]
            [korma.core :refer :all]
            [clojure.string :as str]
            [clojure.java.jdbc]
            [clj-time.coerce]
            [clj-time.format]
            [clj-time.core]))

(def db-connection-info
  {:db "winvoice"
   :classname "org.postgresql.Driver"
   :host "localhost"
   :port "5432"
   :user "postgres"
   :password "admin"
   :subname "//localhost:5432/winvoice"
   :subprotocol "postgresql"
   :make-pool? true})

; set up korma
(korma/defdb db db-connection-info)

        








