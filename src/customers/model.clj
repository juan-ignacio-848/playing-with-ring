(ns customers.model
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as jdbc-sql]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all :as helpers]))

(defn create-customer [db customer]
  (assoc customer :id (:CUSTOMER/ID (jdbc-sql/insert! db :customer customer))))

(defn delete-customer [db id]
  (jdbc-sql/delete! db :customer {:id id}))

(defn find-customer [db id]
  (jdbc-sql/get-by-id db :customer id))

(defn find-customers [db]
  (jdbc-sql/query db ["select * from customer"]))

(defn update-customer [db customer]
  (jdbc/execute-one! db (-> (helpers/update :customer)
                            (sset {:first_name (:first_name customer)
                                   :last_name  (:last_name customer)})
                            (where [:= :id (:id customer)])
                            sql/format))
  customer)