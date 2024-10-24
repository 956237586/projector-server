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

import org.jetbrains.projector.server.core.ij.log.DelegatingJvmLogger
import org.jetbrains.projector.server.service.ProjectorFontProvider
import org.jetbrains.projector.util.loading.UseProjectorLoader
import org.jetbrains.projector.util.logging.Logger
import java.awt.Toolkit
import java.lang.reflect.Method
import kotlin.system.exitProcess

@UseProjectorLoader
open class ProjectorStarter {

  companion object {

    /**
     * Starts the Projector server with the specified arguments and AWT provider.
     * This function retrieves the main class to launch from the system property,
     * gets the main method of that class, and invokes it with the provided arguments.
     *
     * @param args The arguments to pass to the main method of the class to launch.
     * @param awtProvider The AWT provider to use for the Projector server.
     */
    @JvmStatic
    fun start(args: Array<String>, awtProvider: PAwtProvider) {
      val canonicalMainClassName = requireNotNull(System.getProperty(ProjectorLauncherBase.MAIN_CLASS_PROPERTY_NAME)) {
        "System property `${ProjectorLauncherBase.MAIN_CLASS_PROPERTY_NAME}` isn't assigned, so can't understand which class to launch"
      }

      val mainMethod = getMainMethodOf(canonicalMainClassName)

      if (runProjectorServer(awtProvider)) {
        mainMethod.invoke(null, args)
      }
      else {
        exitProcess(1)
      }
    }

    private fun getMainMethodOf(canonicalClassName: String): Method {
      val mainClass = Class.forName(canonicalClassName)
      return mainClass.getMethod("main", Array<String>::class.java)
    }

    /**
     * Sets up the singletons required for the Projector server.
     * This function sets up the AWT toolkit, font manager, and repaint manager.
     *
     * @param projectorToolkit The AWT toolkit to be used by the Projector server.
     */
    private fun setupSingletons(projectorToolkit: Toolkit) {
      setupToolkit(projectorToolkit)
      setupFontManager()
      setupRepaintManager()
    }

    /**
     * Initializes the general headless settings for the Projector server.
     * This function sets up the system properties and sets the font provider to non-agent mode.
     */
    private fun initializeHeadlessGeneral() {
      setupSystemProperties()
      ProjectorFontProvider.isAgent = false
    }

    /**
     * Initializes the full headless settings for the Projector server.
     * This function sets up the singletons required for the Projector server.
     *
     * @param projectorToolkit The AWT toolkit to be used by the Projector server.
     */
    private fun initializeHeadlessFull(projectorToolkit: Toolkit) {
      setupSingletons(projectorToolkit)
    }

    /**
     * Runs the Projector server with the specified AWT provider and logger factory.
     * This function initializes the headless settings, sets up the singletons,
     * and starts the Projector server.
     *
     * @param awtProvider The AWT provider to use for the Projector server.
     * @param loggerFactory The logger factory to use for logging.
     * @return True if the server was started successfully, false otherwise.
     */
    @JvmStatic
    @JvmOverloads
    fun runProjectorServer(awtProvider: PAwtProvider, loggerFactory: (tag: String) -> Logger = ::DelegatingJvmLogger): Boolean {
      System.setProperty(ProjectorServer.ENABLE_PROPERTY_NAME, true.toString())

      assert(ProjectorServer.isEnabled) { "Can't start the ${ProjectorServer::class.simpleName} because it's disabled..." }

      // Initializing toolkit before awt transformer results in caching of headless property (= true)
      // and call to system graphics environment initialization, so we need firstly to set up java.awt.headless=false,
      // then set our graphics environment (via transformer), and only then to initialize toolkit
      val server = ProjectorServer.startServer(
        isAgent = false, loggerFactory,
        { initializeHeadlessGeneral() }, { initializeHeadlessFull(awtProvider.createToolkit()) },
      )

      Runtime.getRuntime().addShutdownHook(object : Thread() {

        override fun run() {
          server.stop()
        }
      })

      return server.wasStarted
    }

    /**
     * Summarizes the logical connections between the setup functions and their roles in rendering the remote interface locally.
     * The setup functions are responsible for configuring the AWT toolkit, font manager, repaint manager, and system properties
     * to ensure that the application can render the remote interface correctly on the local machine.
     */
    @JvmStatic
    private fun summarizeLogicalConnections() {
      // The setupSingletons function configures the AWT toolkit, font manager, and repaint manager to be used by the Projector server.
      // The initializeHeadlessGeneral function configures the system properties and sets the font provider to non-agent mode.
      // The initializeHeadlessFull function configures the singletons required for the Projector server.
      // Together, these functions ensure that the Projector server can render the remote interface correctly on the local machine.
    }

  }
}
