(ns clj-consul-catalog.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))


(defn- merge-defaults [src inf]
  (json/write-str (merge src inf)))

(defn- with-ctx [path loc]
  (if (clojure.string/ends-with? path "/")
    (str path loc)
    (str path "/" loc)))


(defn- deref-with-default [ref]
  (deref ref 2000 false))

(defn- with-http
  ([]
   (fn [x] (http/get x)))
  ([b]
   (fn [x] (http/put x {:body (json/write-str b)}))))


(defn- exec [path loc m]
  (when-let [resp (-> path
                      (with-ctx loc)
                      m
                      deref-with-default)]
    (let [body (:body resp)]
      (when-not (empty? body)
        (json/read-str body)))))


(defn datacenters [path]
  (exec path "datacenters" (with-http)))


(defn nodes [path]
  (exec path "nodes" (with-http)))

(defn services [path]
  (exec path "services" (with-http)))


(defn service [path name]
  (exec path (str "services/" name) (with-http)))


(defn- register-request [{:keys [node addr service-name service-id service-addr service-port]}]
  {"Node" node
   "Address" addr
   "Service" {
      "ID"  service-id
      "Service"  service-name
      "Address"  service-addr
      "Port"  service-port
    }})


(defn register [path info]
  (exec path "register" (with-http (register-request info))))



(defn- deregister-request [{:keys [datacenter node service-id]}]
  {"Datacenter" datacenter
   "Node" node
   "ServiceID" service-id})


(defn deregister [path info]
  (exec path "deregister" (with-http (deregister-request info))))


(defn node []
  )

(defn watch []
  )
