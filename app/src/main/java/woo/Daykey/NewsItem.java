package woo.Daykey;

class NewsItem {
    private String title;
    private String date;
    private String writer;
    private String visitors;

    NewsItem(String title, String date, String visitors, String writer) {
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.visitors = visitors;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getWriter() {
        return writer;
    }

    public String getVisitors() {
        return visitors;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setVisitors(String visitors) {
        this.visitors = visitors;
    }
}
