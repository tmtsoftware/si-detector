# sidetector

This project implements an HCD (Hardware Control Daemon) and an Assembly using 
TMT Common Software ([CSW](https://github.com/tmtsoftware/csw)) APIs. 

## Subprojects

* sidetector-assembly - an assembly that talks to the sidetector HCD
* sidetector-hcd - an HCD that talks to the sidetector hardware
* sidetector-deploy - for starting/deploying HCDs and assemblies

## Build Instructions

The build is based on sbt and depends on libraries generated from the 
[csw](https://github.com/tmtsoftware/csw) project.

Make sure build dependencies on csw-framework is version is CSW-0.7.0-RC1 for both HCD container and client, even though sbt build publishLocal for csw creates a 0.1.0-SNAPSHOT locally.

Then run:
sbt stage


## Prerequisites for running Components

The CSW services need to be running before starting the components. 
This is done by starting the `csw-services.sh` script, which is installed as part of the csw build.
If you are not building csw from the sources, you can get the script as follows:



 - Download csw-apps zip from https://github.com/tmtsoftware/csw/releases.
 - Unzip the downloaded zip.
 - set INTERFACE_NAME environment variable instead of relying on the “-i” command line argument to csw-services.sh
 - to set CLUSTER_SEEDS environment variable.
  
  - Go to the bin directory where you will find `csw-services.sh` script.
 - modify csw-services.sh version>Java 6 so that it handles versions > 9
 - Run `./csw_services.sh --help` to get more information.
 - Run `./csw_services.sh start` to start the location service and config server.





## Building the HCD and Assembly Applications

 - Run `sbt sidetector-deploy/universal:packageBin`, this will create self contained zip in `sidetector-deploy/target/universal` directory
 - Unzip the generated zip and cd into the bin directory

Note: An alternative method is to run `sbt stage`, which installs the applications locally in ./target/universal/stage/bin.

## Running the HCD and Assembly

Run the container cmd script with arguments. For example:

* Run the HCD in standalone mode with a local config file (The standalone config format is differennt than the container format):

```
./target/universal/stage/bin/sidetector-container-cmd-app --standalone --local ./src/main/resources/SampleHcdStandalone.conf
```

* Start the HCD and assembly in a container using the Java implementations:

```
sidetector-deploy/target/universal/stage/bin/sidetector-container-cmd-app --local sidetector-deploy/src/main/resources/JSidetectorContainer.conf
```

* Or the Scala versions:

```
./target/universal/stage/bin/sidetector-container-cmd-app --local ./src/main/resources/SampleContainer.conf
```
# si-detector
