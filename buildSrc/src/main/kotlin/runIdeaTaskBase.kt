/*
 * Copyright (c) 2019-2022, JetBrains s.r.o. and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. JetBrains designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact JetBrains, Na Hrebenech II 1718/10, Prague, 14000, Czech Republic
 * if you need additional information or have any questions.
 */

import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import java.util.*

public fun Project.createRunIdeaTask(
  name: String,
  isAgent: Boolean,
  localProperties: Properties,
  configuration: JavaExec.() -> Unit,
) {

  val ideaPath = localProperties["projectorLauncher.ideaPath"] as? String
  println("----------- $name config ---------------")
  println("Idea path: $ideaPath")
  println("------------------------------------------------")
  if (ideaPath == null) return

  val ideaLib = "$ideaPath/lib"
  var ideaClassPath = "$ideaLib/bootstrap.jar;$ideaLib/extensions.jar;$ideaLib/util.jar;$ideaLib/jdom.jar;$ideaLib/log4j.jar;$ideaLib/trove4j.jar"

  // todo: this is added because of 222 EAP. deal with specific jars with versions!
  ideaClassPath += """
    $ideaLib/3rd-party-rt.jar
    $ideaLib/annotations.jar
    $ideaLib/app.jar
    $ideaLib/byte-buddy-agent.jar
    $ideaLib/error-prone-annotations.jar
    $ideaLib/external-system-rt.jar
    $ideaLib/externalProcess-rt.jar
    $ideaLib/forms_rt.jar
    $ideaLib/groovy.jar
    $ideaLib/idea_rt.jar
    $ideaLib/intellij-coverage-agent-1.0.706.jar
    $ideaLib/intellij-test-discovery.jar
    $ideaLib/jps-model.jar
    $ideaLib/jsch-agent.jar
    $ideaLib/junit.jar
    $ideaLib/junit4.jar
    $ideaLib/protobuf.jar
    $ideaLib/rd.jar
    $ideaLib/stats.jar
    $ideaLib/testFramework.jar
    $ideaLib/util-8.jar
    $ideaLib/util.jar
    $ideaLib/util_rt.jar
  """.trimIndent().lines().joinToString(";", prefix = ";")

  //val jdkHome = System.getProperty("java.home")
  val jdkHome = "D:/lib/jdk/jbrsdk_jcef-17.0.12-windows-x64-b1087.25"

  println("JDK home dir: $jdkHome")

  val ideaPathsSelector = "ProjectorIntelliJIdea"
  val prefix = getIdePrefix(ideaPath)

  val (classToLaunchProperty, launcherClassName) = getLaunchingSetup(isAgent)

  createRunProjectorTask(name, classToLaunchProperty, "com.intellij.idea.Main", launcherClassName) {
    classpath(ideaClassPath, "$jdkHome/lib/tools.jar")

    jvmArgs(
      "-Didea.paths.selector=$ideaPathsSelector",
      "-Didea.vendor.name=ProjectorVendor",
      "-Didea.jre.check=true",
      "-Didea.is.internal=true",
    )

    if (isIdeVersionAtLeast(ideaPath, "212")) { // appeared in 211, became default in 212, mandatory in 221
      jvmArgs("-Djava.system.class.loader=com.intellij.util.lang.PathClassLoader")
    }

    if (prefix != null) {
      jvmArgs("-Didea.platform.prefix=$prefix") // This is required for IDE to determine proper file locations
    }

    configuration()
  }
}
