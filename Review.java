public class Review {
    private String reviewer;
    private String content;
    private String stars;

    public Review(String reviewer, String content, String stars) {
        this.reviewer = reviewer;
        this.content = content;
        this.stars = stars;
    }

    public String getReviewer() {
        return reviewer;
    }

    public String getContent() {
        return content;
    }

    public String getStars() {
        return stars;
    }

    public String toString() {
        return "Reviewer: " + reviewer + "\nContent: " + content + "\nStars: " + stars;
    }
}
