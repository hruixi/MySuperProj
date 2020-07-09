package rui.com.crashlog.HttpRev;

public class ST_AddCase
{
    private boolean hospital;
    private boolean ststus;
    private String message;

    public String showStr()
    {
        String contentStr = "";
        contentStr += hospital + "\n"
                + ststus + "\n"
                + message + "\n";

        return contentStr;
    }
}
