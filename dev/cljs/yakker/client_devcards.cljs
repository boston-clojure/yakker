(ns yakker.client-devcards
  (:require-macros [devcards.core :refer [defcard defcard-rg]])
  (:require [yakker.client :refer [send-transit-msg! make-websocket!]]
            [reagent.core :as reagent]))

;;; UI
(defonce messages (reagent/atom (list)))
(defonce ws-conn (atom))

(defn update-messages! [{:keys [message]}]
  (swap! messages #(take 10 (conj % message))))

(defn message-list []
  [:ul (reverse (for [[i message] (map-indexed vector @messages)] ^{:key i} [:li message]))])

(defn message-input []
  (let [value (reagent/atom nil)]
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

(defcard-rg TinkerSome [:div "A simple dev card.  Almost boring."])

;;; Main
(defn init! []
  (when @ws-conn
    (.close @ws-conn))
  (reset! ws-conn (make-websocket! (str "ws://bosclj.xngns.net:3000/ws") update-messages!)))

(init!)
