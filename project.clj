(defproject ttt-reagent "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]]

  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.10.764"]
                                  [speclj "3.3.1"]
                                  [com.cleancoders.c3kit/apron "1.0.2"]]}}
  :plugins [[speclj "3.3.1"]
            [lein-cljsbuild "1.1.8"]]

  #_:cljsbuild #_{:builds        {:dev  {:source-paths   ["src/cljs" "spec/cljs"]
                                     :compiler       {:output-to     "js/ttt-reagent_dev.js"
                                                      :optimizations :whitespace
                                                      :pretty-print  true}
                                     :notify-command ["phantomjs"  "bin/speclj" "js/ttt-reagent_dev.js"]}

                              :prod {:source-paths ["src/cljs"]
                                     :compiler     {:output-to     "js/ttt-reagent.js"
                                                    :optimizations :simple}}}
              :test-commands {"test" ["phantomjs" "bin/speclj" "js/ttt-reagent_dev.js"]}}

  :source-paths ["src/clj" "src/cljs"]
  :test-paths ["spec/clj"]

  :clean-targets ^{:protect false} [:target-path "resources/public/cljs"]

  :aliases {"cljs" ["run" "-m" "ttt-reagent.cljs"]})
