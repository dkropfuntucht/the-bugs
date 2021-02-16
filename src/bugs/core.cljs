(ns bugs.core
  (:require [bugs.enemies :as enemies]
            [bugs.player :as player]
            [bugs.screens :as screens]
            [bugs.screens.game]
            [bugs.screens.title :as title]
            [bugs.sprite-font :as sprite-font]
            [iris.core :as iris]
            [prospero.core :as procore]
            [prospero.game-objects :as progo]
            [prospero.events :as proevent]
            [prospero.loop :as proloop]))

(def game-system {::procore/game-system      ::iris/game-system
                  ::proloop/global-mode-path [0 :global-mode]
                  :display-width             1024
                  :display-height             768
                  ::iris/web-root            (.getElementById js/document "app")})

(defn make-root
  [game-system]
  (->
   (progo/base-object   game-system)
   (assoc               :children     [])
   (assoc               :global-mode  :title-screen)
   (assoc               :current-mode :boot-up)
   (progo/update-state  (fn [me current-time-code frame-elapsed-time root-state global-mode]
                          (let [children     (:children me)
                                current-mode (:current-mode me)]

                            (if (= current-mode global-mode)
                              me
                              (do
                                (cond-> me
                                  true
                                  (assoc :current-mode global-mode)

                                  (not= global-mode :paused)
                                  (assoc :children (screens/screen-children global-mode game-system))))))))
   (progo/process-event
    {[::proevent/keyboard-up ::proevent/key-any-key {:active-global-modes #{:title-screen}}]
     #(assoc % :global-mode :game-screen)

     [::proevent/keyboard-held ::proevent/key-arrow-left {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-left]

     [::proevent/keyboard-held ::proevent/key-letter-a {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-left]

     [::proevent/keyboard-held ::proevent/key-arrow-down {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-down]

     [::proevent/keyboard-held ::proevent/key-letter-s {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-down]

     [::proevent/keyboard-held ::proevent/key-letter-d {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-right]

     [::proevent/keyboard-held ::proevent/key-arrow-right {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-right]

     [::proevent/keyboard-held ::proevent/key-arrow-up {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-up]

     [::proevent/keyboard-held ::proevent/key-letter-w {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-up]

     ;; - stops
     [::proevent/keyboard-up ::proevent/key-letter-a {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-stop]

     [::proevent/keyboard-up ::proevent/key-arrow-down {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-stop]

     [::proevent/keyboard-up ::proevent/key-arrow-left {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-stop]

     [::proevent/keyboard-up ::proevent/key-letter-s {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-stop]

     [::proevent/keyboard-up ::proevent/key-letter-d {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-stop]

     [::proevent/keyboard-up ::proevent/key-arrow-right {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-stop]

     [::proevent/keyboard-up ::proevent/key-arrow-up {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-stop]

     [::proevent/keyboard-up ::proevent/key-letter-w {:active-global-modes #{:game-screen}}]
     [::proevent/emit-signal-on-event ::player/move-stop]})))

(procore/start-game game-system
                    [(make-root game-system)])
