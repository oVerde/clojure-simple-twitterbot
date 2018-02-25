(ns twiiter-bot.core
  (:require [twitter.oauth :as oauth]
            [twitter.api.restful :as rest]))

(defonce app-consumer-key         (System/getenv "TWITTER_CONSUMER_KEY"))
(defonce app-consumer-secret      (System/getenv "TWITTER_CONSUMER_SECRET"))
(defonce user-access-token        (System/getenv "TWITTER_ACCESS_TOKEN"))
(defonce user-access-token-secret (System/getenv "TWITTER_ACCESS_TOKEN_SECRET"))

(def creds (oauth/make-oauth-creds
             app-consumer-key
             app-consumer-secret
             user-access-token
             user-access-token-secret))

(defn tweet [msg]
  (rest/statuses-update :oauth-creds creds :params {:status msg}))

(defn read-in-messages [message-file]
  (vec (filter not-empty (line-seq (clojure.java.io/reader message-file)))))

(defn robust-tweet [messages]
  (if (empty? messages)
    (println "No messages. Exiting")
    (let [msg (rand-nth messages)]
      (println "Choosing: " msg)
      (try
        (tweet msg)
        (println "Done.")
        (catch Throwable t
          (robust-tweet (vec (filter #(= msg %) messages))))))))

(defn choose-and-tweet [message-file]
  (try
    (let [messages (read-in-messages message-file)]
      (robust-tweet messages))
    (catch Throwable t
      (println (.getMessage t)))))


(defn -main [message-file]
  (choose-and-tweet message-file)
  (System/exit 0))





