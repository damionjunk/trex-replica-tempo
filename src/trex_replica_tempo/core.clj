(ns trex-replica-tempo.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
  (:import [javax.sound.midi MidiSystem ShortMessage])
  (:gen-class))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; MIDI
;;

(defn get-midi-devices
  "Returns a list of hashes with info about the midi devices available."
  []
  (reduce
    (fn [m device]
      (assoc m (.getName device)
               {:name        (.getName device)
                :vendor      (.getVendor device)
                :version     (.getVersion device)
                :description (.getDescription device)
                :device      (. MidiSystem getMidiDevice device)}))
    {}
    (. MidiSystem getMidiDeviceInfo)))

(defn bpm-sleep
  "Returns how many ms to sleep between beats given `bpm`."
  [bpm]
  (/ 1000 (/ bpm 60)))

(defn tap-tempo
  "The T-Rex Replica listens for two 'taps' on CC 20."
  [bpm device]
  (println "Setting T-Rex Replica to" bpm "BPM.")
  (try
    (.open device)
    (.send (.getReceiver device) (ShortMessage. ShortMessage/CONTROL_CHANGE 0 20 0) -1)
    (Thread/sleep (bpm-sleep bpm))
    (.send (.getReceiver device) (ShortMessage. ShortMessage/CONTROL_CHANGE 0 20 0) -1)
    (.close device)
    (catch Exception e
      (println "Failure to set BPM. Check your MIDI device."))))


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CLI Stuff
;;

(def cli-options
  [["-l" "--list" "List MIDI devices"]
   ["-d" "--device DEVICE" "Device name"]
   ["-b" "--bpm BPM" "Beats per minute"
    :default 120
    :parse-fn #(Integer/parseInt %)
    :validate [#(and (pos? %) (< % 2000)) "Must be a number between 1 and 2000"]]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["T-Rex Tap Tempo over MIDI"
        ""
        "Usage: java -jar replica-bpm.jar [options]"
        ""
        "Example:"
        "java -jar replica-bpm.jar -d MIDI -b 120"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn devices-list [devices]
  (str "The following MIDI devices are available:\n\n"
    (string/join \newline
                 (map #(str (:name %) " : " (:description %)) (vals devices)))))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; MAIN!
;;

(defn -main [& args]
  (let [devices (get-midi-devices)
        {:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (:list options) (exit 0 (devices-list devices))
      errors (exit 1 (error-msg errors))
      (and (:bpm options)
           (:device options))
      (tap-tempo (:bpm options) (:device (get devices (:device options))))
      :else (exit 1 (usage summary)))))