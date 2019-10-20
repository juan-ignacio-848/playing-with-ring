(ns customers.handlers
  (:require [customers.model :as model]
    [ring.util.response :refer [response created]]))

(defn customer-id [req]
  (-> req
      :path-params
      :id))

(defn create-customer [req]
  (created "" (model/create-customer (:body req))))

(defn delete-customer [req]
  (model/delete-customer (customer-id req)))

(defn find-customer [req]
  (-> req
      customer-id
      model/find-customer
      response))

(defn find-customers [req]
  (response {:customers (model/find-customers)}))

(defn update-customer [req]
  (response (model/update-customer (customer-id req) (:body req))))