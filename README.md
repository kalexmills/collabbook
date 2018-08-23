# Collabbook
[Taskbook clone](https://github.com/klauscfhq/taskbook) written in Java using [picocli](https://picocli.info), and designed to play nicely with git.

### Features
* Collaboration with teammates done via a hidden .collabbook file which is meant to be tracked inside a git repository.
* When syncing commits with remote repository, updates to the task list are shared.
* Merge conflicts in the .collabbook file are avoided by a custom format designed to play nicely with on git's default merging schemes.
* Nearly all taskbook features are available from the commandline.

### Known Issues
* Unacceptable performance (~2 secs) due to JVM startup overhead.
  * A [golang](https://golang.org) port / rewrite is [in progress](https://github.com/kalexmills/collabbook-go), to enable the lightning-fast performance that comes from native compilation.
  * Integration with [nailgun](https://github.com/facebook/nailgun) or [drip](https://github.com/ninjudd/drip) are other options, for those who don't mind semi-persistent background JVMs.
