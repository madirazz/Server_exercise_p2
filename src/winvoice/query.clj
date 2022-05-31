(ns winvoice.query
  (:require [winvoice.database]
            [korma.core :refer :all]
            [clojure.java.jdbc]
            [clj-time.coerce]
            [clj-time.format]
            [clj-time.core]
            [clojure.string :as str]))

(declare aste)
(declare bids)
(declare buyers)
(declare fatture)
(declare sellers)
(declare debtors)
(declare cedenti)
(declare clienti)
(declare sys-user)
(declare debtorsinfo)
(declare operazioni)
(declare documents)

(defentity aste
  (pk :id)
  (table :aste)
  (belongs-to fatture {:fk :fatture_id}))


(defentity bids
  (pk :id)
  (table :bids)
  (belongs-to aste {:fk :aste_id})
  (belongs-to buyers {:fk :buyers_id}))

(defentity buyers
  (pk :id)
  (table :buyers))


(defentity fatture
  (pk :id)
  (table :fatture)
  (belongs-to clienti {:fk :clienti_id})
  (belongs-to sellers {:fk :sellers_id})
  (belongs-to cedenti {:fk :cedenti_id}))


(defentity sellers
  (pk :id)
  (table :sellers))


(defentity debtors
  (pk :id)
  (table :debtors))


(defentity cedenti
  (pk :id)
  (table :cedenti)
  (belongs-to sellers {:fk :sellers_id}))

(defentity clienti
  (pk :id)
  (table :clienti)
  (belongs-to debtors {:fk :debtorsinfo_id}))

(defentity sys-user
  (pk :id)
  (table :sys-user))


(defentity debtorsinfo
  (pk :id)
  (table :debtorsinfo)
  (belongs-to debtors {:fk :debtors_id})
  (belongs-to sellers {:fk :sellers_id})
  (belongs-to buyers {:fk :buyers_id}))


(defentity operazioni
  (pk :id)
  (table :operazioni)
  (belongs-to bids {:fk :bids_id}))


(defentity documents
  (pk :id)
  (table :documents))


;;Select all active sellers (stato=attivo) whose 'iva' starts with a given string
(defn get-active-sellers [iva]
  (select sellers
          (where (and {:stato "attivo"}
                      {:iva [like (str iva "%")]}))))

;;Select from buyer all buyers whose name starts with a given string
(defn get-buyers-starting-with [start]
  (select buyers
          (where {:nome [like (str start "%")]})))


;;Retrieve the ragione_sociale of all debtors of a given seller (id)
(defn get-debtors-rs [id]
      (select debtors
        (join debtorsinfo (= :debtors.id :debtorsinfo.debtors_id))
        (fields :debtors.ragione_sociale)
        (where {:debtorsinfo.sellers_id [= id]})))

;;Select all Fatture of a given seller (id) where due date is past 1 January 2022
(defn get-fatture-due [id]
  (select fatture
          (join cedenti  (= :cedenti.id :fatture.cedenti_id))
          (where (and {:cedenti.sellers_id [= id]}
                      {:fatture.data_scadenza [> (java.sql.Date/valueOf "2022-01-01")]}))
          (fields :fatture.id)))

;;select invoice amount and due date from aste where stato = "ANTICIPATA" 
;;and invoice amount lies within a given range of integer
(defn get-invoice-amount-anticipata [min-amount max-amount]
  (select fatture
          (join aste (= :fatture.id :aste.fatture_id))
          (where (and {:fatture.importo [> min-amount]}
                      {:fatture.importo [< max-amount]}
                      {:aste.stato "ANTICIPATA"}))
          (fields :fatture.importo :fatture.data_scadenza)))

