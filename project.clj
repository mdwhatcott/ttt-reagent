(defproject ttt-reagent "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]
                 [reagent "1.1.0"]
                 [ttt-grid "0.1.1"]]

  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.10.764"]
                                  [speclj "3.3.1"]
                                  [com.cleancoders.c3kit/apron "1.0.2"]]}}
  :plugins [[speclj "3.3.1"]
            [lein-cljsbuild "1.1.8"]]

  :source-paths ["src/cljs"]
  :test-paths ["spec/cljs"]

  :clean-targets ^{:protect false} [:target-path "resources/public/cljs"]

  :aliases {"cljs"  ["run" "-m" "ttt-reagent.cljs"]
            "test1" ["run" "-m" "ttt-reagent.cljs" "once" "development"]
            "tests" ["run" "-m" "ttt-reagent.cljs" "auto" "development"]
            "prod1" ["run" "-m" "ttt-reagent.cljs" "once" "production"]
            "prods" ["run" "-m" "ttt-reagent.cljs" "auto" "production"]})
