# mdbot
Telegram Bot for Ingress Mission Day to be held 11 August 2018 in Dnipro, Ukraine.
Maintained by @mihmax.

# Main functions

* Trilingual
* Two basic screens:
** Main screen with text
** Missions screen
* Autodetect user language, allow change
* Stores basic information about the user
* List of missions with maps
** Since v6 with fast navigation

# Technical trivia

* To build, run `./build.sh`, results will be in `out/artifacts/mdbot_jar`
* Bot runs in long-polling mode (no public IP required)
* Written in Groovy (bundles Groovy 2.5.2 runtime)
* Known to compile on Java 8 (did not try other versions)
* Runs on Java 8 (Chronicle Map does not work on Java 10)

# TODO

* Read all content (no hardcoded content)
* Robust storage (currently Chronicle Maps is used for multiple String - String maps)
** Part 1 - is Chronicle Maps really needed? (27MB or ~ 15% of libraries that do not run on Java 10)
** Part 2 - move all settings to one serializable class to store in 1 Chronicle Map
* Idea: Show global mission map when user opens Missions

# Release Notes

* Version 7 - post-Dnipro improvements
  * Alpha - Code Cleanup, Variable Number of missions
* Version 6 - removed QR code generation, **Final Version that was running during Dnipro Mission Day**
* Version 5 - final test
* Versions 0-4 - debug and experimentation