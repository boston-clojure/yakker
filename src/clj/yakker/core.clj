(ns yakker.core
  (:require [bidi.ring :as br]
            [clojure.tools.logging :as log]
            [immutant.web :refer [run]]
            [immutant.web.async :as async]
            [immutant.web.undertow :as undertow]
            [ring.util.response :as rur]))

;;; Partially lifted from:
;;; http://www.luminusweb.net/docs/websockets.md#immutant

;;; Websockets
(defonce channels (atom #{}))

(defn connect! [channel]
  (log/info "channel open")
  (swap! channels conj channel))

(defn disconnect! [channel {:keys [code reason]}]
  (log/info "close code:" code "reason:" reason)
  (swap! channels #(remove #{channel} %)))

(defn notify-clients! [channel msg]
  (doseq [channel @channels]
    (async/send! channel msg)))

(def websocket-callbacks {:on-open    connect!
                          :on-close   disconnect!
                          :on-message notify-clients!})

;;; Handlers
(defn ws-handler [request]
  (async/as-channel request websocket-callbacks))

(defn index [_]
  (assoc-in (rur/resource-response "index.html" {:root "public"})
            [:headers "Content-type"]
            "text/html"))

(defn status [request]
  {:headers {"Content-Type" "text/plain"}
   :body    (with-out-str (clojure.pprint/pprint request))})

(defn not-found [_]
  (rur/not-found "Not found.\n"))

;;; Routes
(def routes ["/" [["ws"     #'ws-handler]
                  ["status" #'status]
                  [true     #'not-found]]])

;;; App
(def app (-> (br/make-handler routes)
             (undertow/http-handler)
             (undertow/graceful-shutdown 10000)))

(defn -main [& args] (run app {:port 3000}))
