(ns bugs.screens)

(defmulti screen-children (fn [game-mode game-system] game-mode))
