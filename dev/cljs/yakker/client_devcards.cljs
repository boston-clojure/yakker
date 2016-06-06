(ns yakker.client-devcards
  (:require-macros [devcards.core :refer [defcard defcard-rg]])
  (:require [yakker.client :refer [send-transit-msg! make-websocket! messages update-messages!]]
            [reagent.core :as reagent]))

;;; UI
(defn message-list []
  [:ul (for [[i message] (map-indexed vector @messages)] ^{:key i} [:li message])])

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

;;; Main
(defn init! []
  (make-websocket! (str "ws://localhost:3000/ws") update-messages!))

(init!)
