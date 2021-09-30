(ns ttt-reagent.core-spec
  (:require-macros
    [speclj.core :refer
     [describe context before it should= should-not=]])
  (:require
    [speclj.core]
    [ttt-reagent.components :as components]
    [cljs.pprint :as pprint]))

(defn parse-arena [arena grid-width]
  (let [box-count (* grid-width grid-width)]
    {:root       (first arena)
     :attributes (second arena)
     :boxes      (->> arena (drop 2) (take box-count) vec)
     :marks      (->> arena (drop 2) (drop box-count) vec)}))

(defn click-box! [index]
  (let [before     (components/arena)
        parsed     (parse-arena before 3)
        box        (get (:boxes parsed) index)
        attributes (second box)
        on-click!  (:on-click attributes)]
    (on-click!)))

(defn assert-class [expected-class indices all-boxes]
  (let [boxes (map #(get all-boxes %) indices)]
    (doseq [box boxes
            :let [class (:class (second box))]]
      (should= class expected-class))))

(describe "Arena Component"
  (context "rendering - 3x3"

    (before (components/new-game! 3))

    (context "empty grid"

      (it "renders empty boxes ready to be clicked"
        (let [rendered        (components/arena)
              parsed          (parse-arena rendered 3)
              root-attributes (:attributes parsed)]
          (should= :svg (:root parsed))
          (should= "0 0 3 3" (:view-box root-attributes))
          (should= "100%" (:width root-attributes))
          (should= "100%" (:height root-attributes))

          (doseq [box (:boxes parsed)]
            (let [tag            (first box)
                  box-attributes (second box)]
              (should= :rect tag)
              (should= 0.9 (:width box-attributes))
              (should= 0.9 (:height box-attributes))
              (should= :empty (:class box-attributes))))))
      )

    (context "after first turn by X"
      (before (click-box! 0))

      (it "renders the selected box without an on-click handler"
        (let [rendered    (components/arena)
              parsed      (parse-arena rendered 3)
              clicked-box (first (:boxes parsed))
              tag         (first clicked-box)
              config      (second clicked-box)]
          (should= :rect tag)
          (should= 0.9 (:width config))
          (should= 0.9 (:height config))
          (should= :empty (:class config))
          (should= nil (:on-click config))))

      (it "switches the player/mark"
        (should= :O @components/mark))

      (it "renders an 'X' in the clicked box"
        (let [rendered    (components/arena)
              parsed      (parse-arena rendered 3)
              clicked-box (first (:boxes parsed))
              box-config  (second clicked-box)

              mark        (first (:marks parsed))
              tag         (first mark)
              attributes  (second mark)
              text        (get mark 2)]
          (should= :text tag)
          (should= (int (:x attributes)) (:x box-config))
          (should= (int (:y attributes)) (:y box-config))
          (should= "X" text)))
      )

    (context "After X and O both take a turn"
      (before (click-box! 0)
              (click-box! 1))

      (it "the player/mark gets switched back to 'X'"
        (should= :X @components/mark))

      (it "renders an 'O' in the box that was clicked second"
        (let [rendered    (components/arena)
              parsed      (parse-arena rendered 3)
              clicked-box (second (:boxes parsed))
              box-config  (second clicked-box)

              mark        (second (:marks parsed))
              tag         (first mark)
              attributes  (second mark)
              text        (get mark 2)]
          (should= :text tag)
          (should= (int (:x attributes)) (:x box-config))
          (should= (int (:y attributes)) (:y box-config))
          (should= "O" text)))
      )

    (context "When a game ends in a win"
      (before (click-box! 0)                                ; X
              (click-box! 1)                                ; O
              (click-box! 2)                                ; X
              (click-box! 3)                                ; O
              (click-box! 4)                                ; X
              (click-box! 5)                                ; O
              (click-box! 6))                               ; X (WINNING PLAY)

      (it "indicates winning/losing moves via background colors"
        (let [rendered (components/arena)
              parsed   (parse-arena rendered 3)
              boxes    (:boxes parsed)]
          (assert-class :winner [0 2 4 6] boxes)
          (assert-class :loser [1 3 5] boxes)
          (assert-class :empty [7 8] boxes)))
      )

    (context "When starting over"
      (before (click-box! 0)
              (click-box! 1)
              (click-box! 2))

      (it "resets the game"
        (let [button (components/start-over)
              click  (:on-click (second button))
              _      (click)
              mark   @components/mark
              grid   @components/grid
              moves  (:moves grid)]
          (should= mark :X)
          (should= 0 (count moves)))))

    )
  )

(defn parse-grid-size [component]
  (let [inputs           (drop 2 component)

        div-radio-3x3    (first inputs)
        radio-3x3        (second div-radio-3x3)
        radio-config-3x3 (second radio-3x3)
        label-3x3        (nth div-radio-3x3 2)
        label-config-3x3 (second label-3x3)

        div-radio-4x4    (second inputs)
        radio-4x4        (second div-radio-4x4)
        radio-config-4x4 (second radio-4x4)
        label-4x4        (nth div-radio-4x4 2)
        label-config-4x4 (second label-4x4)]

    {:radio-3x3 radio-config-3x3
     :radio-4x4 radio-config-4x4

     :label-3x3 label-config-3x3
     :label-4x4 label-config-4x4}))

(describe "Grid Size Selection Component"
  (before (components/new-game! 3))

  (it "defines radio buttons for each supported grid size"
    (let [parsed (parse-grid-size (components/grid-size-selection))
          {:keys [radio-3x3 radio-4x4 label-3x3 label-4x4]} parsed]

      (should= :radio (:type radio-3x3))
      (should= :radio (:type radio-4x4))

      (should= "grid-size-selection" (:name radio-3x3))
      (should= "grid-size-selection" (:name radio-4x4))

      (should= (:id radio-3x3) (:for label-3x3))
      (should= (:id radio-4x4) (:for label-4x4))
      (should-not= (:id radio-3x3) (:id radio-4x4))))

  (it "allows changing the grid size to 4x4 and back to 3x3"
    (let [parsed         (parse-grid-size (components/grid-size-selection))
          on-click-4x4   (-> parsed :radio-4x4 :on-click)
          on-clicker-3x3 (-> parsed :radio-3x3 :on-click)]

      (on-click-4x4)
      (should= 4 (:width @components/grid))

      (on-clicker-3x3)
      (should= 3 (:width @components/grid))))

  (it "does nothing if the size clicked matches the current size"
    (let [parsed       (parse-grid-size (components/grid-size-selection))
          on-click-3x3 (-> parsed :radio-3x3 :on-click)]
      (click-box! 0)

      (on-click-3x3)

      (should= 1 (count (:filled-by-cell @components/grid)))))

  )
