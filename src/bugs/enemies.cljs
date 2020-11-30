(ns bugs.enemies
  (:require [prospero.game-objects :as progo]))

(def tile-size 64)

(defn make-bug
  [game-system x-loc y-loc]
  (-> (progo/base-object  game-system)
      (progo/bounds-box   tile-size tile-size)
      (progo/sprite-sheet {:sprite-sheet-path "/images/sprite_sheet.png"
                           :width             tile-size
                           :height            tile-size
                           :columns           5
                           :rows              5})
      (progo/sprite-index 0 0)
      (progo/position-3d  x-loc y-loc 0)))


(defn make-bug-manager
  [game-system]
  (-> (progo/base-object  game-system)
      (progo/bounds-box   (:display-width game-system)
                          (:display-height game-system))
      (progo/position-3d  0 0 0)
      #_(assoc :children
               (mapv
                (fn [[x y]]
                  (make-bug game-system x y))
                [[  0   0]
                 [ 64   0]
                 [128   0]
                 [192   0]
                 [256   0]
                 [128  64]
                 [128 128]
                 [128 192]
                 [128 256]

                 [384   0]
                 [384  64]
                 [384 128]
                 [384 192]
                 [384 256]
                 [448 128]
                 [512 128]
                 [576   0]
                 [576  64]
                 [576 128]
                 [576 192]
                 [576 256]

                 [704   0]
                 [704  64]
                 [704 128]
                 [704 192]
                 [704 256]
                 [768   0]
                 [832   0]
                 [768 128]
                 [768 256]
                 [832 256]

                 [  0 384]
                 [ 64 384]
                 [128 384]
                 [  0 448]
                 [  0 512]
                 [  0 578]
                 [  0 640]
                 [192 448]
                 [ 64 512]
                 [128 512]
                 [192 578]
                 [ 64 640]
                 [128 640]

                 [320 384]
                 [320 448]
                 [320 512]
                 [320 578]
                 [320 640]
                 [384 640]
                 [448 640]
                 [512 640]
                 [512 384]
                 [512 448]
                 [512 512]
                 [512 578]
                 [512 640]

                 [640 384]
                 [704 384]
                 [768 384]
                 [832 384]
                 [640 448]
                 [640 512]
                 [640 576]
                 [640 640]
                 [704 640]
                 [768 640]
                 [768 512]
                 [832 576]
                 [832 640]]))))
