(ns clj-consul-catalog.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.core.async :refer [timeout go-loop <! >!] :include-macros true]))



(defonce ^:private reject-repo (atom #{}))



(defn- with-ctx [path loc]
  (if (clojure.string/ends-with? path "/")
    (str path loc)
    (str path "/" loc)))



(defn- keyword->capital [x]
  (let [words (clojure.string/split (name x) #"-")
        capitalwords (map #(clojure.string/capitalize %) words)]
    (clojure.string/join capitalwords)))


(defn- transform [orig]
  ;(into {} (map (fn [[k v]] {(keyword->capital k) v}) orig))
  (clojure.walk/postwalk (fn [k] (if (keyword? k) (keyword->capital k) k))
                         orig))


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

(defn- with-hash [val inner]
  (hash (get-in val inner)))

(defn- function<-hash [f hsh]
  (f hsh @reject-repo))


(def ^:private remove-> (partial function<-hash remove))

(def ^:private ifsome (partial function<-hash some))


(defn register [path info]
  (let [hsh (with-hash info [:service :id])]
    (swap! reject-repo (fn [m] (remove-> #(= hsh %))))
    (go-loop []
      (<! (timeout 10000))
      (when (not (ifsome #(= hsh %)))
        (do (exec path "register" (with-http (transform info)))
            (recur))))))




(defn deregister [path info]
  (do
    (swap! reject-repo #(conj % (with-hash info [:service-id])))
    (exec path "deregister" (with-http (transform info)))))
