(ns bugs.background
  (:require [prospero.game-objects :as progo]))

(defn make-background
  [game-system image-path]
  (-> (progo/base-object game-system)
      (progo/bounds-box  1024 768)
      (progo/position-3d 0 0 -1)
      (progo/texture     image-path)))
