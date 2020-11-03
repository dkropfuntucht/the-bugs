(ns bugs.core
  (:require [iris.core :as iris]
            [prospero.core :as procore]
            [prospero.game-objects :as progo]))

(def game-system {::procore/game-system ::iris/game-system
                  :display-width        1024
                  :display-height       768
                  ::iris/web-root       (.getElementById js/document "app")})

(procore/start-game game-system
                    [(-> (progo/base-object game-system)
                         (progo/bounds-box  1024 768)
                         (progo/colour-rgb  255 0 0))])
