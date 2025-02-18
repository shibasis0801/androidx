/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.transition

import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class EpicenterTest : BaseTest() {

    @Test
    fun defaultEpicenter() {
        testViewEpicenter {}
    }

    @Test
    fun rotatedViewEpicenter() {
        testViewEpicenter {
            rotation = 180f
        }
    }

    @Test
    fun scaledPivotEpicenter() {
        testViewEpicenter {
            pivotX = width.toFloat()
            pivotY = height.toFloat()
            scaleX = 0.5f
            scaleY = 0.5f
        }
    }

    private fun testViewEpicenter(viewSetup: View.() -> Unit) {
        val view = setupTestView()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.viewSetup()
        }
        val transitionSupport = FragmentTransitionSupport()

        val transition = AutoTransition()
        transitionSupport.setEpicenter(transition, view)

        val rect = RectF(0f, 0f, view.width.toFloat(), view.height.toFloat())
        view.matrix.mapRect(rect)

        rect.offset(view.left.toFloat(), view.top.toFloat())
        val (parentX, parentY) = IntArray(2).apply {
            (view.parent as View).getLocationOnScreen(this)
        }
        rect.offset(parentX.toFloat(), parentY.toFloat())

        val expected = Rect().also {
            rect.round(it)
        }

        assertThat(transition.epicenter).isEqualTo(expected)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    /**
     * Returns a view of size 100x100 located at (50, 50)
     */
    private fun setupTestView(): View {
        val view = View(rule.activity)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            rule.activity.root.addView(
                view,
                FrameLayout.LayoutParams(100, 100)
                    .apply {
                        leftMargin = 50
                        topMargin = 50
                    }
            )
            view.left = 50
            view.top = 50
            view.right = 150
            view.bottom = 150
        }
        return view
    }
}
