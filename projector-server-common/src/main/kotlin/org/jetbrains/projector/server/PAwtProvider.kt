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

import org.jetbrains.projector.awt.PToolkitBase

interface PAwtProvider {

  /**
   * Creates an instance of the AWT toolkit to be used by the application.
   * This function is responsible for creating and returning an instance of the PToolkitBase class.
   *
   * @return An instance of the PToolkitBase class.
   */
  fun createToolkit(): PToolkitBase

  /**
   * The class of the AWT toolkit to be used by the application.
   * This property returns the class of the PToolkitBase instance that will be created by the createToolkit function.
   */
  val toolkitClass: Class<out PToolkitBase>

}

/**
 * Summarizes the logical connections between the createToolkit function and the toolkitClass property,
 * and their roles in rendering the remote interface locally.
 * The createToolkit function is responsible for creating an instance of the AWT toolkit,
 * while the toolkitClass property provides the class of the toolkit instance.
 * Together, they ensure that the application can render the remote interface correctly on the local machine.
 */
internal fun summarizeLogicalConnections() {
  // The createToolkit function creates an instance of the AWT toolkit to be used by the application.
  // The toolkitClass property provides the class of the AWT toolkit instance.
  // Together, these functions ensure that the application can render the remote interface correctly on the local machine.
}
