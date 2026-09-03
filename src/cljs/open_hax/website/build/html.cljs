(ns open-hax.website.build.html
  "Build-time writer for the per-locale HTML shells. A node script, not part
  of the browser bundle.

  The shells are generated from the same locale dictionaries the running app
  uses, through `open-hax.website.domain.html-document`, so a translated
  `<title>` cannot drift from a translated page: there is exactly one place a
  message lives. A JavaScript build script could not read those dictionaries,
  which is why this step is ClojureScript."
  (:require ["fs" :as fs]
            ["path" :as path]
            [open-hax.website.domain.html-document :as html-document]))

(defn write-pages!
  "Write every locale's shell under `out-dir`. Returns the paths written."
  [out-dir]
  (mapv (fn [{:keys [page/output-path page/html]}]
          (let [target (path/join out-dir output-path)]
            (fs/mkdirSync (path/dirname target) #js {:recursive true})
            (fs/writeFileSync target html "utf8")
            target))
        (html-document/pages)))

(defn main
  [& args]
  (let [out-dir (or (first args) "dist/site")
        written (write-pages! out-dir)]
    (doseq [target written]
      (println "wrote" target))
    (println (str "wrote " (count written) " locale shells to " out-dir))))
