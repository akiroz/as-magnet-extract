(defproject girlcelly-magnet-extract "0.2.0"
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.incubator "0.1.4"]
                 [clj-http "3.6.1"]
                 [hickory "0.7.1"]]
  :main ^:skip-aot girlcelly-magnet-extract.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
