(ns open-hax.website.sections.published
  "Published documents: the content the site did not compile in.

  Kept deliberately separate from the site's own sections. The hero, the
  product cards and the two galleries are OpenHax's own copy and ship in the
  build; everything here arrives as data through the publication seam and is
  rendered only because a manifest route names it. Nothing in this namespace
  reaches a Knoxx origin — it reads files from this origin's content root."
  (:require [helix.core :refer [defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [open-hax.website.domain.copy :as copy]
            [open-hax.website.domain.locale :as locale]
            [open-hax.website.domain.published :as published]
            [open-hax.website.infra.published-artifact :as artifact]))

(defnc PublishedListing
  "Routes the manifest carries for this locale. Renders nothing at all when
  there are none, which is every deploy before the first publication."
  [{:keys [locale manifest]}]
  (let [entries (published/listing manifest locale)]
    (when (seq entries)
      (d/div {:class-name "max-w-4xl mx-auto"}
             (d/h2 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-8 text-center"}
                   (copy/text locale :published/heading))
             (d/ul {:class-name "space-y-2"}
                   (for [{:keys [listing/href listing/title]} entries]
                     (d/li {:key href}
                           (d/a {:href href
                                 :class-name "block px-4 py-3 rounded-lg border border-slate-800 bg-slate-900/50 text-slate-200 hover:border-slate-700 hover:text-white transition"}
                                (or title (copy/text locale :published/untitled))))))))))

(defnc PublishedNotFound
  "The only page a static SPA fallback cannot give a 404 status to. It says so
  in words instead — see docs/decisions/0001-spa-fallback-over-prerendering.md."
  [{:keys [locale]}]
  (d/main {:class-name "min-h-screen flex items-center justify-center px-4"}
          (d/div {:class-name "max-w-xl text-center"}
                 (d/h1 {:class-name "text-3xl sm:text-4xl font-bold text-white mb-4"}
                       (copy/text locale :published/not-found-title))
                 (d/p {:class-name "text-slate-400 mb-8"}
                      (copy/text locale :published/not-found-body))
                 (d/a {:href (locale/locale-root locale)
                       :class-name "px-6 py-3 bg-white text-slate-950 font-semibold rounded-lg hover:bg-slate-100 transition"}
                      (copy/text locale :published/back)))))

(defnc PublishedDocument
  "One published artifact, fetched from the content root by the path the
  manifest gave for it.

  The artifact HTML is inserted as-is. It comes from this origin's read-only
  content root, written by the single writer the content root allows, and the
  manifest is the commit point for it — the same trust boundary that lets the
  build inline its own markup. Nothing user-supplied reaches here."
  [{:keys [locale route]}]
  (let [[state set-state] (hooks/use-state {:artifact/status :loading})]
    (hooks/use-effect
     [(:route/path route)]
     (let [live? (atom true)]
       (set-state {:artifact/status :loading})
       (-> (artifact/load! route)
           (.then (fn [result]
                    (when @live? (set-state result)))))
       #(reset! live? false)))
    (d/main {:class-name "min-h-screen px-4 sm:px-6 lg:px-8 pt-24 pb-16"}
            (d/article {:class-name "max-w-3xl mx-auto"}
                       (d/a {:href (locale/locale-root locale)
                             :class-name "text-xs text-slate-500 hover:text-slate-300 transition"}
                            (copy/text locale :published/back))
                       (case (:artifact/status state)
                         :loading nil

                         :ok (d/div {:class-name "mt-6 prose prose-invert max-w-none text-slate-200"
                                     :dangerouslySetInnerHTML #js {:__html (:artifact/body state)}})

                         (d/div {:role "alert" :class-name "mt-6"}
                                (d/h1 {:class-name "text-2xl font-bold text-white mb-2"}
                                      (copy/text locale :published/not-found-title))
                                (d/p {:class-name "text-slate-400"}
                                     (copy/text locale :published/not-found-body))))))))
