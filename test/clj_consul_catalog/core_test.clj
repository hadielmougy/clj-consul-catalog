(ns clj-consul-catalog.core-test
  (:require [clojure.test :refer :all]
            [clj-consul-catalog.core :refer :all]
            [clojure.core.async :refer [timeout <!!] :include-macros true]))

(defn- dovalid-register-request [id port]
  (register "http://localhost:8500/v1/catalog/"
            {:node    "DESKTOP-2RC0A0R"
             :address "127.0.0.1"
             :service {
                       :id id
                       :service "redis"
                       :address "127.0.0.1"
                       :port port}
             }))

(deftest test-1-register-request
  (testing "dovalid-register-request"
    (let [rs (dovalid-register-request "redis1" 8000)]
      (is (= rs true))
      (<!! (timeout 1000))
      (is (= (count (filter
                      #(= "redis1" (get % "ServiceID"))
                      (service "http://localhost:8500/v1/catalog/" "redis")))  1)))))



(deftest test-2-register-request
  (testing "dovalid-register-request"
    (dovalid-register-request "redis1" 8000)
    (dovalid-register-request "redis2" 8005)
    (<!! (timeout 1000))
    (is (= (count (filter
                    #(= "redis1" (get % "ServiceID"))
                    (service "http://localhost:8500/v1/catalog/" "redis")))  1))

    (is (= (count (filter
                    #(= "redis2" (get % "ServiceID"))
                    (service "http://localhost:8500/v1/catalog/" "redis")))  1))

    (is (= (count (filter
                    #(or (= "redis1" (get % "ServiceID")) (= "redis2" (get % "ServiceID")))
                    (service "http://localhost:8500/v1/catalog/" "redis")))  2))

    ;wait for more than the default life time for consul to remove the service
    ;to ensure register beat has happen
    (<!! (timeout 65000))

    (is (= (count (filter
                    #(or (= "redis1" (get % "ServiceID")) (= "redis2" (get % "ServiceID")))
                    (service "http://localhost:8500/v1/catalog/" "redis")))  2))

   ))



(deftest test-1-register-request
  (testing "dovalid-register-and-deregister"
    (let [rs (dovalid-register-request "redis1" 8000)]
      (<!! (timeout 1000))
      (is (= (count (filter
                      #(= "redis1" (get % "ServiceID"))
                      (service "http://localhost:8500/v1/catalog/" "redis")))  1))



      (deregister "http://localhost:8500/v1/catalog/"
                  {:datacenter "dc1"
                   :node "DESKTOP-2RC0A0R"
                   :service-id "redis1"})


      (is (= (count (filter
                      #(= "redis1" (get % "ServiceID"))
                      (service "http://localhost:8500/v1/catalog/" "redis")))  0))


      (dovalid-register-request "redis1" 8000)

      (is (= (count (filter
                      #(= "redis1" (get % "ServiceID"))
                      (service "http://localhost:8500/v1/catalog/" "redis"))) 1))
      )))