/*
 * Copyright (C) 2012 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (c) 2012 Michael Elsdörfer (Android Autostarts)
 * Copyright (c) 2012 Stephen Erickson, Chris Ravenscroft, Adam Shanks (RootTools)
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

package org.sufficientlysecure.rootcommands.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Utils {
    /**
     * This code is adapted from java.lang.ProcessBuilder.start().
     * 
     * The problem is that Android doesn't allow us to modify the map returned by
     * ProcessBuilder.environment(), even though the docstring indicates that it should. This is
     * because it simply returns the SystemEnvironment object that System.getenv() gives us. The
     * relevant portion in the source code is marked as "// android changed", so presumably it's not
     * the case in the original version of the Apache Harmony project.
     * 
     * Note that simply passing the environment variables we want to Process.exec won't be good
     * enough, since that would override the environment we inherited completely.
     * 
     * We needed to be able to set a CLASSPATH environment variable for our new process in order to
     * use the "app_process" command directly. Note: "app_process" takes arguments passed on to the
     * Dalvik VM as well; this might be an alternative way to set the class path.
     * 
     * Code from https://github.com/miracle2k/android-autostarts, use under Apache License was
     * agreed by Michael Elsdörfer
     */
    public static Process runWithEnv(String command, ArrayList<String> customAddedEnv,
            String baseDirectory) throws IOException {

        Map<String, String> environment = System.getenv();
        String[] envArray = new String[environment.size()
                + (customAddedEnv != null ? customAddedEnv.size() : 0)];
        int i = 0;
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            envArray[i++] = entry.getKey() + "=" + entry.getValue();
        }
        if (customAddedEnv != null) {
            for (String entry : customAddedEnv) {
                envArray[i++] = entry;
            }
        }

        Process process;
        if (baseDirectory == null) {
            process = Runtime.getRuntime().exec(command, envArray, null);
        } else {
            process = Runtime.getRuntime().exec(command, envArray, new File(baseDirectory));
        }
        return process;
    }
}
