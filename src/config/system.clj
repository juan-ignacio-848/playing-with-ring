(ns config.system
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [customers.routes :as customers]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [org.httpkit.server :as httpkit]
            [reitit.ring :as ring]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]))

(defmethod aero/reader 'ig/ref [_ _ value]
  (ig/ref value))

(defn read-config []
  (aero/read-config (io/resource "system.edn")))

(extend-protocol reitit.core/Expand
  clojure.lang.Var
  (expand [this _]
    {:handler this}))

(def routes [[customers/routes]])

(defmethod ig/init-key :router [_ config]
  (ring/router [routes]))

(defn add-db-to-request [handler {:keys [datasource]}]
  (fn [req]
    (handler (assoc req :datasource datasource))))

(defmethod ig/init-key :ring-handler [_ config]
  (-> (:router config)
      (ring/ring-handler (ring/create-default-handler
                           {:not-found (constantly {:status 404 :body "Not found"})}))
      wrap-params
      (wrap-json-response)
      (wrap-json-body {:keywords? true})
      (add-db-to-request (:database config))
      ))

(defmethod ig/init-key :http-kit [_ config]
  (assoc config
    :stop-server
    (httpkit/run-server (:handler config) config)))

(defmethod ig/halt-key! :http-kit [_ {stop-server :stop-server}]
  (stop-server))

(defn- create-tables
  "Called at application startup. Attemps to create the
  database tables if they don't exist."
  [db]
  (let [auto-key (str "generated always as identity"
                      " (start with 1, increment by 1)"
                      " primary key")]
    (try
      (jdbc/execute-one! db
                         [(str "create table customer (
                         id integer " auto-key ",
                         first_name varchar(32),
                         last_name  varchar(32)")])
      (catch Exception e
        (println "Exception: " (ex-message e))
        (println "Looks like the database is already setup?")))))

(derive :jdbc/h2 :jdbc/db)

(defmethod ig/init-key :jdbc/db [_ db]
  (let [datasource (jdbc/get-datasource db)]
    (create-tables datasource)
    (assoc db :datasource datasource)))

(defmethod ig/halt-key! :jdbc/db [_ db]
  (assoc db :datasource nil))