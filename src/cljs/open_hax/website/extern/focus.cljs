(ns open-hax.website.extern.focus
  "DOM focus transfer after an explicitly activated element mounts.")

(defn focus-connected!
  "A stable React ref callback; detach and disconnected nodes are inert."
  [element]
  (when (and element (.-isConnected element))
    (.focus element)))
