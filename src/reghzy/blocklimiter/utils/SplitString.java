package reghzy.blocklimiter.utils;

public class SplitString {
    public final String before;
    public final String after;

    public SplitString(String before, String after) {
        this.before = before;
        this.after = after;
    }

    public static SplitString split(String original, char splitter) {
        int splitIndex = original.indexOf(splitter);
        if (splitIndex == -1) {
            return null;
        }

        return new SplitString(original.substring(0, splitIndex), original.substring(splitIndex + 1));
    }

    public static SplitString split(String original, String splitter) {
        int splitIndex = original.indexOf(splitter);
        if (splitIndex == -1) {
            return null;
        }

        return new SplitString(original.substring(0, splitIndex), original.substring(splitIndex + splitter.length()));
    }
}
