package org.nemreader.util;

public class DockerUtil {

    public static boolean isDockerRunning() {
        String dockerEnv = System.getenv("DOCKER");
        if (dockerEnv == null || !dockerEnv.equalsIgnoreCase("true")) {
            // If the environment variable is not set or not true, Docker is considered not running
            return false;
        } else {
            return true;
        }
    }
}
