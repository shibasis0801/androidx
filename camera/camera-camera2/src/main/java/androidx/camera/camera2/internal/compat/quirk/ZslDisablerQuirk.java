/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.camera.camera2.internal.compat.quirk;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.impl.Quirk;

import java.util.Locale;

/**
 * <p>QuirkSummary
 *     Bug Id: 252818931, 261744070
 *     Description: On certain devices, the captured image has color issue for reprocessing. We
 *                  need to disable zero-shutter lag and return false for
 *                  {@link CameraInfo#isZslSupported()}.
 *     Device(s): Samsung Fold4, Xiaomi Mi 8
 */
@RequiresApi(21) // TODO(b/200306659): Remove and replace with annotation on package-info.java
public class ZslDisablerQuirk implements Quirk {

    static boolean load() {
        return isSamsungFold4() || isXiaoMiMi8();
    }

    private static boolean isSamsungFold4() {
        return "samsung".equalsIgnoreCase(Build.BRAND)
                && android.os.Build.MODEL.toUpperCase(Locale.US).startsWith("SM-F936");
    }
    private static boolean isXiaoMiMi8() {
        return "xiaomi".equalsIgnoreCase(Build.BRAND)
                && android.os.Build.MODEL.toUpperCase(Locale.US).startsWith("MI 8");
    }
}
