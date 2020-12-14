(ns bugs.core
  (:require [bugs.enemies :as enemies]
            [bugs.screens :as screens]
            [bugs.screens.title :as title]
            [bugs.sprite-font :as sprite-font]
            [iris.core :as iris]
            [prospero.core :as procore]
            [prospero.game-objects :as progo]
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
   (progo/update-state (fn [me current-time-code frame-elapsed-time root-state global-mode]
                         (let [children     (:children me)
                               current-mode (:current-mode me)]

                           (if (= current-mode global-mode)
                             me
                             (do
                               (cond-> me
                                 true
                                 (assoc :current-mode global-mode)

                                 (not= global-mode :paused)
                                 (assoc :children (screens/screen-children global-mode game-system))))))))))

(procore/start-game game-system
                    [(make-root game-system)])
