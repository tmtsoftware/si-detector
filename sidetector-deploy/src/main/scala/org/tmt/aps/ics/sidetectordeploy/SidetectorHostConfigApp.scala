package org.tmt.aps.ics.sidetectordeploy

import csw.framework.deploy.hostconfig.HostConfig

object SidetectorHostConfigApp extends App {

  HostConfig.start("sidetector-host-config-app", args)

}
