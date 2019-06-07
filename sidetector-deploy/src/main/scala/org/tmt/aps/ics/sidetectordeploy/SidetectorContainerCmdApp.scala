package org.tmt.aps.ics.sidetectordeploy

import csw.framework.deploy.containercmd.ContainerCmd

object SidetectorContainerCmdApp extends App {

  ContainerCmd.start("sidetector-container-cmd-app", args)

}
