(ns bugs.screens.title
  (:require [bugs.screens :as screen]
            [bugs.background :as background]
            [bugs.sprite-font :as sprite-font]))


(defmethod screen/screen-children :title-screen
  [code game-system]
  [(background/make-background game-system "images/starfield.png")
   (sprite-font/letter game-system  30  10 1 :t)
   (sprite-font/letter game-system 386  10 1 :h)
   (sprite-font/letter game-system 686  10 1 :e)
   (sprite-font/letter game-system   0 350 1 :b)
   (sprite-font/letter game-system 250 350 1 :u)
   (sprite-font/letter game-system 509 350 1 :g)
   (sprite-font/letter game-system 769 350 1 :s)])
