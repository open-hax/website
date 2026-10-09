(ns open-hax.website.infra.published-artifact-test
  "Exercise the fetch edge with native HTTP Response headers and bodies."
  (:require [cljs.test :refer [async deftest is]]
            [open-hax.website.infra.published-artifact :as artifact]
            [open-hax.website.infra.published-manifest :as manifest]))

(def route
  {:route/path "/notes/demo"
   :route/artifact "artifacts/notes/demo/en/rev-1.html"
   :route/media-type "text/html"})

(defn- response
  [status media-type body]
  (js/Response. body #js {:status status
                          :headers #js {"content-type" media-type}}))

(defn- with-fetch!
  [native-response requests thunk]
  (let [previous-fetch js/fetch]
    (set! js/fetch
          (fn [path options]
            (swap! requests conj {:path path
                                 :cache (.-cache options)
                                 :credentials (.-credentials options)})
            (js/Promise.resolve native-response)))
    (try
      (-> (thunk)
          (.finally #(set! js/fetch previous-fetch)))
      (catch :default err
        (set! js/fetch previous-fetch)
        (js/Promise.reject err)))))

(defn- complete!
  [done promise check]
  (-> promise
      (.then (fn [result] (check result) (done)))
      (.catch (fn [err] (is false (str err)) (done)))))

(deftest a-real-html-artifact-is-loaded-with-its-body
  (async done
         (let [body "<article class=\"published-document\"><p>CMS demo</p></article>"
               requests (atom [])]
           (complete!
            done
            (with-fetch! (response 200 "text/html; charset=utf-8" body)
                         requests #(artifact/load! route))
            (fn [result]
              (is (= {:artifact/status :ok :artifact/body body
                      :artifact/media-type "text/html"} result))
              (is (= [{:path "/published/artifacts/notes/demo/en/rev-1.html"
                       :cache "no-store" :credentials "omit"}]
                     @requests)))))))

(deftest an-html-fallback-for-the-manifest-is-still-absent
  (async done
         (let [requests (atom [])]
           (complete!
            done
            (with-fetch! (response 200 "text/html; charset=utf-8"
                                   "<!doctype html><html><body>SPA shell</body></html>")
                         requests manifest/load!)
            (fn [result]
              (is (= :ok (:published/status result)))
              (is (= :absent (:published/source result)))
              (is (= [] (:published/routes result)))
              (is (= "/published/manifest.edn" (:path (first @requests)))))))))

(deftest a-missing-html-artifact-remains-missing
  (async done
         (complete!
          done
          (with-fetch! (response 404 "text/html" "<html>Not found</html>")
                       (atom []) #(artifact/load! route))
          (fn [result]
            (is (= :missing (:artifact/status result)))))))

(deftest artifact-path-validation-refuses-cross-origin-and-traversal-requests
  (async done
         (let [requests (atom [])]
           (complete!
            done
            (with-fetch!
              (response 200 "text/html" "<p>Must not be fetched</p>")
              requests
              #(js/Promise.all
                (into-array
                 (map (fn [path] (artifact/load! (assoc route :route/artifact path)))
                      ["https://knoxx.internal/x.html" "//knoxx.internal/x.html"
                       "artifacts/../../secret.html"]))))
            (fn [results]
              (is (every? #(= :error (:artifact/status %)) (array-seq results)))
              (is (empty? @requests)))))))
