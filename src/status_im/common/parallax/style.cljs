(ns status-im.common.parallax.style
  (:require
    [react-native.core :as rn]
    [react-native.safe-area :as safe-area]))

(defn outer-container
  [container-style]
  (merge {:position :absolute
          :top      (if rn/small-screen? (safe-area/get-top) 0)
          :left     0
          :right    0
          :bottom   0}
         container-style))

(def video
  {:position :absolute
   :top      0
   :left     0
   :right    0
   :bottom   0})

(defn container-view
  [width height]
  {:position :absolute
   :width    width
   :height   height})
