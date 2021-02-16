(ns bugs.player
  (:require [prospero.game-objects :as progo]
            [prospero.events :as proevent]))

(def tile-size 64)

(defn make-player
  [game-system x-loc y-loc]
  (-> (progo/base-object  game-system)
      (progo/bounds-box   tile-size tile-size)
      (progo/sprite-sheet {:sprite-sheet-path "/images/sprite_sheet.png"
                           :sprite-width      tile-size
                           :sprite-height     tile-size
                           :columns           5
                           :rows              5})
      (progo/sprite-index 0 1)
      (progo/position-3d  x-loc y-loc 0)
      (progo/add-animators
       [[[:translation 0] [[20 :pixels] [1 :second]] :constant {:limit         1000
                                                                :animator-id  ::anim-right
                                                                :initial-state :stopped}]
        [[:translation 0] [[-20 :pixels] [1 :second]] :constant {:limit 20
                                                                 :animator-id  ::anim-left
                                                                 :initial-state :stopped}]
        [[:translation 1] [[-20 :pixels] [1 :second]] :constant {:limit         20
                                                                 :animator-id  ::anim-up
                                                                 :initial-state :stopped}]

        [[:translation 1] [[20 :pixels] [1 :second]] :constant {:limit         20
                                                                :animator-id  ::anim-down
                                                                :initial-state :stopped}]
        [[:texture :col] [[1 :unit] [300 :ms]] :constant {:limit         3
                                                          :domain        :integers
                                                          :on-limit      :reset
                                                          :initial-value 0
                                                          :animator-id   ::anim-move
                                                          :initial-state :stopped}]])
      (progo/process-event
       {[::proevent/signal ::move-left]
        [::proevent/change-animators-on-event {::anim-left  :running
                                               ::ani-move  :running
                                               ::anim-right :stopped
                                               ::anim-up    :stopped
                                               ::anim-down  :stopped}]

        [::proevent/signal ::move-down]
        [::proevent/change-animators-on-event {::anim-left  :stopped
                                               ::anim-move  :running
                                               ::anim-right :stopped
                                               ::anim-up    :stopped
                                               ::anim-down  :running}]
        [::proevent/signal ::move-right]
        [::proevent/change-animators-on-event {::anim-left  :stopped
                                               ::anim-move  :running
                                               ::anim-right :running
                                               ::anim-up    :stopped
                                               ::anim-down  :stopped}]
        [::proevent/signal ::move-up]
        [::proevent/change-animators-on-event {::anim-left  :stopped
                                               ::anim-move  :running
                                               ::anim-right :stopped
                                               ::anim-up    :running
                                               ::anim-down  :stopped}]

        [::proevent/signal ::move-stop]
        [::proevent/change-animators-on-event {::anim-left  :stopped
                                               ::anim-move  :stopped
                                               ::anim-right :stopped
                                               ::anim-up    :stopped
                                               ::anim-down  :stopped}]})))
