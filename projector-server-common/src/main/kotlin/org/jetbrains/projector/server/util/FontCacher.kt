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
@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package org.jetbrains.projector.server.util

import org.jetbrains.projector.common.protocol.data.FontDataHolder
import org.jetbrains.projector.common.protocol.data.TtfFontData
import org.jetbrains.projector.server.core.util.ObjectIdCacher
import org.jetbrains.projector.server.service.ProjectorFontProvider
import org.jetbrains.projector.util.loading.unprotect
import sun.font.CompositeFont
import sun.font.FileFont
import sun.font.Font2D
import java.awt.Font
import java.io.File
import java.util.*

object FontCacher {

  private val filePathCacher = ObjectIdCacher<Short, String>(0) { (it + 1).toShort() }

  /**
   * Retrieves the unique identifier for the given font.
   * This function gets the file path of the font and returns its cached ID.
   *
   * @param font The font for which to retrieve the unique identifier.
   * @return The unique identifier for the given font, or null if the file path is not found.
   */
  fun getId(font: Font): Short? {
    val filePath = font.getFilePath() ?: return null

    return filePathCacher.getIdBy(filePath)
  }

  /**
   * Retrieves the FontDataHolder for the given font ID.
   * This function gets the file path associated with the font ID, reads the font data,
   * encodes it in Base64, and returns a FontDataHolder containing the font ID and TtfFontData.
   *
   * @param fontId The unique identifier of the font to retrieve.
   * @return The FontDataHolder containing the font ID and TtfFontData.
   */
  fun getFontData(fontId: Short): FontDataHolder {
    val filePath = filePathCacher.getObjectBy(fontId)

    val data = File(filePath).readBytes()
    val base64 = String(Base64.getEncoder().encode(data))

    return FontDataHolder(fontId, TtfFontData(ttfBase64 = base64))
  }

  private val publicFileNameMethod = FileFont::class.java.getDeclaredMethod("getPublicFileName").apply {
    unprotect()
  }

  /**
   * Retrieves the file path of the Font2D object.
   * This function checks if the Font2D object is a CompositeFont or FileFont and returns the appropriate file path.
   *
   * @return The file path of the Font2D object, or null if not found.
   */
  private fun Font2D.getFilePath(): String? {
    when (this) {
      is CompositeFont -> {
        for (i in 0 until this.numSlots) {
          val physicalFont = this.getSlotFont(i)
          return physicalFont.getFilePath()  // todo: use not only the first
        }

        return null
      }

      is FileFont -> return publicFileNameMethod.invoke(this) as String

      else -> return null
    }
  }

  private val getFont2DMethod = Font::class.java.getDeclaredMethod("getFont2D").apply {
    unprotect()
  }

  /**
   * Retrieves the file path of the Font object.
   * This function finds the Font2D object associated with the Font and returns its file path.
   *
   * @return The file path of the Font object, or null if not found.
   */
  private fun Font.getFilePath(): String? {
    return ProjectorFontProvider.findFont2D(this.name, this.style, 0).getFilePath()
  }

  /**
   * Summarizes the logical connections between the getId, getFontData, getFilePath, and getFilePath extension functions,
   * and their roles in rendering the remote interface locally.
   * The getId function retrieves the unique identifier for the given font.
   * The getFontData function retrieves the FontDataHolder for the given font ID.
   * The getFilePath function retrieves the file path of the Font2D object.
   * The getFilePath extension function for Font retrieves the file path of the Font object.
   * Together, these functions ensure that the application can render the remote interface correctly on the local machine.
   */
  internal fun summarizeLogicalConnections() {
    // The getId function retrieves the unique identifier for the given font.
    // The getFontData function retrieves the FontDataHolder for the given font ID.
    // The getFilePath function retrieves the file path of the Font2D object.
    // The getFilePath extension function for Font retrieves the file path of the Font object.
    // Together, these functions ensure that the application can render the remote interface correctly on the local machine.
  }
}
