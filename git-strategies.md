**GOAL:** To provide an non-intrusive way of hosting a flat-file database of tasks alongside a git repository.
1) Should not require the user to manually resolve conflicts.
2) Should not edit the repository history.

## Method A
Commandline examples given

Merge remote changes without committing

`git pull --strategy=recursive -Xtheirs --no-commit --squash origin master`

 Remove all changed files other than `.taskbook`
 
`TODO:`

Commit changes silently without editing history.

`git commit --amend`


## Method B
Using JGit.

Use the porcelain diff command and resolve conflicts manually. If the flat-file format is a .csv this should be fairly
easy to do.