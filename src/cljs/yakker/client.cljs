(ns yakker.client
  (:require [cognitect.transit :as t]))

;;; Lifted from:
;;; https://yogthos.net/posts/2015-06-11-Websockets.html

;;; Transit over Websockets
(defonce ws-chan (atom nil))

(def ^:private json-reader (t/reader :json))
(def ^:private json-writer (t/writer :json))

(defn receive-transit-msg! [update-fn]
  (fn [msg]
    (update-fn (->> msg .-data (t/read json-reader)))))

(defn decorate [m s]
  (update-in m [:message] #(str %2 ": " %1) s))

(def myid (int (rand 10000)))

(defn send-transit-msg! [msg]
  (if @ws-chan
    (.send @ws-chan (t/write json-writer (decorate msg myid)))
    (throw (js/Error. "Websocket is not available!"))))

(defn make-websocket! [url receive-handler]
  (println "attempting to connect websocket")
  (if-let [chan (js/WebSocket. url)]
    (do (set! (.-onmessage chan) (receive-transit-msg! receive-handler))
        (reset! ws-chan chan)
        (println "Websocket connection established with: " url)
        chan)
    (throw (js/Error. "Websocket connection failed!"))))
