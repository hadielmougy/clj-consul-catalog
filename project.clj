(defproject clj-consul-catalog "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                [http-kit "2.2.0"]
                [org.clojure/data.json "0.2.6"]]

  :plugins [[cider/cider-nrepl "0.8.1"]]
  :main ^:skip-aot clj-consul-catalog.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
