(ns clj-consul-catalog.core-test
  (:require [clojure.test :refer :all]
            [clj-consul-catalog.core :refer :all]
            [clojure.core.async :refer [timeout <!!] :include-macros true]))

(defn- schema [id port]
  {:node    "DESKTOP-2RC0A0R"
   :address "127.0.0.1"
   :service {
             :id id
             :service "redis"
             :address "127.0.0.1"
             :port port}
   })

(def path "http://localhost:8500/v1/catalog/")

(def s (service path (schema "redis1" 8080) :interval 10))


(def s1 (service path (schema "redis2" 8081)))

(deftest test-1-register-request
  (testing "dovalid-register-request"
    (let [status (register s)]
      (is (= status true))
      (<!! (timeout 1000))
      (is (= (count (filter
                      #(= "redis1" (get % "ServiceID"))
                      (discover s)))  1)))
    (deregister s)
    (deregister s1)
    (<!! (timeout 1000))))




(deftest test-2-register-request
  (testing "dovalid-register-request"
    (register s)
    (register s1)
    (<!! (timeout 1000))
    (is (= (count (filter
                    #(= "redis1" (get % "ServiceID"))
                    (discover s1)))  1))

    (is (= (count (filter
                    #(= "redis2" (get % "ServiceID"))
                    (discover s1)))  1))

    (is (= (count (filter
                    #(or (= "redis1" (get % "ServiceID")) (= "redis2" (get % "ServiceID")))
                    (discover s1)))  2))

    ;wait for more than the default life time for consul to remove the service
    ;to ensure register beat has happen
    (<!! (timeout 65000))

    (is (= (count (filter
                    #(or (= "redis1" (get % "ServiceID")) (= "redis2" (get % "ServiceID")))
                    (discover s1)))  2))


    (deregister s)
    (deregister s1)
    (<!! (timeout 1000))
    ))


(deftest test-1-register-request
  (testing "dovalid-register-and-deregister"
    (register s)
    (<!! (timeout 1000))
    (is (= (count (filter
                    #(= "redis1" (get % "ServiceID"))
                    (discover s)))  1))



    (deregister s)



    (is (= (count (filter
                    #(= "redis1" (get % "ServiceID"))
                    (discover s)))  0))


    (register s)

    (is (= (count (filter
                    #(= "redis1" (get % "ServiceID"))
                    (discover s))) 1))))



