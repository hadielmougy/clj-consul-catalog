(defproject clj-consul-catalog "0.2.1-SNAPSHOT"
  :description "Consul service registry client for clojure"
  :url "https://github.com/hadielmougy/clj-consul-catalog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                [http-kit "2.2.0"]
                [org.clojure/data.json "0.2.6"]
                [org.clojure/core.async "0.2.395"]]

  :plugins [[cider/cider-nrepl "0.8.1"]]
  :main ^:skip-aot clj-consul-catalog.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
