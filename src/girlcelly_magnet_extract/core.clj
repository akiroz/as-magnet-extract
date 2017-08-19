(ns girlcelly-magnet-extract.core
  (:require [clojure.core.strint :refer [<<]]
            [clj-http.client :as client]
            [hickory.core :refer [parse as-hickory]]
            [hickory.select :refer [select find-in-text]])
  (:gen-class))

(defn -main [archive & _]
  (doall
    (->> (range)
         (sequence
           (comp (map         (fn [page]
                                (binding [*out* *err*]
                                  (print (<< "Requesting page ~{page}... "))
                                  (flush))
                                (<< "http://girlcelly.blog.fc2.com/~{archive}-~{page}.html")))
                 (map         (fn [url]
                                (-> url client/get :body)))
                 (map         (fn [html]
                                (->> html parse as-hickory
                                     (select (find-in-text #"^magnet:.+")))))
                 (map         (fn [elements]
                                (->> elements
                                     (sequence
                                       (comp (mapcat :content)
                                             (filter string?)
                                             (filter #(re-matches #"^magnet:.+" %)))))))
                 (take-while  (fn [magnet-urls]
                                (binding [*out* *err*]
                                  (println (<< "found ~{(count magnet-urls)} URLs.")))
                                (> (count magnet-urls) 0)))
                 (mapcat identity) ;; flatten
                 (map println)
                 )))))


