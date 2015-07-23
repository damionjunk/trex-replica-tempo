(defproject trex-replica-tempo "0.1.0-SNAPSHOT"
  :description "Sets the temp on a T-Rex Replica pedal over MIDI."
  :url "https://github.com/damionjunk/trex-replica-tempo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :profiles {:uberjar {:aot :all}}
  :uberjar-name "replica-bpm.jar"
  :main trex-replica-tempo.core
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.cli "0.3.1"]])
