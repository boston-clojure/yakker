(defproject yakker "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.36"]
                 [org.clojure/tools.logging "0.3.1"]
                 [bidi "2.0.9"]
                 [com.cognitect/transit-cljs "0.8.237"]
                 [org.immutant/web "2.1.4"]
                 [reagent "0.6.0-alpha2"]
                 [com.github.Sepia-Officinalis/clj-bitauth "0.1.2"]]
  :repositories [["jitpack" "https://jitpack.io"]]
  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-figwheel "0.5.3-2"]]
  :main ^:skip-aot yakker.core
  :target-path "target/%s"
  :profiles {:dev     {:dependencies [[com.cemerick/piggieback "0.2.1"]
                                      [devcards "0.2.1-7"]
                                      [figwheel-sidecar "0.5.3-2"]]
                       :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                                      :init             (do (use 'figwheel-sidecar.repl-api)
                                                            (start-figwheel!))}}
             :uberjar {:aot :all}}
  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :clean-targets ^{:protect false} ["resources/public/js/" "target"]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/cljs"]
                        :figwheel     {:devcards       true
                                       :websocket-host :js-client-host}
                        :compiler     {:main                 "yakker.client"
                                       :asset-path           "js/out"
                                       :output-to            "resources/public/js/yakker.js"
                                       :output-dir           "resources/public/js/out"
                                       :source-map-timestamp true}}]})
