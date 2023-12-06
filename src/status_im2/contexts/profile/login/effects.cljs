(ns status-im2.contexts.profile.login.effects
  (:require
    [native-module.core :as native-module]
    [status-im2.contexts.profile.config :as profile.config]
    [utils.re-frame :as rf]))

(rf/reg-fx :effects.profile/login
 (fn [[key-uid hashed-password]]
   ;;"node.login" signal will be triggered as a callback
   (native-module/login-account
    (assoc (profile.config/login) :keyUid key-uid :password hashed-password))))