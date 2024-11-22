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

plugins {
  kotlin("jvm")
  application
  `maven-publish`
}

applyCommonServerConfiguration(application)

//dependencies {
//  implementation("$projectorClientGroup:projector-common:$projectorClientVersion")
//  implementation("$projectorClientGroup:projector-ij-common:$projectorClientVersion")
//  implementation("$projectorClientGroup:projector-server-core:$projectorClientVersion")
//  implementation("$projectorClientGroup:projector-util-loading:$projectorClientVersion")
//  implementation("$projectorClientGroup:projector-util-logging:$projectorClientVersion")
//  api(project(":projector-awt"))
//
//  if (intellijPlatformBuildNumber >= BuildNumber.fromString("203.5981.165")!!) {
//    compileOnly("com.jetbrains.intellij.platform:code-style:$intellijPlatformVersion")
//  } else {
//    compileOnly("com.jetbrains.intellij.platform:lang:$intellijPlatformVersion")
//  }
//
//  compileOnly("com.jetbrains.intellij.platform:core-ui:$intellijPlatformVersion")
//  compileOnly("com.jetbrains.intellij.platform:ide-impl:$intellijPlatformVersion")
//  compileOnly("org.jetbrains.intellij.deps.jcef:jcef:$intellijJcefVersion")
//
//  testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
//  testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
//  testImplementation("com.jetbrains.intellij.platform:core:$intellijPlatformVersion")
//}
kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
  }
}
