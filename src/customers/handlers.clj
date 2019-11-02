(ns customers.handlers
  (:require [customers.model :as model]
            [ring.util.response :refer [response created]]))

(defn customer-id [req]
  (-> req
      :path-params
      :id))

(defn create-customer [req]
  (created "" (model/create-customer (:datasource req) (:body req))))

(defn delete-customer [req]
  (model/delete-customer (:datasource req) (get-in req [:path-params :id])))

(defn find-customer [req]
  (response (model/find-customer (:datasource req) (get-in req [:path-params :id]))))

(defn find-customers [req]
  (response {:customers (model/find-customers (:datasource req))}))

(defn update-customer [req]
  (tap> {:body (:body req)})
  (response (model/update-customer (:datasource req)
                                   (assoc (:body req) :id (get-in req [:path-params :id])))))