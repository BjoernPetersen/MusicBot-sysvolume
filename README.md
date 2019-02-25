# MusicBot-sysvolume [![GitHub (pre-)release](https://img.shields.io/github/release/BjoernPetersen/MusicBot-sysvolume/all.svg)](https://github.com/BjoernPetersen/MusicBot-sysvolume/releases) [![CircleCI branch](https://img.shields.io/circleci/project/github/BjoernPetersen/MusicBot-sysvolume/master.svg)](https://circleci.com/gh/BjoernPetersen/MusicBot-sysvolume/tree/master) [![GitHub license](https://img.shields.io/github/license/BjoernPetersen/MusicBot-sysvolume.svg)](https://github.com/BjoernPetersen/MusicBot-sysvolume/blob/master/LICENSE)

Provides system master volume control by calling CLI commands.

## Config

The CLI commands to access and change the master volume are highly system dependent.
You need to provide the commands and an extraction regex pattern in order for this plugin
to work.

### Windows example

Windows doesn't have a reasonable built-in CLI interface to control the master volume.
It is recommended to install the [`setvol`](https://www.rlatour.com/setvol/) tool by 
Rob Latour and add it to your `PATH`.

The config should then look like this:

| key        | value
| ---------- | -----
| valueMode  | `Percent`
| getCommand | `setvol report`
| getPattern | `Volume = (\d+)`
| setCommand | `setvol <volume>`

### Linux/ALSA

| key        | value
| ---------- | -----
| valueMode  | `Percent`
| getCommand | `amixer sget 'Master'`
| getPattern | `TODO`
| setCommand | `amixer sset 'Master' <volume>%`

### Linux/PulseAudio

| key        | value
| ---------- | -----
| valueMode  | `Percent`
| getCommand | `TODO`
| getPattern | `TODO`
| setCommand | `pactl set-sink-volume 0 <volume>%`
