(ns girlcelly-magnet-extract.core
  (:require [clojure.core.strint :refer [<<]]
            [clj-http.client :as client]
            [hickory.core :refer [parse as-hickory]]
            [hickory.select :refer [select find-in-text]])
  (:gen-class))

(defn -main [archive last-page & _]
  (doall
    (->> (-> last-page read-string (+ 1) range)
         (sequence (comp
                     (map (fn [page]
                            (<< "http://girlcelly.blog.fc2.com/~{archive}-~{page}.html")))
                     (map (fn [url]
                            (-> url client/get :body)))
                     (mapcat (fn [html]
                               (->> html parse as-hickory
                                    (select (find-in-text #"^magnet:.+")))))
                     (mapcat :content)
                     (filter string?)
                     (filter #(re-matches #"^magnet:.+" %))))
         (map println))))
