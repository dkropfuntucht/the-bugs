(ns bugs.sprite-font
  (:require [bugs.enemies :as enemies]
            [prospero.game-objects :as progo]))

(defmulti letter-pos (fn [letter] letter))

(defmethod letter-pos :b
  [_]
  [[  0   0]
   [ 64   0]
   [128   0]
   [  0  64]
   [  0 128]
   [  0 196]
   [  0 256]
   [192  64]
   [ 64 128]
   [128 128]
   [192 196]
   [ 64 256]
   [128 256]])

(defmethod letter-pos :e
  [_]
  [[  0   0]
   [  0  64]
   [  0 128]
   [  0 192]
   [  0 256]
   [ 64   0]
   [128   0]
   [ 64 128]
   [ 64 256]
   [128 256]])

(defmethod letter-pos :g
  [_]
  [

   [  0   0]
   [ 64   0]
   [128   0]
   [196   0]
   [  0  64]
   [  0 128]
   [  0 196]
   [  0 256]
   [ 64 256]
   [128 256]
   [128 128]
   [196 192]
   [196 256]])

(defmethod letter-pos :h
  [_]
  [[  0   0]
   [  0  64]
   [  0 128]
   [  0 192]
   [  0 256]
   [ 64 128]
   [128 128]
   [196   0]
   [196  64]
   [196 128]
   [196 192]
   [196 256]])

(defmethod letter-pos :s
  [_]
  [[  0    0]
   [ 64    0]
   [128    0]
   [196    0]
   [  0   64]
   [  0  128]
   [ 64  128]
   [128  128]
   [196  128]
   [196  196]
   [  0  256]
   [ 64  256]
   [128  256]
   [196  256]])

(defmethod letter-pos :t
  [_]
  [[  0   0]
   [ 64   0]
   [128   0]
   [192   0]
   [256   0]
   [128  64]
   [128 128]
   [128 192]
   [128 256]])

(defmethod letter-pos :u
  [_]
  [[  0   0]
   [  0  64]
   [  0 128]
   [  0 196]
   [  0 256]
   [  0 256]
   [ 64 256]
   [128 256]
   [196   0]
   [196  64]
   [196 128]
   [196 196]
   [196 256]])

(defn letter
  [game-system x-loc y-loc z-loc letter]
  (-> (progo/base-object game-system)
      (progo/position-3d x-loc y-loc z-loc)
      (assoc :children
             (map
              (fn [[x y]]
                (enemies/make-bug game-system x y))
              (letter-pos letter)))))
