/*
 * Copyright (c) 2019-2023, JetBrains s.r.o. and/or its affiliates. All rights reserved.
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
package org.jetbrains.projector.server

import org.jetbrains.projector.util.loading.ProjectorClassLoaderSetup
import org.jetbrains.projector.util.loading.unprotect
import java.lang.reflect.Method

abstract class ProjectorLauncherBase {

  companion object {

    /* Field is public for the case, when someone would like to launch server from their own code. */
    @Suppress("Could be private")
    const val MAIN_CLASS_PROPERTY_NAME = "org.jetbrains.projector.server.classToLaunch"

    /**
     * Starts the Projector server with the specified arguments and AWT provider.
     * This function retrieves the main class to launch from the system property,
     * gets the main method of that class, and invokes it with the provided arguments.
     *
     * @param args The arguments to pass to the main method of the class to launch.
     * @param awtProvider The AWT provider to use for the Projector server.
     */
    @JvmStatic
    protected fun start(args: Array<String>, awtProvider: PAwtProvider) {

      /**
       * [ProjectorStarter.start]
       */
      getStarterClass()
        .getDeclaredMethod("start", Array<String>::class.java, PAwtProvider::class.java)
        .apply(Method::unprotect)
        .invoke(null, args, awtProvider)
    }

    /**
     * Runs the Projector server with the specified AWT provider.
     * This function retrieves the main class to launch from the system property,
     * gets the runProjectorServer method of that class, and invokes it with the provided AWT provider.
     *
     * @param awtProvider The AWT provider to use for the Projector server.
     * @return True if the server was started successfully, false otherwise.
     */
    @JvmStatic
    protected fun runProjectorServer(awtProvider: PAwtProvider): Boolean {

      /**
       * [ProjectorStarter.runProjectorServer]
       */
      return getStarterClass()
        .getDeclaredMethod("runProjectorServer", PAwtProvider::class.java)
        .apply(Method::unprotect)
        .invoke(null, awtProvider) as Boolean
    }

    /**
     * Retrieves the starter class for the Projector server.
     * This function initializes the Projector class loader and loads the ProjectorStarter class.
     *
     * @return The Class object representing the ProjectorStarter class.
     */
    @JvmStatic
    private fun getStarterClass(): Class<*> {
      val thisClass = ProjectorLauncherBase::class.java
      val prjClassLoader = ProjectorClassLoaderSetup.initClassLoader(thisClass.classLoader)

      return prjClassLoader.loadClass("${thisClass.packageName}.ProjectorStarter")
    }

    /**
     * Summarizes the logical connections between the start, runProjectorServer, and getStarterClass functions,
     * and their roles in rendering the remote interface locally.
     * The start function initializes and starts the Projector server with the specified arguments and AWT provider.
     * The runProjectorServer function initializes and starts the Projector server with the specified AWT provider.
     * The getStarterClass function retrieves the starter class for the Projector server.
     * Together, these functions ensure that the Projector server is properly initialized and started,
     * allowing the remote interface to be rendered correctly on the local machine.
     */
    @JvmStatic
    private fun summarizeLogicalConnections() {
      // The start function initializes and starts the Projector server with the specified arguments and AWT provider.
      // The runProjectorServer function initializes and starts the Projector server with the specified AWT provider.
      // The getStarterClass function retrieves the starter class for the Projector server.
      // Together, these functions ensure that the Projector server is properly initialized and started,
      // allowing the remote interface to be rendered correctly on the local machine.
    }
  }

}
