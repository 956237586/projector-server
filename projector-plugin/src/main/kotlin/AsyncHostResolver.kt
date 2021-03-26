/*
 * Copyright (c) 2019-2021, JetBrains s.r.o. and/or its affiliates. All rights reserved.
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

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.thread
import org.jetbrains.projector.server.core.util.getHostName
import javax.swing.SwingUtilities

class Host(val address: String, private val name: String) {

  constructor(ip: java.net.InetAddress, name: String? = null) : this(ip2String(ip), name ?: "resolving ...")

  override fun toString(): String {
    val displayName = when {
      address == "127.0.0.1" -> "localhost"
      name == address -> ""
      else -> name
    }

    return if (displayName.isEmpty()) address else "$address ( $displayName )"
  }

  companion object {
    private fun ip2String(ip: java.net.InetAddress) = ip.toString().substring(1)
  }
}

interface ResolvedHostSubscriber {
  fun resolved(h: Host)
}

class AsyncHostResolver {
  private val subscribers = Collections.synchronizedList(ArrayList<ResolvedHostSubscriber>())
  private val address2Name = Collections.synchronizedMap(HashMap<java.net.InetAddress, String>())
  private val queue: MutableList<java.net.InetAddress> = Collections.synchronizedList(ArrayList<java.net.InetAddress>())

  fun subscribe(s: ResolvedHostSubscriber) = subscribers.add(s)

  fun unsubscribe(s: ResolvedHostSubscriber) = subscribers.remove(s)

  fun unsubscribeAll() = subscribers.clear()

  fun cancelAllPendingRequests() = queue.clear()

  fun resolve(ip: java.net.InetAddress): Host {
    val name = getName(ip)

    if (name == null) {
      addRequest(ip)
    }

    return Host(ip, name)
  }

  private fun getName(ip: java.net.InetAddress): String? = address2Name[ip]

  private fun addRequest(ip: java.net.InetAddress) {
    queue.add(ip)
    runWorker()
  }

  private fun runWorker() {
    thread {
      while (queue.isNotEmpty()) {
        var item: java.net.InetAddress?

        synchronized(queue) {
          item = queue.firstOrNull()
          item?.let { queue.removeAt(0) }
        }

        item?.let { ip ->
          val res = getHostName(ip)

          res?.let { name ->
            address2Name[ip] = name

            for (s in subscribers) {
              SwingUtilities.invokeLater { s.resolved(Host(ip, name)) }
            }
          }
        }
      }
    }
  }
}
