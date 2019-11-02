(ns customers.model
  (:require [next.jdbc.sql :as sql]))

(defn create-customer [db customer]
  (assoc customer :id (:CUSTOMER/ID (sql/insert! db :customer customer))))

(defn delete-customer [db id]
  (sql/delete! db :customer {:id id}))

(defn find-customer [db id]
  (sql/get-by-id db :customer id))

(defn find-customers [db]
  (sql/query db ["select * from customer"]))

(defn update-customer [db customer]
  (sql/update! db
               :customer
               {:first_name (:first_name customer)
                :last_name  (:last_name customer)}
               {:id (:id customer)})
  customer)
