/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2017 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jraf.android.alibglitch;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class GlitchEffect {
    private static final Random RANDOM = new Random();
    private static final int BITMAP_COUNT = 4;
    private static final int JPEG_QUALITY = 33;
    private static final int JPEG_CORRUPTION_COUNT = 5;
    private static final int JPEG_HEADER_SIZE = 100;
    private static final int ANIM_FRAME_DURATION_MAX = 150;
    private static final int ANIM_FRAME_DURATION_MIN = 50;

    private static Handler sMainHandler;

    public static void showGlitch(Activity activity) {
        Bitmap bitmap = captureWindow(activity);
        Bitmap[] corruptedBitmaps = makeCorruptedBitmaps(bitmap, BITMAP_COUNT);
        showAnimation(activity, corruptedBitmaps);
    }

    private static Bitmap captureWindow(Activity activity) {
        View root = activity.getWindow().getDecorView().getRootView();
        Bitmap screenshot = Bitmap.createBitmap(root.getWidth(), root.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(screenshot);
        root.draw(canvas);
        return screenshot;
    }

    private static Bitmap[] makeCorruptedBitmaps(Bitmap bitmap, int count) {
        Bitmap[] res = new Bitmap[count];
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, bos);
        byte[] data = bos.toByteArray();
        int i = 0;
        while (i < count) {
            byte[] corrupted = getCorruptedArray(data);
            Bitmap decoded = decodeJpg(corrupted);
            if (decoded != null) {
                res[i] = decoded;
                i++;
            }
        }
        return res;
    }

    private static byte[] getCorruptedArray(byte[] source) {
        byte[] res = source.clone();
        for (int i = 0; i < JPEG_CORRUPTION_COUNT; i++) {
            int idx = RANDOM.nextInt(res.length - JPEG_HEADER_SIZE) + JPEG_HEADER_SIZE;
            res[idx] += (byte) RANDOM.nextInt(3);
        }
        return res;
    }

    @Nullable
    private static Bitmap decodeJpg(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 2;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    private static void showAnimation(Activity activity, Bitmap[] bitmaps) {
        final WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        View decorView = activity.getWindow().getDecorView();
        layoutParams.width = decorView.getWidth();
        layoutParams.height = decorView.getHeight();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        layoutParams.token = decorView.getRootView().getWindowToken();

        final ImageView imageView = new ImageView(activity);
        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(true);
        int totalDuration = 0;
        Resources resources = activity.getResources();
        for (Bitmap bitmap : bitmaps) {
            int duration = RANDOM.nextInt(ANIM_FRAME_DURATION_MAX - ANIM_FRAME_DURATION_MIN) + ANIM_FRAME_DURATION_MIN;
            totalDuration += duration;
            animationDrawable.addFrame(new BitmapDrawable(resources, bitmap), duration);
        }
        imageView.setImageDrawable(animationDrawable);
        windowManager.addView(imageView, layoutParams);
        animationDrawable.start();
        getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                windowManager.removeView(imageView);
            }
        }, totalDuration);
    }

    private static Handler getMainHandler() {
        if (sMainHandler == null) sMainHandler = new Handler(Looper.getMainLooper());
        return sMainHandler;
    }
}
