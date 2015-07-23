# trex-replica-tempo

A Clojure command line utility to set the tempo on the T-Rex Replica
delay [pedal](http://www.t-rex-effects.com/replica/).

## Usage

```
lein uberjar

java -jar target/replica-bpm.jar -b 120 -d MIDI
```

Set the BPM to 120 using a Midi device called *MIDI*.

To see all available Midi devices:

```
java -jar replica-bpm.jar -l
```

Results will vary, this is my system:

```
The following MIDI devices are available:

Gervill : Software MIDI Synthesizer
MIDI : Pro40 MIDI
Real Time Sequencer : Software sequencer
```

## License

Copyright © 2015 Damion Junk

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
