(ns as-magnet-extract.core
  (:gen-class)
  (:require
    [clojure.string :as string]
    [clojure.walk :refer [keywordize-keys]]
    [clojure.edn :as edn]
    [clojure.core.strint :refer [<<]]
    [com.climate.claypoole :as cp]
    [cemerick.url :refer [url] :rename {url URL}]
    [ring.util.codec :refer [form-decode]]
    [clj-http.client :as http]
    [hickory.core :refer [parse as-hickory]]
    [hickory.select :as s]
    [clojure.term.colors :as colors]
    ))

(def api-host
  "http://www.anime-sharing.com")

(def net-pool
  (cp/threadpool 16))

(defn eprintln [s]
  (binding [*out* *err*] (println s)))

(defn search [params]
  (->> (-> (http/post (-> api-host
                          (URL "forum/search.php")
                          (assoc :query {:do "process"})
                          str)
                      {:redirect-strategy  :none
                       :multi-param-style  :array
                       :form-params        params})
           (get-in [:headers "Location"]))
       (re-find #"[0-9]+$")))

(defn get-pages [search-id]
  (let [page-1  (-> (str (URL api-host "forum/search.php"))
                    (http/get {:query-params {:searchid search-id}})
                    :body parse as-hickory)
        pages   (->> page-1
                     (s/select (s/id "pagination_top"))
                     first
                     (s/select (s/nth-of-type 1 :a))
                     first :content first string/trim
                     (re-find #"[0-9]+$")
                     Integer/parseInt)
        threads (->> page-1
                     (s/select (s/id "postpagestats"))
                     first :content first string/trim
                     (re-find #"[0-9]+$")
                     Integer/parseInt)
        ]
    (eprintln (<< "Found ~{threads} threads in ~{pages} pages."))
    (lazy-cat ;; nyan~
      [page-1]
      (cp/upfor net-pool
                [page-no (range 2 (inc pages))]
                (-> (str (URL api-host "forum/search.php"))
                    (http/get {:query-params {:searchid search-id
                                              :page     page-no}})
                    :body parse as-hickory)))))

(defn get-links [result-page]
  (cp/upfor net-pool
            [thread-path (->> result-page
                              (s/select (s/id "searchbits"))
                              first
                              (s/select (s/class "title"))
                              (map #(get-in % [:attrs :href])))]
            (let [thread-body (->> (URL api-host "forum" thread-path)
                                   str http/get :body parse as-hickory)
                  title       (->> thread-body
                                   (s/select (s/class "threadtitle"))
                                   first
                                   (s/select (s/tag :a))
                                   first :content first string/trim)]
              (eprintln title)
              (try
                (->> thread-body
                     (s/select (s/class "postcontent"))
                     first
                     (s/select (s/find-in-text #"magnet:.+"))
                     first :content
                     (filter string?)
                     (map #(->> % string/trim
                                (re-find #"^magnet:\?(.+)$")))
                     (filter (comp not nil?))
                     (map #(->> % second
                                form-decode keywordize-keys :xt
                                (str "magnet:?xt="))))
                (catch Exception _
                  (-> (<< "ERROR: Failed to extract magnet URI for ~{title}")
                      colors/red colors/bold eprintln))))))

(defn -main [& args]
  (let [search-id   (or (first args)
                      (-> "search.edn"
                          slurp edn/read-string search))]
    (eprintln (<< "Search ID: ~{search-id}"))
    (doseq [page-body (get-pages search-id)]
      (doseq [magnet-links (get-links page-body)]
        (doseq [l magnet-links]
          (println l))))
    (cp/shutdown net-pool)
    (System/exit 0)))


