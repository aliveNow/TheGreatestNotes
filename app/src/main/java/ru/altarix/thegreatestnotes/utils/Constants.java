package ru.altarix.thegreatestnotes.utils;

/**
 * Created by samsmariya on 01.10.17.
 */

public class Constants {
    public static final String NAMESPACE_PREFIX = "ru.altarix.thegreatestnotes";

    public static class Extras {

        private static String createExtra(String suffix){
            return Constants.NAMESPACE_PREFIX + ".extra." + suffix;
        }

        public static final String NOTE = createExtra("NOTE");
        public static final String ACTION = createExtra("ACTION");
    }

    public enum Action {
        CREATE,
        VIEW,
        EDIT,
        DELETE;
    }
}
