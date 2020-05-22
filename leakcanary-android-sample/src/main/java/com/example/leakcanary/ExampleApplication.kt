/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.leakcanary

import android.app.Application
import android.os.Handler
import android.os.StrictMode
import android.view.View
import shark.SharkLog

open class ExampleApplication : Application() {
  val leakedViews = mutableListOf<View>()

  override fun onCreate() {
    super.onCreate()
    enabledStrictMode()

    val holderClass = Class.forName("android.view.ViewGroup\$ViewLocationHolder")
    val mRootField = holderClass.getDeclaredField("mRoot")
    mRootField.isAccessible = true

    val sPoolField = holderClass.getDeclaredField("sPool")
    sPoolField.isAccessible = true
    val sPool: Any = sPoolField.get(null)

    val simplePoolClass: Class<*>? = sPool.javaClass.superclass

    val poolArrayField = simplePoolClass!!.getDeclaredField("mPool")
    poolArrayField.isAccessible = true
    val poolSizeField = simplePoolClass.getDeclaredField("mPoolSize")
    poolSizeField.isAccessible = true

    @Suppress("UNCHECKED_CAST")
    val poolArray = poolArrayField.get(sPool) as Array<Any>

    val handler = Handler()
    val foo = object : Runnable {
      override fun run() {
        val poolSize = poolSizeField.get(sPool) as Int
        val found = mutableListOf<String>()
        for (i in 0 until poolSize) {
          val viewLocationHolder = poolArray[i]
          val mRoot = mRootField[viewLocationHolder]
          if (mRoot != null) {
            found += "$i with root: $mRoot"
          }
        }
        SharkLog.d { "sPool size $poolSize found:\n ${found.joinToString("\n")}\n\n" }
        handler.postDelayed(this, 1000)
      }
    }
    foo.run()

  }

  private fun enabledStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .penaltyDeath()
            .build()
    )
  }
}
