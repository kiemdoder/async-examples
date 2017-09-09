(ns async-examples.core
  (:require [clojure.core.async :refer [admix alt! chan close! mix sliding-buffer timeout unmix go go-loop put! <!]]
            clj-tuple))

(def events [:event1 :event2 :event3])

(defn event-generator [id cancel-chan]
  (let [c (chan 1)]
    (go-loop [timeout-chan (timeout (rand-int 10000))]
      (alt!
        timeout-chan (do
                       (put! c (clj-tuple/vector id (rand-nth events)))
                       (recur (timeout (rand-int 10000))))
        cancel-chan (println "cancel chan closed")))
    c))

(defn consume-chan [ch]
  (go-loop []
    (if-let [v (<! ch)]
      (do
        (println "->" v)
        (recur))
      (println "chan closed"))))

(defn create-mix []
  (let [cancel-chan (chan)
        output-chan (chan 5)
        mix-out (mix output-chan)
        event-gen-channels (map #(event-generator % cancel-chan) (range 5))
        ]))

(comment
  (do
    (def cancel (chan 1))
    (consume-chan (event-generator 1 cancel)))

  )
