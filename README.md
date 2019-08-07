# Bench4BL
Bench4BL is a collection of bug reports and corresponding source code files to fix a bug specified by each bug report to support bug localization research. This collection contains 10,017 bug reports collected from 51 open source projects, and each bug report is mapped with the source code files of the corresponding version. Therefore, this dataset can help researchers and practitioners evaluate bug localization techniques with a large number of subjects.
This document describes how to use this dataset and how to reproduce the result of our paper below. Please cite the following paper if you utilize the dataset:


    @inproceedings{bench4bl,
      Author = {Jaekwon Lee and Dongsun Kim and Tegawend\'e F. Bissyand\'e and Woosung Jung and Yves Le Traon},
      Title = {Bench4BL: Reproducibility Study of the Performance of IR-based Bug Localization},
      Booktitle = {Proceedings of the 27th ACM SIGSOFT International Symposium  on  Software Testing and Analysis},
      Series = {ISSTA 2018},
      Year = {2018},
      doi = {10.1145/3213846.3213856},
      pages = {1--12}
    }



### Subjects ( Bug reports and Source Code Repositories )
The following table shows five old subjects that used in previous studies and 46 new subjects that we newly collected.
The subjects are classified into six groups (the five subjects used in previous studies are grouped as "Old subjects").
Each subject consists of bug reports, bug report repositories that we refined, cloned git repository, and metadata of them that we curated. If you need a recent git repository, please clone the repository again by using a link in the "Git Repository" column.
Please refer to "Getting Started" for more details.


| Group                  | Subject | Archive       | Git Repository                           |
|:-----------------------|:--------|:-----------------------|:-----------------------------------------|
| Apache | CAMEL | [CAMEL.tar](https://sourceforge.net/projects/irblsensitivity/files/Apache/CAMEL.tar) | [https://github.com/apache/camel.git](https://github.com/apache/camel.git) |
| Apache | HBASE | [HBASE.tar](https://sourceforge.net/projects/irblsensitivity/files/Apache/HBASE.tar) | [https://github.com/apache/hbase.git](https://github.com/apache/hbase.git) |
| Apache | HIVE | [HIVE.tar](https://sourceforge.net/projects/irblsensitivity/files/Apache/HIVE.tar) | [https://github.com/apache/hive.git](https://github.com/apache/hive.git) |
| Commons | CODEC | [CODEC.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/CODEC.tar) | [https://github.com/apache/commons-codec.git](https://github.com/apache/commons-codec.git) |
| Commons | COLLECTIONS | [COLLECTIONS.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/COLLECTIONS.tar) | [https://github.com/apache/commons-collections.git](https://github.com/apache/commons-collections.git) |
| Commons | COMPRESS | [COMPRESS.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/COMPRESS.tar) | [https://github.com/apache/commons-compress.git](https://github.com/apache/commons-compress.git) |
| Commons | CONFIGURATION | [CONFIGURATION.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/CONFIGURATION.tar) | [https://github.com/apache/commons-configuration.git](https://github.com/apache/commons-configuration.git) |
| Commons | CRYPTO | [CRYPTO.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/CRYPTO.tar) | [https://github.com/apache/commons-crypto.git](https://github.com/apache/commons-crypto.git) |
| Commons | CSV | [CSV.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/CSV.tar) | [https://github.com/apache/commons-csv.git](https://github.com/apache/commons-csv.git) |
| Commons | IO | [IO.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/IO.tar) | [https://github.com/apache/commons-io.git](https://github.com/apache/commons-io.git) |
| Commons | LANG | [LANG.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/LANG.tar) | [https://github.com/apache/commons-lang.git](https://github.com/apache/commons-lang.git) |
| Commons | MATH | [MATH.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/MATH.tar) | [https://github.com/apache/commons-math.git](https://github.com/apache/commons-math.git) |
| Commons | WEAVER | [WEAVER.tar](https://sourceforge.net/projects/irblsensitivity/files/Commons/WEAVER.tar) | [https://github.com/apache/commons-weaver.git](https://github.com/apache/commons-weaver.git) |
| JBoss | ENTESB | [ENTESB.tar](https://sourceforge.net/projects/irblsensitivity/files/JBoss/ENTESB.tar) | [https://github.com/jboss-fuse/fuse.git](https://github.com/jboss-fuse/fuse.git) |
| JBoss | JBMETA | [JBMETA.tar](https://sourceforge.net/projects/irblsensitivity/files/JBoss/JBMETA.tar) | [https://github.com/jboss/metadata.git](https://github.com/jboss/metadata.git) |
| Wildfly | ELY | [ELY.tar](https://sourceforge.net/projects/irblsensitivity/files/Wildfly/ELY.tar) | [https://github.com/wildfly-security/wildfly-elytron.git](https://github.com/wildfly-security/wildfly-elytron.git) |
| Wildfly | SWARM | [SWARM.tar](https://sourceforge.net/projects/irblsensitivity/files/Wildfly/SWARM.tar) | [https://github.com/wildfly-swarm/wildfly-swarm.git](https://github.com/wildfly-swarm/wildfly-swarm.git) |
| Wildfly | WFARQ | [WFARQ.tar](https://sourceforge.net/projects/irblsensitivity/files/Wildfly/WFARQ.tar) | [https://github.com/wildfly/wildfly-arquillian.git](https://github.com/wildfly/wildfly-arquillian.git) |
| Wildfly | WFCORE | [WFCORE.tar](https://sourceforge.net/projects/irblsensitivity/files/Wildfly/WFCORE.tar) | [https://github.com/wildfly/wildfly-core.git](https://github.com/wildfly/wildfly-core.git) |
| Wildfly | WFLY | [WFLY.tar](https://sourceforge.net/projects/irblsensitivity/files/Wildfly/WFLY.tar) | [https://github.com/wildfly/wildfly.git](https://github.com/wildfly/wildfly.git) |
| Wildfly | WFMP | [WFMP.tar](https://sourceforge.net/projects/irblsensitivity/files/Wildfly/WFMP.tar) | [https://github.com/wildfly/wildfly-maven-plugin.git](https://github.com/wildfly/wildfly-maven-plugin.git) |
| Spring | AMQP | [AMQP.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/AMQP.tar) | [https://github.com/spring-projects/spring-amqp](https://github.com/spring-projects/spring-amqp) |
| Spring | ANDROID | [ANDROID.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/ANDROID.tar) | [https://github.com/spring-projects/spring-android](https://github.com/spring-projects/spring-android) |
| Spring | BATCH | [BATCH.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/BATCH.tar) | [https://github.com/spring-projects/spring-batch](https://github.com/spring-projects/spring-batch) |
| Spring | BATCHADM | [BATCHADM.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/BATCHADM.tar) | [https://github.com/spring-projects/spring-batch-admin](https://github.com/spring-projects/spring-batch-admin) |
| Spring | DATACMNS | [DATACMNS.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/DATACMNS.tar) | [https://github.com/spring-projects/spring-data-commons](https://github.com/spring-projects/spring-data-commons) |
| Spring | DATAGRAPH | [DATAGRAPH.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/DATAGRAPH.tar) | [https://github.com/spring-projects/spring-data-neo4j](https://github.com/spring-projects/spring-data-neo4j) |
| Spring | DATAJPA | [DATAJPA.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/DATAJPA.tar) | [https://github.com/spring-projects/spring-data-jpa](https://github.com/spring-projects/spring-data-jpa) |
| Spring | DATAMONGO | [DATAMONGO.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/DATAMONGO.tar) | [https://github.com/spring-projects/spring-data-mongodb](https://github.com/spring-projects/spring-data-mongodb) |
| Spring | DATAREDIS | [DATAREDIS.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/DATAREDIS.tar) | [https://github.com/spring-projects/spring-data-redis](https://github.com/spring-projects/spring-data-redis) |
| Spring | DATAREST | [DATAREST.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/DATAREST.tar) | [https://github.com/spring-projects/spring-data-rest](https://github.com/spring-projects/spring-data-rest) |
| Spring | LDAP | [LDAP.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/LDAP.tar) | [https://github.com/spring-projects/spring-ldap](https://github.com/spring-projects/spring-ldap) |
| Spring | MOBILE | [MOBILE.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/MOBILE.tar) | [https://github.com/spring-projects/spring-mobile](https://github.com/spring-projects/spring-mobile) |
| Spring | ROO | [ROO.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/ROO.tar) | [https://github.com/spring-projects/spring-roo](https://github.com/spring-projects/spring-roo) |
| Spring | SEC | [SEC.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SEC.tar) | [https://github.com/spring-projects/spring-security](https://github.com/spring-projects/spring-security) |
| Spring | SECOAUTH | [SECOAUTH.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SECOAUTH.tar) | [https://github.com/spring-projects/spring-security-oauth](https://github.com/spring-projects/spring-security-oauth) |
| Spring | SGF | [SGF.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SGF.tar) | [https://github.com/spring-projects/spring-data-gemfire](https://github.com/spring-projects/spring-data-gemfire) |
| Spring | SHDP | [SHDP.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SHDP.tar) | [https://github.com/spring-projects/spring-hadoop](https://github.com/spring-projects/spring-hadoop) |
| Spring | SHL | [SHL.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SHL.tar) | [https://github.com/spring-projects/spring-shell](https://github.com/spring-projects/spring-shell) |
| Spring | SOCIAL | [SOCIAL.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SOCIAL.tar) | [https://github.com/spring-projects/spring-social](https://github.com/spring-projects/spring-social) |
| Spring | SOCIALFB | [SOCIALFB.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SOCIALFB.tar) | [https://github.com/spring-projects/spring-social-facebook](https://github.com/spring-projects/spring-social-facebook) |
| Spring | SOCIALLI | [SOCIALLI.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SOCIALLI.tar) | [https://github.com/spring-projects/spring-social-linkedin](https://github.com/spring-projects/spring-social-linkedin) |
| Spring | SOCIALTW | [SOCIALTW.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SOCIALTW.tar) | [https://github.com/spring-projects/spring-social-twitter](https://github.com/spring-projects/spring-social-twitter) |
| Spring | SPR | [SPR.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SPR.tar) | [https://github.com/spring-projects/spring-framework](https://github.com/spring-projects/spring-framework) |
| Spring | SWF | [SWF.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SWF.tar) | [https://github.com/spring-projects/spring-webflow](https://github.com/spring-projects/spring-webflow) |
| Spring | SWS | [SWS.tar](https://sourceforge.net/projects/irblsensitivity/files/Spring/SWS.tar) | [https://github.com/spring-projects/spring-ws](https://github.com/spring-projects/spring-ws) |
| Previous | AspectJ | [AspectJ.tar](https://sourceforge.net/projects/irblsensitivity/files/Previous/AspectJ.tar) | [https://github.com/eclipse/org.aspectj](https://github.com/eclipse/org.aspectj) |
| Previous | JDT | [JDT.tar](https://sourceforge.net/projects/irblsensitivity/files/Previous/JDT.tar) | [https://github.com/eclipse/eclipse.jdt.core](https://github.com/eclipse/eclipse.jdt.core) |
| Previous | PDE | [PDE.tar](https://sourceforge.net/projects/irblsensitivity/files/Previous/PDE.tar) | [https://github.com/eclipse/eclipse.pde.ui](https://github.com/eclipse/eclipse.pde.ui) |
| Previous | SWT | [SWT.tar](https://sourceforge.net/projects/irblsensitivity/files/Previous/SWT.tar) | [https://github.com/eclipse/eclipse.platform.swt](https://github.com/eclipse/eclipse.platform.swt) |
| Previous | ZXing | [ZXing.tar](https://sourceforge.net/projects/irblsensitivity/files/Previous/ZXing.tar) | [https://github.com/zxing/zxing](https://github.com/zxing/zxing) |


### Repository Directory Structure
 - **techniques**: This folder includes source code and executable files of previous techniques such as BugLocator and Locus. We revised the source code files so that every technique produces results with the identical format. All executable files are stored in the folder "techniques/releases"
 - **analysis**: The execution result of previous techniques, which are refind for scripts in forlder "scripts > analysis".
 - **scripts**: Python scripts to prepare resources for bug localization experiments and to execute previous techniques.
 - **packing.sh**: Shell script to pack resources for each subject.
 - **unpacking.sh**: Shell script to unpack resources for each subject.




# Getting Started
This section describes all procedures of using this benchmarks. The procedures include setting experiment environments, creating a bug repository and checking out source code files of a specific version. The step of creating a bug repository can be skipped if you use archives listed in the above table.
All the procedures are tested on Ubuntu 16.04 LTS.


    ## Scripts Directory Structure ##
    - repository: Scripts to prepare the resources to execute each technique.
    - results: Scripts to collect the execution results of each technique and export to Excel.
    - analysis: Scripts to analysis for the result of each technique and features extracted from resources.
                 We applied Mann-Whitney U test, Pearson correlation and so on.
    - commons: Scripts to managing subjects and common functions.
    - utils: Personal libraries for experiments.


### Clone this repository
Clone the repository by using the following command.
> $ git clone https://github.com/exatoa/Bench4BL.git

If you don't have git, please install git first using following commands.
> $ sudo apt-get update <br />
> $ sudo apt-get install git <br />

    
### Download subjects' archives.
Download all subjects from the Subjects table and save them in the cloned repository path. We saved them into the 'Bench4BL/_archives' directory. To use our scripts, we recommend that each subject stores in the group directory to which it belongs. After downloaded, unpack all archives by using the unpacking.sh script. If you don't need all subjects, you can download some of them.
> $ cd Bench4BL <br />
> Bench4BL$ mkdir _archives <br />
> Bench4BL$ cd _archives <br />
> Bench4BL/_archives$ mkdir Apache <br /> 
> Bench4BL/_archives$ cd Apache <br />
> Bench4BL/_archives/Apache$ wget -O CAMEL.tar "https://sourceforge.net/projects/irblsensitivity/files/Apache/CAMEL.tar" <br />
> ....work recursively.... <br />
> Bench4BL$ mkdir data <br />
> Bench4BL$ chmod +x unpacking.sh <br />
> Bench4BL$ ./unpacking.sh _archives data

The last command unpacks all archive files in '_archives' folder into 'data' folder as keeping the directory structures in '_archives'.

We appended the script to download all archives to the '_archives' folder. If you want to use this, please use following instructions. This scripts creats all folders and download archives into each folder.
> Bench4BL$ chmod +x downloads.sh <br />
> Bench4BL$ ./downloads.sh


### Install python
We used python 2.7. (If you have python 2.7 in your computer, please skip this section.)
> $ sudo add-apt-repository ppa:fkrull/deadsnakes <br />
> $ sudo apt-get update <br />
> $ sudo apt-get install python2.7 python <br />
> $ sudo apt-get install python-pip <br />

### Install python libraries
We have 8 dependencies below:

    bs4 >= 0.0.1
    matplotlib >= 2.0.1
    numpy >= 1.13.3
    scipy >= 0.19.1
    python-dateutil >= 2.6.1
    pytz >= 2017.3
    GitPython >= 2.1.5
    XlsxWriter >= 0.9.8

You can install using following commnad.
> $ sudo pip install --upgrade pip <br />
> $ pip install numpy scipy matplotlib pytz GitPython bs4 xlswriter python-dateutil<br />
> or<br />
> $ pip install -r scripts/requirements.txt<br />
> we serve the requirements file.<br />

### Update PATH information (Editing script code)
In the file 'Bench4BL/scripts/commons/Subject.py', there are variables that stores a resource PATH information as a string and subject informations. To use our scripts, you should change the variables properly. You should use absolute PATH to update the PATH information and use the same subject name with subject Directory name for the subject information.

    class Subjects(object):
        ...
        root = u'/mnt/exp/Bench4BL/data/'
        root_result = u'/mnt/exp/Bench4BL/expresults/'
        techniques = ['BugLocator', 'BRTracer', 'BLUiR', 'AmaLgam', 'BLIA', 'Locus']
        groups = ['Apache', 'Commons', 'JBoss', 'Wildfly', 'Spring']
        projects = {
            'Apache':[u'CAMEL', u'HBASE', u'HIVE'],
            'Commons':[u'CODEC', u'COLLECTIONS', u'COMPRESS', u'CONFIGURATION', u'CRYPTO', u'IO', u'LANG', u'MATH', u'WEAVER',u'CSV'],
            'JBoss':[u'ENTESB', u'JBMETA'],
            'Wildfly':[u'ELY', u'WFARQ', u'WFCORE', u'WFLY', u'WFMP',u'SWARM'],
            'Spring':[U'AMQP', U'ANDROID', U'BATCH', U'BATCHADM', U'DATACMNS', U'DATAGRAPH', U'DATAJPA', U'DATAMONGO', U'DATAREDIS', U'DATAREST', U'LDAP', U'MOBILE', U'ROO', U'SEC', U'SECOAUTH', U'SGF', U'SHDP', U'SHL', U'SOCIAL', U'SOCIALFB', U'SOCIALLI', U'SOCIALTW', U'SPR', U'SWF', U'SWS']
        }
        ...

* root : The directory that you unpacked downloaded archives.
* root_result : The directory that the previous techniques' result will be stored.
* techniques : The list of previous technique names.
* groups : The list of group names that you want to test.
* projects : The list of subject names that you want to test. Each subject name should be classified into specific group name.


### Version Information
We selected specific versions for each subject and saved into 'versions.txt'. The file is in JSON format and we used a dictionary to save information. Top-level keys mean a subject name corresponding to "Subjects.py". The selected versions are also listed in dictionary structure. The key text is version name which means you want to represent it and the value test is tag name written in git repository.
For example, assume that you want to store CODEC Subject's version information. You prepare JSON code and save it in 'Bench4BL/data/Commons/CODEC/versions.txt'. We offer the selected versions in the archieves. If you want to use a version that we selected, it is not necessary to change version information files.

    {
        "CODEC":{
                "1.4":"CODEC_1_4",
                "1.5":"commons-codec-1.5",
                "1.6":"1_6",
                "1.7":"1.7",
                "1.1":"CODEC_1_1",
                "1.2":"CODEC_1_2",
                "1.3":"CODEC_1_3",
                "1.8":"1.8",
                "1.9":"1.9",
                "1.10":"1.10"
        }
    }


### Inflate the source codes.
We used multiple versions of source code for the experiment. Since the provided archives have only a git repository, you need to check out repositories according to versions that you selected above. The script 'launcher_GitInflator.py' clones a git repositories and checks it out into the multiple versions which you selected. These source codes are stored into a folder 'Bench4BL/data/[Group Name]/[Project Name]/sources/' automatically.
> Bench4BL/scripts$ python launcher_GitInflator.py <br />

    
### Build bug repositories
We need to build a repository for the bug reports with pre-crawled bug reports. The bug repository is in XML format and includes bug data which is used in the experiments. The 'launcher_repoMaker.py' makes the bug repository that containing entire crawled bug reports information and bug repositories that stores bug reports according to the mapped version. But, since we already offer the result of this step in provided subject's archives, use this script if you want to update the bug repositories. The 'launcher_DupRepo.py' creates a bug repository file that contains bug information merged duplicate bug reports.

> Bench4BL/scripts$ python launcher_repoMaker.py <br />
> Bench4BL/scripts$ python launcher_DupRepo.py <br />

    
### Update count information of bug and source codes.
The script of Counting.py makes a count information for bug and source code. The result will be stored 'bugs.txt', 'sources.txt' and 'answers.txt' in each subject's folder.
> Bench4BL/scripts$ python Counting.py <br />


# Execute Previous Techniques
To get the result of each technique, you can use 'Bench4BL/scripts/launcher_Tool.py'. The script executes 6 techniques for all subjects.
The script basically works for the multiple versions of bug repository and each of the related source codes. We explain what you need to run the tool first and describe the tool usage.

### Install Java
All previous techniques are executed in Java Runtime Environment. If you have java in your computer, please skip this section.

> $ sudo apt-get install python-software-properties <br />	
> $ sudo add-apt-repository ppa:webupd8team/java <br />	
> $ sudo apt-get update <br />
> $ sudo apt-get install openjdk-8-jre-headless <br />

### Install indri
- To execute BLUiR and AmaLgam, you need to install indri.
- Since there are compile problems, we chose indri-5.6 version.
- In the installing process, please memorize the path in the first line in the "make install" log. <br />
(In my case, /usr/local/bin.  This is the installed path of indri)
- And then, Change Settings.txt file.
- Commands to install indri
> // Install g++ and make for indri <br />
> $ sudo add-apt-repository ppa:ubuntu-toolchain-r/test <br />
> $ sudo apt-get update <br />
> $ sudo apt-get install g++ <br />
> $ sudo apt-get install make <br />
> $ sudo apt-get install --reinstall zlibc zlib1g zlib1g-dev <br />
> <br />
> // download and install indri (If you faced an error in the compiling, please try with another version.)<br />
> $ wget https://downloads.sourceforge.net/project/lemur/lemur/indri-5.6/indri-5.6.tar.gz <br />
> $ tar -xzf indri-5.6.tar.gz <br />
> $ cd indri-5.6 <br />
> $ ./configure <br />
> $ make <br />
> $ sudo make install <br />
>    /usr/bin/install -c -m 755 -d /usr/local/bin <br />
>    /usr/bin/install -c -m 755 -d /usr/local/include <br />
>    /usr/bin/install -c -m 755 -d /usr/local/include/indri <br />
>    ... <br />
>    ... <br />
>    /usr/bin/install -c -m 644 Makefile.app /usr/local/share/indri <br />
>  <br />
> // changeSettings.txt file <br />
> $ cd ~/Bench4BL/techniques/releases &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;// We assume you cloned our repository to  <br />
> $ vi Settings.txt <br />
> &nbsp; &nbsp; indripath=/usr/local/bin/ &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<-- edit this value as a the first log of "make install" <br />
>

### Usage of launcher_Tool.py
* Preparing step
    - You need to set the PATHs and JavaOptions in the launcher_Tool.py file.
    - Open the file, launcher_Tool.py and check the following variables 
    - ProgramPATH: Set the directory path which contains the release files of the IRBL techniques. (ex. u'~/Bench4BL/techniques/releases/')
    - OutputPATH: Set the result path to save output of each technique (ex. u'~/Bench4BL/expresults/')
    - JavaOptions: Set the java command options. (ex. '-Xms512m -Xmx4000m')
    - JavaOptions_Locus: Set the java options for Locus. Because Locus need a large memory, we separated the option. (ex. '-Xms512m -Xmx8000m')
* Options
    - -w <work name>: \[necessary\] With this option, users can set the ID for each experiment, and each ID is also used as a directory name to store the execution results of each Technique. Additionally, if the name starts with "Old", this script works for the previous data, otherwise works for the new data.
    - -g <group name>: A specific group. With this option, the script works for the subjects in the specified group. 
    - -p <subject name>: A specific subject. To use this option, you should specify the group name. 
    - -t <technique name>: A specific technique. With this option, the script makes results of specified technique.
    - -v <version name>: A specific version. With this option, the script works for the specified version of source code.
    - -s: Single version mode, With this option, the script works for the only latest source code.
    - -m: With this option, the bug repositories created by combining the text of duplicate bug report pairs are used instead of the normal one.


* Examples
> Bench4BL/scripts$ python launcher_Tool.py -w ExpFirst <br />
> Bench4BL/scripts$ python launcher_Tool.py -w ExpFirst -s <br />
> Bench4BL/scripts$ python launcher_Tool.py -w ExpFirst_Locus -t Locus <br />
> Bench4BL/scripts$ python launcher_Tool.py -w ExpFirst_CAMLE -g Apache -p CAMEL <br />



# Previous Techniques Load on Eclipse
We changed previous techniques on Eclipse. But we didn't include eclipse environment files (.metadata folder, .project and .classpath file) in each previous techniques folders.
 
So, If you want to load these techniques on Eclipse, please follow next sequence.
 
 - Open Eclipse
 - Make a 'techniques' folder into workplace of Eclipse. Then .metadata folder will be created in 'techniques' folder.
 - On the 'Package Explorer' panel, Open context menu by clicking right mouse button.
 - Select 'Import', Then a pop-up windows will be placed.
 - Except BLUiR project,  choose 'General > Projects from Folder or Archive' item and click 'Next' button.
 - Designate project folder in 'techniques' and click 'Finish' button.
 - Then, the project will be loaded and be shown in the Package Explorer.
 - BLUiR is made as Maven project. So, You should import with 'Maven > Existing Maven Project'. And then, just choose project folder. You don't need to change any other options.
 - Especially BLIA project, need to add library JUnit.



