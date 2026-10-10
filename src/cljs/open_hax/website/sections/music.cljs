(ns open-hax.website.sections.music
  "Published Suno selections and optional staged local tracks."
  (:require [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.data.suno :as suno]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.extern.focus :as focus]))

(defn folder-badge
  [folder]
  (when (seq folder)
    (d/span {:class-name "px-2 py-0.5 text-xs rounded bg-slate-800 text-slate-300 border border-slate-700 truncate max-w-[120px]"}
            folder)))

(defnc TrackRow [{:keys [locale track index on-play]}]
  (d/button {:class-name "w-full text-left flex items-center gap-3 p-3 rounded-lg hover:bg-slate-800/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300 transition group"
             :type "button"
             :aria-label (copy/text-with locale :music/play {:title (:title track)})
             :on-click #(on-play track)}
            (d/span {:class-name "text-slate-500 text-sm w-5 text-center font-mono"}
                    (str (inc index)))
            (d/span {:class-name "h-9 w-9 rounded bg-slate-800 flex items-center justify-center group-hover:bg-slate-700 transition shrink-0"
                     :aria-hidden true}
                    "▶")
            (d/div {:class-name "flex-1 min-w-0"}
                   (d/h4 {:class-name "text-white font-medium truncate text-sm"} (:title track))
                   (d/p {:class-name "text-slate-300 text-xs truncate"} (:folder track)))
            (folder-badge (:folder track))
            (d/span {:class-name "text-slate-300 text-xs font-mono shrink-0"}
                    (:duration track))))

(defnc SunoCard [{:keys [locale track active? on-load]}]
  (d/article {:class-name "rounded-xl overflow-hidden border border-slate-800 bg-slate-900"}
             (if active?
               (d/iframe {:src (:embed-url track)
                          :title (copy/text-with locale :music/player-label {:title (:title track)})
                          :ref focus/focus-connected!
                          :tab-index 0
                          :class-name "w-full h-64 border-0 bg-slate-950 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-cyan-300"
                          :allow "autoplay; encrypted-media"
                          :loading "lazy"
                          :referrer-policy "strict-origin-when-cross-origin"})
               (d/button {:type "button"
                          :class-name "relative group block w-full h-64 overflow-hidden bg-slate-950 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-cyan-300"
                          :aria-label (copy/text-with locale :music/play {:title (:title track)})
                          :on-click #(on-load (:id track))}
                         (d/img {:src (:cover-url track)
                                 :alt (:title track)
                                 :class-name "h-full w-full object-cover opacity-80 group-hover:opacity-100 transition-opacity"
                                 :loading "lazy"
                                 :decoding "async"})
                         (d/span {:class-name "absolute inset-0 flex items-center justify-center bg-slate-950/20"}
                                 (d/span {:class-name "inline-flex items-center gap-2 rounded-full border border-white/30 bg-slate-950/80 px-5 py-3 font-medium text-white shadow-lg group-hover:bg-slate-900 transition"}
                                         (d/span {:aria-hidden true} "▶")
                                         (copy/text locale :music/listen)))))
             (d/div {:class-name "p-5"}
                    (d/h3 {:class-name "text-white font-semibold leading-snug min-h-[2.75rem]"}
                          (:title track))
                    (d/a {:href (:song-url track)
                          :target "_blank"
                          :rel "noopener noreferrer"
                          :class-name "inline-flex items-center gap-2 text-sm text-cyan-300 hover:text-cyan-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300 rounded mt-3"}
                         (copy/text locale :music/song-link)
                         (d/span {:aria-hidden true} "↗")))))

(defnc MusicSection [{:keys [locale]}]
  (let [[playing set-playing] (hooks/use-state nil)
        [active-suno set-active-suno] (hooks/use-state nil)
        published (filterv :is-public? suno/tracks)]
    (when (or (seq published) (seq assets/music))
      (d/div {:class-name "max-w-6xl mx-auto"}
             (d/div {:class-name "text-center mb-12"}
                    (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                          (copy/text locale :music/heading))
                    (when (seq published)
                      (d/p {:class-name "text-slate-300 max-w-2xl mx-auto"}
                           (copy/text-with locale :music/lede {:count (count published)})))
                    (d/a {:href (:url suno/profile)
                          :target "_blank"
                          :rel "noopener noreferrer"
                          :class-name "inline-flex items-center gap-2 mt-5 text-cyan-300 hover:text-cyan-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300 rounded"}
                         (copy/text-with locale :music/profile {:name (:display-name suno/profile)})
                         (d/span {:aria-hidden true} "↗")))
             (when (seq published)
               (d/div {:class-name "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5"}
                      (for [track published]
                        ($ SunoCard {:key (:id track)
                                     :locale locale
                                     :track track
                                     :active? (= active-suno (:id track))
                                     :on-load #(do (set-playing nil) (set-active-suno %))}))))
             (when (seq assets/music)
               (d/div {:class-name "max-w-4xl mx-auto mt-12"}
                      (d/h3 {:class-name "text-xl font-semibold text-white mb-3"}
                            (copy/text locale :music/local-heading))
                      (d/p {:class-name "text-slate-300 text-sm mb-4"}
                           (copy/text-with locale :music/local-lede {:count (count assets/music)}))
                      (d/div {:class-name "space-y-0.5 max-h-[600px] overflow-y-auto"}
                             (for [[idx track] (map-indexed vector assets/music)]
                               ($ TrackRow {:key (:id track)
                                            :locale locale
                                            :track track
                                            :index idx
                                            :on-play #(do (set-active-suno nil) (set-playing %))})))
                      (when playing
                        (d/div {:class-name "mt-6 p-4 rounded-lg bg-slate-800/50 border border-slate-700"}
                               (d/p {:class-name "text-white font-medium"} (:title playing))
                               (d/audio {:key (:id playing)
                                         :src (:src playing)
                                         :auto-play true
                                         :controls true
                                         :preload "none"
                                         :aria-label (copy/text-with locale :music/play {:title (:title playing)})
                                         :class-name "w-full mt-2"})))))))))
