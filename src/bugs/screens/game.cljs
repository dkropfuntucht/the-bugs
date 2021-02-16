(ns bugs.screens.game
  (:require [bugs.screens :as screen]
            [bugs.background :as background]
            [bugs.player :as player]))

(defmethod screen/screen-children :game-screen
  [code game-system]
  [(background/make-background game-system "images/starfield.png")
   (player/make-player game-system 492 600)])
