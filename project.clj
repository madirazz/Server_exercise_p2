(defproject winvoice "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.postgresql/postgresql "42.3.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [clj-time "0.15.2"]
                 [korma "0.5.0-RC1" :exclusions [org.clojure/java.jdbc]]
                 [ring-server "0.5.0"]
                 [ring/ring-defaults "0.3.3"]
                 [compojure "1.6.2" :exclusions [ring/ring-core]]
                 [http-kit "2.5.3"]
                 [ring/ring-json "0.5.1"]
                 [ring-middleware-format "0.7.5"]]
  
  :main winvoice.handler
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler winvoice.handler/app}
  :profiles
  {:dev {:dependencies [[org.clojure/tools.namespace "1.2.0"]
                        [ring/ring-mock "0.3.2"]]}})


