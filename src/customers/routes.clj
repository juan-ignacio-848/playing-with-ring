(ns customers.routes
  (:require [customers.handlers :as handlers]))

(def routes [["/customers" {:get  #'handlers/find-customers
                            :post #'handlers/create-customer}]
             ["/customers/:id" {:get    #'handlers/find-customer
                                :delete #'handlers/delete-customer
                                :patch  #'handlers/update-customer
                                :put    #'handlers/update-customer}]
             ])