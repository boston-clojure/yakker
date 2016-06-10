(ns yakker.client-devcards
  (:require-macros [devcards.core :refer [defcard defcard-rg]])
  (:require [yakker.client :refer [send-transit-msg! make-websocket!]]
            [markdown.core :refer [md->html]]
            [reagent.core :as reagent]))

;;; UI
(defonce messages (reagent/atom []))
(defonce ws-conn (atom))

(defn format-date [value]
  (str "(" (.toLocaleTimeString (js/Date. (if value value (js/Date.now)))) ") "))

(defn update-messages! [{:keys [message timestamp]}]
  (swap! messages #(vec (take-last 10 (conj % (str (when timestamp (format-date timestamp))
                                                   message))))))
(defn escape-html [text]
  (clojure.string/escape text
                         {\& "&amp;"
                          \< "&lt;"
                          \> "&gt;"
                          \" "&quot;"
                          \' "&#39;"}))

(defn decorate-msg
  "Parses for markdown syntax. Returns map html-formatted string"
  [msg]
  (mark-unsafe (md->html (escape-html msg))))

(defn mark-unsafe
  "Marks a string as safe for reagent. Takes string, returns map"
  [msg]
  {:dangerouslySetInnerHTML (js-obj "__html" msg)})

(defn message-list []
  [:ul (for [[i message] (map-indexed vector @messages)] ^{:key i} [:li (decorate-msg message)])])

(defn message-input []
  (let [value (reagent/atom nil)
        vv (reagent/atom nil)]
    (fn []
      [:div "Name"
       [:input.form-control {:type :text
                             :on-change   #(reset! vv (-> % .-target .-value))
                             }  ]
       "  Message"
       [:input.form-control {:type        :text
                             :placeholder "type in a message and press enter"
                             :value       @value
                             :on-change   #(reset! value (-> % .-target .-value))
                             :on-key-down #(when (= (.-keyCode %) 13)
                                             (send-transit-msg! {:message
                                                                 (str @vv ": " @value)
                                                                 :timestamp (js/Date.now)})
                                             (reset! value nil))}]]
      )))

(defcard-rg Messages [message-list])

(defcard-rg Input [message-input])

;;; Main
(defn init! []
  (when @ws-conn
    (.close @ws-conn))
  <<<<<<< HEAD
  =======

                                        ;(reset! ws-conn (make-websocket! (str "ws://localhost:3000/ws") update-messages!)))
  >>>>>>> a4aff4c7093e5c1441c4800e3950f405fc2022c1
  (reset! ws-conn (make-websocket! (str "ws://bosclj.xngns.net:3000/ws") update-messages!)))
(init!)
