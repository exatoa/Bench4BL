# Locus

You can run Locus using command line: `java main.Main [config]`
It reads all the configurations from the [config] file.
[config] is optional, which refers to `config_local.txt` under the project folder by default.
You can specify the config file to any file, which should have the same format as `confit_local.txt`

The reauired configurations are as followings:

1. `task`: the task you want to run `{indexHunks, corpusCreation, produceChangeResults, produceFileResults, all}`

    * indexHunks: extract the concerned changes
    * corpusCreation: create the corpus for bug reports, change logs, and changes [must be executed after the first step]
    * produceChangeResults: produce the results at the change level, the results will be put into file `changeLevelResults.txt` [must be executed after the first two steps]
    * produceFileResults: produce the results at the file level, the results will be put into file `fileLevelResults.txt` [must be executed after the first two steps]
    * all: conduct all the previous step

2. `repoDir`: which refers to the repository of your target project. [supports GIT right now]
3. `sourceDir`: which is the target version of the source files
4. `workingLoc`: the working directory of the current run, which will store all the intermediate files and the final results
5. `bugReport`: specify the bug report file
6. `changeOracle`: **Optional**, which is only required when you need to produce the results at the change level
