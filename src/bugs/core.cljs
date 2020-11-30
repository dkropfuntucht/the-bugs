(ns bugs.core
  (:require [bugs.enemies :as enemies]
            [bugs.sprite-font :as sprite-font]
            [iris.core :as iris]
            [prospero.core :as procore]
            [prospero.game-objects :as progo]))

(def game-system {::procore/game-system ::iris/game-system
                  :display-width        1024
                  :display-height       768
                  ::iris/web-root       (.getElementById js/document "app")})

(procore/start-game game-system
                    [(sprite-font/letter game-system  30  10 1 :t)
                     (sprite-font/letter game-system 386  10 1 :h)
                     (sprite-font/letter game-system 686  10 1 :e)
                     (sprite-font/letter game-system   0 350 1 :b)
                     (sprite-font/letter game-system 250 350 1 :u)
                     (sprite-font/letter game-system 509 350 1 :g)
                     (sprite-font/letter game-system 769 350 1 :s)
                     (enemies/make-bug-manager game-system)])
