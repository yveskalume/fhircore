/*
 * Copyright 2021 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartregister.fhircore.quest.robolectric

import android.os.Build
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.clearAllMocks
import java.io.File
import java.io.FileReader
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.smartregister.fhircore.quest.app.fakes.FakeKeyStore
import org.smartregister.fhircore.quest.coroutine.CoroutineTestRule

@RunWith(FhircoreTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
abstract class RobolectricTest {

  @get:Rule(order = 10) val coroutineTestRule = CoroutineTestRule()

  @get:Rule(order = 20) val instantTaskExecutorRule = InstantTaskExecutorRule()

  /** Get the liveData value by observing but wait for 3 seconds if not ready then stop observing */
  @Throws(InterruptedException::class)
  fun <T> getLiveDataValue(liveData: LiveData<T>): T? {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer: Observer<T> =
      object : Observer<T> {
        override fun onChanged(o: T?) {
          data[0] = o
          latch.countDown()
          liveData.removeObserver(this)
        }
      }
    liveData.observeForever(observer)
    latch.await(3, TimeUnit.SECONDS)
    return data[0] as T?
  }

  fun String.readFile(): String {
    val file = File("$ASSET_BASE_PATH/$this")
    val charArray = CharArray(file.length().toInt()).apply { FileReader(file).read(this) }
    return String(charArray)
  }

  @After
  open fun tearDown() {
    Shadows.shadowOf(Looper.getMainLooper()).idle()
  }

  companion object {
    val ASSET_BASE_PATH =
      (System.getProperty("user.dir") +
        File.separator +
        "src" +
        File.separator +
        "main" +
        File.separator +
        "resources" +
        File.separator)

    @JvmStatic
    @BeforeClass
    fun beforeClass() {
      FakeKeyStore.setup
    }

    @JvmStatic
    @AfterClass
    fun resetMocks() {
      clearAllMocks()
    }
  }
}