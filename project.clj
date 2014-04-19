(defproject cam-clj/mazes "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [rm-hull/monet "0.1.10"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["src/cljs"]

  :cljsbuild {
    :builds [{:id "mazes"
              :source-paths ["src/cljs"]
              :compiler {
                :output-to "mazes.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
