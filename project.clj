(defproject as-magnet-extract "0.3.2"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.incubator "0.1.4"]
                 [com.climate/claypoole "1.1.4"]
                 [clj-http "3.7.0"]
                 [hickory "0.7.1"]
                 [com.cemerick/url "0.1.1"]
                 [ring/ring-codec "1.1.0"]
                 [clojure-term-colors "0.1.0-SNAPSHOT"]
                 ]
  :main ^:skip-aot as-magnet-extract.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
