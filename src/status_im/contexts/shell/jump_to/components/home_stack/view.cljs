(ns status-im.contexts.shell.jump-to.components.home-stack.view
  (:require
    [quo.theme :as quo.theme]
    [react-native.reanimated :as reanimated]
    [status-im.contexts.chat.home.view :as chat]
    [status-im.contexts.communities.home.view :as communities]
    [status-im.contexts.shell.jump-to.components.home-stack.style :as style]
    [status-im.contexts.shell.jump-to.constants :as shell.constants]
    [status-im.contexts.shell.jump-to.state :as state]
    [status-im.contexts.shell.jump-to.utils :as utils]
    [status-im.contexts.wallet.home.view :as wallet]
    [utils.re-frame :as rf]))

(defn load-stack?
  [stack-id]
  (case stack-id
    :communities-stack @state/load-communities-stack?
    :chats-stack       @state/load-chats-stack?
    :homi-stack     @state/load-homi-stack?
    :iot-stack      @state/load-iot-stack?
    :wallet-stack      @state/load-wallet-stack?))

(defn- f-stack-view
  [stack-id shared-values]
  [reanimated/view
   {:style (style/stack-view
            stack-id
            {:opacity (get shared-values
                           (get shell.constants/stacks-opacity-keywords stack-id))
             :z-index (get shared-values
                           (get shell.constants/stacks-z-index-keywords stack-id))})}
   (case stack-id
     :communities-stack [:f> communities/view]
     :chats-stack       [:f> chat/view]
     :homi-stack        [:f> chat/view]
     :iot-stack      [:f> chat/view]
     :wallet-stack     [wallet/view]
     [:<>])])

(defn lazy-screen
  [stack-id shared-values]
  (when (load-stack? stack-id)
    [:f> f-stack-view stack-id shared-values]))

(defn f-home-stack
  []
  (let [shared-values            @state/shared-values-atom
        theme                    (quo.theme/use-theme-value)
        {:keys [width height]}   (utils/dimensions)
        alert-banners-top-margin (rf/sub [:alert-banners/top-margin])]
    [reanimated/view
     {:style (style/home-stack
              shared-values
              {:theme  theme
               :width  width
               :height (- height alert-banners-top-margin)})}
     [lazy-screen :communities-stack shared-values]
     [lazy-screen :chats-stack shared-values]
     [lazy-screen :homi-stack shared-values]
     [lazy-screen :iot-stack shared-values]
     [lazy-screen :wallet-stack shared-values]]))
