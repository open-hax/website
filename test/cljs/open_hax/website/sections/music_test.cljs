(ns open-hax.website.sections.music-test
  (:require ["react-dom/server" :as rdom-server]
            [cljs.test :refer [deftest is testing]]
            [clojure.string :as str]
            [helix.core :refer [$]]
            [helix.hooks :as hooks]
            [open-hax.website.data.assets :as assets]
            [open-hax.website.data.suno :as suno]
            [open-hax.website.sections.music :as music]))

(deftest music-section-exists
  (testing "MusicSection is a function/component"
    (is (fn? music/MusicSection))))

(deftest track-row-exists
  (testing "TrackRow is a function/component"
    (is (fn? music/TrackRow))))

(deftest an-empty-track-list-is-a-valid-build
  (testing "the optional local collection may remain empty"
    (is (vector? assets/music))
    (is (<= 0 (count assets/music)))))

(deftest every-listed-track-is-addressable-inside-the-build-output
  (doseq [track assets/music]
    (is (str/starts-with? (:src track) "/music/") (pr-str track))
    (is (not (str/includes? (:src track) "..")) (pr-str track))
    (is (int? (:id track)) (pr-str track))))

(deftest track-ids-are-stable-and-unique
  (is (= (count assets/music) (count (set (map :id assets/music))))))

(defn- render [element]
  (.renderToStaticMarkup rdom-server element))

(deftest public-song-selection-is-explicit-and-exact
  (is (= "https://suno.com/@misstakenid" (:url suno/profile)))
  (is (= "calliope.suno.metadata/normalize" (:normalizer suno/profile)))
  (is (re-matches #"[a-f0-9]{64}" (:source-sha256 suno/profile)))
  (is (= 6 (count suno/tracks)))
  (is (= (count suno/tracks) (count (set (map :id suno/tracks)))))
  (doseq [track suno/tracks]
    (is (true? (:is-public? track)) (pr-str (:id track)))
    (is (= (str "https://suno.com/song/" (:id track)) (:song-url track)))
    (is (= (str "https://suno.com/embed/" (:id track)) (:embed-url track)))
    (is (str/starts-with? (:cover-url track) "https://cdn2.suno.ai/"))
    (is (pos? (:duration-seconds track)))
    (is (= #{:id :title :song-url :embed-url :cover-url :observed-audio-url :duration-seconds :is-public?}
           (set (keys track))) "no account or credential fields belong in presentation data")))

(deftest public-music-renders-song-and-profile-links-without-loading-players
  (let [markup (render ($ music/MusicSection {:locale :en}))]
    (is (str/includes? markup (:url suno/profile)))
    (doseq [track suno/tracks]
      (is (str/includes? markup (:song-url track)))
      (is (str/includes? markup (:title track))))
    (is (not (str/includes? markup "<iframe")))
    (is (not (str/includes? markup "<audio")))
    (is (not (str/includes? markup "autoplay")))))

(deftest an-active-card-uses-the-official-embed-and-retains-its-song-link
  (let [track (first suno/tracks)
        markup (render ($ music/SunoCard {:locale :en :track track :active? true
                                         :on-load identity}))]
    (is (str/includes? markup "<iframe"))
    (is (str/includes? markup (:embed-url track)))
    (is (str/includes? markup (:song-url track)))
    (is (str/includes? markup "title=\""))
    (is (not (str/includes? markup (:observed-audio-url track))))
    (is (not (str/includes? markup "autoplay=")))))

(deftest an-unpublished-track-is-never-presented-as-a-public-song
  (with-redefs [suno/tracks [(assoc (first suno/tracks) :is-public? false)]
                assets/music []]
    (is (= "" (render ($ music/MusicSection {:locale :en}))))))

(deftest an-empty-music-selection-hides-the-section
  (with-redefs [suno/tracks [] assets/music []]
    (is (= "" (render ($ music/MusicSection {:locale :en}))))))

(deftest optional-local-tracks-remain-keyboard-accessible
  (let [markup (render ($ music/TrackRow
                         {:locale :en :track {:id 1 :title "Local study" :duration "3:20"}
                          :index 0 :on-play identity}))]
    (is (str/starts-with? markup "<button"))
    (is (str/includes? markup "type=\"button\""))
    (is (str/includes? markup "Local study"))))

(deftest a-local-play-click-renders-the-selected-audio-ready-to-start
  (let [tracks [{:id 1 :title "Local study" :src "/music/study.mp3" :duration "3:20"}
                {:id 2 :title "Second study" :src "/music/second.mp3" :duration "2:10"}]
        state (atom [nil nil])
        hook-index (atom 0)
        clicks (atom {})
        track-row music/TrackRow
        use-state (fn [initial]
                    (let [index @hook-index]
                      (swap! hook-index inc)
                      [(get @state index initial)
                       #(swap! state assoc index %)]))
        render-section (fn []
                         (reset! hook-index 0)
                         (render ($ music/MusicSection {:locale :en})))]
    ;; Retain the real row and its actual button handler. Only the hook storage
    ;; is bound so server rendering can observe the state after that click;
    ;; this proves the media flags and selection, not a browser playback result.
    (with-redefs [assets/music tracks
                  hooks/use-state use-state
                  music/TrackRow (fn [^js props]
                                   (let [^js button (track-row props nil)
                                         ^js button-props (.-props button)
                                         track (.-track props)]
                                     (swap! clicks assoc (:id track) (.-onClick button-props))
                                     button))]
      (let [initial (render-section)]
        (is (not (str/includes? initial "<audio")))
        (is (not (str/includes? initial "<iframe")))
        (is (not (str/includes? initial "autoplay"))))
      (doseq [track tracks]
        ((get @clicks (:id track)))
        (let [markup (render-section)
              audio (re-find #"<audio[^>]*>" markup)]
          (is (= track (first @state)))
          (is (str/includes? (or audio "") (str "src=\"" (:src track) "\"")))
          (is (str/includes? (or audio "") "autoplay=\"\""))
          (is (str/includes? (or audio "") "controls=\"\""))
          (is (not (str/includes? markup "<iframe"))))))))
