(ns status-im.contexts.wallet.collectible.view
  (:require
    [quo.core :as quo]
    [quo.theme :as quo.theme]
    [react-native.core :as rn]
    [react-native.svg :as svg]
    [reagent.core :as reagent]
    [status-im.common.scroll-page.view :as scroll-page]
    [status-im.contexts.wallet.collectible.options.view :as options-drawer]
    [status-im.contexts.wallet.collectible.style :as style]
    [status-im.contexts.wallet.collectible.tabs.view :as tabs]
    [utils.i18n :as i18n]
    [utils.re-frame :as rf]))

(defn header
  [collectible-name collection-name collection-image-url]
  [rn/view {:style style/header}
   [quo/text
    {:weight :semi-bold
     :size   :heading-1} collectible-name]
   [rn/view {:style style/collection-container}
    [rn/view {:style style/collection-avatar-container}
     [quo/collection-avatar {:image collection-image-url}]]
    [quo/text
     {:weight :semi-bold
      :size   :paragraph-1}
     collection-name]]])

(defn cta-buttons
  []
  [rn/view {:style style/buttons-container}
   [quo/button
    {:container-style style/send-button
     :type            :outline
     :size            40
     :icon-left       :i/send}
    (i18n/label :t/send)]
   [quo/button
    {:container-style style/opensea-button
     :type            :outline
     :size            40
     :icon-left       :i/opensea}
    (i18n/label :t/opensea)]])

(def tabs-data
  [{:id                  :overview
    :label               (i18n/label :t/overview)
    :accessibility-label :overview-tab}
   {:id                  :about
    :label               (i18n/label :t/about)
    :accessibility-label :about-tab}])

(defn f-view-internal
  [{:keys [theme] :as _props}]
  (let [selected-tab  (reagent/atom :overview)
        on-tab-change #(reset! selected-tab %)]
    (fn []
      (let [collectible                 (rf/sub [:wallet/last-collectible-details])
            animation-shared-element-id (rf/sub [:animation-shared-element-id])
            {:keys [id
                    preview-url
                    collection-data
                    collectible-data]}  collectible
            {svg?        :svg?
             preview-uri :uri}          preview-url
            token-id                    (:token-id id)
            {collection-image :image-url
             collection-name  :name}    collection-data
            {collectible-name :name}    collectible-data
            collectible-image           {:image        preview-uri
                                         :image-width  300
                                         ; collectibles don't have width/height
                                         ; but we need to pass something
                                         ; without it animation doesn't work smoothly
                                         ; and :border-radius not  applied
                                         :image-height 300
                                         :id           token-id
                                         :header       collectible-name
                                         :description  collection-name}]
        (rn/use-unmount #(rf/dispatch [:wallet/clear-last-collectible-details]))
        [scroll-page/scroll-page
         {:navigate-back? true
          :height         148
          :page-nav-props {:type        :title-description
                           :title       collectible-name
                           :description collection-name
                           :right-side  [{:icon-name :i/options
                                          :on-press  #(rf/dispatch
                                                       [:show-bottom-sheet
                                                        {:content (fn [] [options-drawer/view
                                                                          {:name  collectible-name
                                                                           :image preview-uri}])
                                                         :theme   theme}])}]
                           :picture     preview-uri}}
         [rn/view {:style style/container}
          [rn/view {:style style/preview-container}
           [rn/touchable-opacity
            {:active-opacity 1
             :on-press       (fn []
                               (if svg?
                                 (js/alert "Can't visualize SVG images in lightbox")
                                 (rf/dispatch
                                  [:lightbox/navigate-to-lightbox
                                   token-id
                                   {:images           [collectible-image]
                                    :index            0
                                    :on-options-press #(rf/dispatch [:show-bottom-sheet
                                                                     {:content
                                                                      (fn []
                                                                        [options-drawer/view
                                                                         {:name  collectible-name
                                                                          :image preview-uri}])}])}])))}
            (if svg?
              [rn/view
               {:style     (assoc style/preview :overflow :hidden)
                :native-ID (when (= animation-shared-element-id token-id)
                             :shared-element)}
               [svg/svg-uri (assoc style/preview :uri preview-uri)]]
              [rn/image
               {:source    preview-uri
                :style     style/preview
                :native-ID (when (= animation-shared-element-id token-id) :shared-element)}])]]
          [header collectible-name collection-name collection-image]
          [cta-buttons]
          [quo/tabs
           {:size           32
            :style          style/tabs
            :scrollable?    true
            :default-active @selected-tab
            :on-change      on-tab-change
            :data           tabs-data}]
          [tabs/view {:selected-tab @selected-tab}]]]))))

(defn- view-internal
  [props]
  [:f> f-view-internal props])

(def view (quo.theme/with-theme view-internal))
