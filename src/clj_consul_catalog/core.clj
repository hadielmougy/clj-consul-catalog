(ns clj-consul-catalog.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))


(defn- merge-defaults [src inf]
  (json/write-str (merge src inf)))

(defn- with-ctx [path loc]
  (if (clojure.string/ends-with? path "/")
    (str path loc)
    (str path "/" loc)))



(defn- keyword->capital [x]
  (let [words (clojure.string/split x #"-")
        capitalwords (map #(clojure.string/capitalize (name %)) words)]
    (clojure.string/join capitalwords)))


(defn- transform [orig]
  (into {} (map (fn [[k v]] {(keyword->capital (name k)) v}) orig)))


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
  "{:node \"DESKTOP-2RC0A0R\"
  :addr \"127.0.0.1\"
  :service-id  \"redis1\"
  :service-name  \"redis\"
  :service-addr  \"127.0.0.1\"
  :service-port  8000}"
  (exec path "register" (with-http (register-request info))))




(defn deregister [path info]
  "{:datacenter \"dc1\"
    :node \"DESKTOP-2RC0A0R\"
    :service-id \"redis1\"}
    "
  (exec path "deregister" (with-http (transform info))))
