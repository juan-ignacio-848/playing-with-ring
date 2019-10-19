(ns customers.handlers
  (:import (java.util UUID))
  (:require [ring.util.response :refer [response]]))

(def customers (atom {}))

(defn uuid [] (.toString (UUID/randomUUID)))

(defn customer [body]
  (let [uuid (uuid)
        customer (merge {:id uuid} body)]
    (swap! customers merge {uuid customer})
    customer)
  )

(defn create-customer [req]
  {:status 201 :body (customer (:body req))})

(defn delete-customer [req]
  (let [id (:id (:path-params req))]
    (swap! customers dissoc id)))

(defn find-customer [req]
  (let [id (:id (:path-params req))]
    (response (@customers id))))

(defn find-customers [req]
  (let [customers (or (vals @customers) [])]
    (response {:customers customers})))

(defn update-customer [req]
  (let [body (dissoc (:body req) :id)
        id (:id (:path-params req))]
    (swap! customers assoc id (merge (@customers id) body))
    (response (@customers id))))