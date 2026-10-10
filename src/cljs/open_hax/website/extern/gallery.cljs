(ns open-hax.website.extern.gallery
  "Browser input and modal focus behavior, kept outside gallery decisions.")

(defn event-value
  [event]
  (.. event -target -value))

(defn backdrop-event?
  [event]
  (= (.-target event) (.-currentTarget event)))

(defn activate-dialog!
  "Focus the dialog, contain Tab/Escape, lock scrolling, and return cleanup.

  The view supplies only an element id and a close callback. DOM nodes remain
  in this adapter, including the trigger whose focus is restored on close."
  [element-id on-close]
  (if-let [dialog (.getElementById js/document element-id)]
    (let [previous-focus (.-activeElement js/document)
          previous-overflow (.. js/document -body -style -overflow)
          focusable (fn []
                      (array-seq
                       (.querySelectorAll
                        dialog
                        "button:not([disabled]), a[href], input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex='0']")))
          handler (fn [event]
                    (case (.-key event)
                      "Escape" (do (.preventDefault event) (on-close))
                      "Tab" (let [elements (focusable)
                                  first-element (first elements)
                                  last-element (last elements)
                                  active (.-activeElement js/document)
                                  backwards? (.-shiftKey event)]
                              (cond
                                (empty? elements)
                                (do (.preventDefault event) (.focus dialog))

                                (or (not (.contains dialog active))
                                    (and backwards? (= active dialog))
                                    (and backwards? (= active first-element))
                                    (and (not backwards?) (= active last-element)))
                                (do (.preventDefault event)
                                    (.focus (if backwards? last-element first-element)))))
                      nil))]
      (set! (.. js/document -body -style -overflow) "hidden")
      (.addEventListener js/document "keydown" handler)
      (.focus (or (first (focusable)) dialog))
      (fn []
        (.removeEventListener js/document "keydown" handler)
        (set! (.. js/document -body -style -overflow) previous-overflow)
        (when (and previous-focus (.-isConnected previous-focus))
          (.focus previous-focus))))
    (fn [] nil)))
