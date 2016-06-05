(ns yakker.core
  (:require-macros [devcards.core :refer [defcard defcard-rg]])
  (:require [cognitect.transit :as t]
            [reagent.core :as reagent :refer [atom]]))

;;; Lifted from:
;;; https://yogthos.net/posts/2015-06-11-Websockets.html

;;; Transit over Websockets
(defonce ws-chan (atom nil))

(defonce messages (atom []))

(def json-reader (t/reader :json))
(def json-writer (t/writer :json))

(defn receive-transit-msg! [update-fn]
  (fn [msg]
    (update-fn (->> msg .-data (t/read json-reader)))))

(defn send-transit-msg! [msg]
  (if @ws-chan
    (.send @ws-chan (t/write json-writer msg))
    (throw (js/Error. "Websocket is not available!"))))

(defn make-websocket! [url receive-handler]
  (println "attempting to connect websocket")
  (if-let [chan (js/WebSocket. url)]
    (do (set! (.-onmessage chan) (receive-transit-msg! receive-handler))
        (reset! ws-chan chan)
        (println "Websocket connection established with: " url))
    (throw (js/Error. "Websocket connection failed!"))))

(defn update-messages! [{:keys [message]}]
  (swap! messages #(vec (take 10 (conj % message)))))

;;; UI
(defn message-list []
  [:ul (for [[i message] (map-indexed vector @messages)] ^{:key i} [:li message])])

(defn message-input []
  (let [value (atom nil)]
    (fn []
      [:input.form-control {:type        :text
                            :placeholder "type in a message and press enter"
                            :value       @value
                            :on-change   #(reset! value (-> % .-target .-value))
                            :on-key-down #(when (= (.-keyCode %) 13)
                                            (send-transit-msg! {:message @value})
                                            (reset! value nil))}])))

(defcard-rg Messages [message-list])

(defcard-rg Input [message-input])

;;; Main
(defn init! []
  (make-websocket! (str "ws://localhost:3000/ws") update-messages!))

(init!)
