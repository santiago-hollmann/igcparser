/*
 * MIT License
 *
 * Copyright (c) 2017 Santiago Hollmann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.shollmann.igcparser.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.shollmann.android.igcparser.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileUtilities {
    @Nullable
    public static File copyFileToCacheFolder(Context context, String fileToCopyPath, String tempFileName) {
        FileInputStream is = null;
        File file = null;
        try {
            is = new FileInputStream(fileToCopyPath);

            try {
                file = new File(context.getCacheDir(), tempFileName);
                OutputStream output = new FileOutputStream(file);
                try {
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;

                    while ((read = is.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    output.flush();
                } finally {
                    output.close();
                }

            } finally {
                is.close();
            }
        } catch (Throwable t) {
            Logger.logError("FileUtilities :: Error trying copy file to temp folder");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Throwable t) {
                Logger.logError("FileUtilities :: Error trying to close input stream");
            }
        }
        return file;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDirectory(dir);
        } catch (Throwable t) {
        }
    }

    public static boolean deleteDirectory(File directory) {
        if (directory != null && directory.isDirectory()) {
            String[] subfolders = directory.list();
            for (int i = 0; i < subfolders.length; i++) {
                boolean success = deleteDirectory(new File(directory, subfolders[i]));
                if (!success) {
                    return false;
                }
            }
            return directory.delete();
        } else if (directory != null && directory.isFile()) {
            return directory.delete();
        } else {
            return false;
        }
    }

}
