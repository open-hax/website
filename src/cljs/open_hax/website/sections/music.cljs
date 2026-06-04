(ns open-hax.website.sections.music
  "Music showcase section."
  (:require [helix.core :as hx :refer [$ defnc]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [clojure.string :as str]
            [open-hax.website.data.assets :as assets]))

(defn folder-badge [folder]
  (when (seq folder)
    (d/span {:class-name "px-2 py-0.5 text-xs rounded bg-slate-800 text-slate-400 border border-slate-700 truncate max-w-[120px]"}
            folder)))

(defnc TrackRow [{:keys [track index on-play]}]
  (d/div {:class-name "flex items-center gap-3 p-2 rounded-lg hover:bg-slate-800/50 transition group cursor-pointer"
          :on-click #(on-play track)}
         (d/span {:class-name "text-slate-600 text-sm w-5 text-center font-mono"}
                 (str (inc index)))
         (d/div {:class-name "h-9 w-9 rounded bg-slate-800 flex items-center justify-center group-hover:bg-slate-700 transition shrink-0"}
                (d/span {:class-name "text-sm"} "▶"))
         (d/div {:class-name "flex-1 min-w-0"}
                (d/h4 {:class-name "text-white font-medium truncate text-sm"} (:title track))
                (d/p {:class-name "text-slate-500 text-xs truncate"} (:folder track)))
          (folder-badge (:folder track))
         (d/span {:class-name "text-slate-500 text-xs font-mono shrink-0"}
                 (:duration track))))

(defnc MusicSection []
  (let [[playing set-playing] (hooks/use-state nil)]
    (d/div {:class-name "max-w-4xl mx-auto"
            :id "music"}
           (d/div {:class-name "text-center mb-16"}
                  (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                        "Music")
                  (d/p {:class-name "text-slate-400 max-w-2xl mx-auto"}
                       (str (count assets/music) " sonic experiments and compositions.")))
           (d/div {:class-name "space-y-0.5 max-h-[600px] overflow-y-auto pr-2"
                   :style {:scrollbar-width "thin"
                           :scrollbar-color "#334155 transparent"}}
                  (for [[idx track] (map-indexed vector assets/music)]
                    ($ TrackRow {:key (:id track)
                                 :track track
                                 :index idx
                                 :on-play set-playing})))
           (when playing
             (d/div {:class-name "mt-6 p-4 rounded-lg bg-slate-800/50 border border-slate-700"}
                    (d/p {:class-name "text-white font-medium"} (:title playing))
                    (d/p {:class-name "text-slate-400 text-sm"} (:folder playing))
                    (d/audio {:src (:src playing)
                              :controls true
                              :class-name "w-full mt-2"
                              :auto-play true}))))))
