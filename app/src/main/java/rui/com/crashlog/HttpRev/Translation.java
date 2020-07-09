package rui.com.crashlog.HttpRev;

public class Translation
{
    private int status;
    private content content;

    private static class content
    {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    //定义 输出返回数据 的方法
    public String showStr()
    {
        String contentStr = "";
        contentStr += status + "\n"
                    + content.from + "\n"
                    + content.to + "\n"
                    + content.vendor + "\n"
                    + content.out + "\n"
                    + content.errNo + "\n";

        return contentStr;
    }
}
