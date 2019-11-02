(ns customers.model
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql])
  (:import (java.util UUID)))

(def customers (atom {}))

(defn uuid []
  (.toString (UUID/randomUUID)))

(defn create-customer [data]
  (let [uuid (uuid)
        customer (merge {:id uuid} data)]
    (swap! customers merge {uuid customer})
    customer))

(defn create-customer' [db customer]
  (assoc customer :id (:CUSTOMER/ID (sql/insert! db :customer customer))))

(defn delete-customer [id]
  (swap! customers dissoc id))

(defn find-customer [id]
  (@customers id))

(defn find-customers [datasource]
  (tap> {:datasource (type datasource)})
  (or (vals @customers) []))

(defn update-customer [id data]
  (let [data (dissoc data :id)]
    (swap! customers assoc id (merge (@customers id) data))
    (@customers id)))
