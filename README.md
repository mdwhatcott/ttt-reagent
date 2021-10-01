# ttt-reagent

## Requirements

1. [Clojure](https://clojure.org/)
1. [Leiningen](https://leiningen.org/)
1. [PhantomJS](https://phantomjs.org/)


## Commands:

1. `lein cljs once development` - Runs tests
1. `lein cljs auto development` - Runs tests continuously on detection of changes
1. `lein cljs once production` - Compiles ClojureScript to javascript
1. `lein cljs auto production` - Compiles ClojureScript to javascript continuously on detection of changes
1. `lein clean` - Deletes all compiled artifacts.

## Play Local Instance:

Run `lein cljs once production` and navigate in a web browser to the file at `/resources/public/index.html`.

```
$ open $(pwd)/resources/public/index.html
```

## Play Online Instance:

Navigate to https://mdwhatcott.github.io/ttt-reagent/resources/public/

