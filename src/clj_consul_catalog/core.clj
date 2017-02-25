(ns clj-consul-catalog.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.core.async :refer [timeout go-loop <! >!] :include-macros true]))



(defonce ^:private reject-repo (atom #{}))

(defprotocol Service
  (discover [this])
  (register [this])
  (deregister [this]))


(defn- with-ctx [path loc]
  (if (clojure.string/ends-with? path "/")
    (str path loc)
    (str path "/" loc)))



(defn- keyword->capital [x]
  (let [words (clojure.string/split (name x) #"-")
        capitalwords (map #(clojure.string/capitalize %) words)]
    (clojure.string/join capitalwords)))


(defn- transform [orig]
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



(defn- with-hash [val inner]
  (hash (get-in val inner)))

(defn- apply> [f hsh]
  (f hsh @reject-repo))


(def ^:private remove-> (partial apply> remove))

(def ^:private ifsome? (partial apply> some))



(defn service [path info & ops]


  (reify Service

    (discover [this]
      (exec path (str "service/" (-> info :service :service)) (with-http)))


    (register [this]
      (let [hsh (with-hash info [:service :id])
            {:keys [interval] :or {interval 10}} ops
            beat (fn [](exec path "register" (with-http (transform info))))
            added (beat)]
        (swap! reject-repo (fn [_] (remove-> #(= hsh %))))
        (when added (go-loop []
                      (<! (timeout (* interval 1000)))
                      (when (not (ifsome? #(= hsh %)))
                        (beat)
                        (recur))))
        added))


    (deregister [this]
      (swap! reject-repo #(conj % (with-hash info [:service-id])))
      (exec path "deregister" (with-http (transform info))))))
