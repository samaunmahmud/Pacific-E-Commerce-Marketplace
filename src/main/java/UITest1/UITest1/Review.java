package UITest1.UITest1;

public class Review {
    private int reviewId;
    private int productId;     
    private int rating;
    private String comment;
    private String reviewStatus;
    private String createdAt;
    private String customerName;
    private int helpfulCount;
    private int unhelpfulCount;

   
    public Review(int reviewId, int rating, String comment, String reviewStatus,
                  String createdAt, String customerName) {
        this(reviewId, 0, rating, comment, reviewStatus, createdAt, customerName, 0, 0);
    }

 
    public Review(int reviewId, int rating, String comment, String reviewStatus,
                  String createdAt, String customerName, int helpfulCount, int unhelpfulCount) {
        this(reviewId, 0, rating, comment, reviewStatus, createdAt, customerName, helpfulCount, unhelpfulCount);
    }


    public Review(int reviewId, int productId, int rating, String comment, String reviewStatus,
                  String createdAt, String customerName, int helpfulCount, int unhelpfulCount) {
        this.reviewId       = reviewId;
        this.productId      = productId;
        this.rating         = rating;
        this.comment        = comment;
        this.reviewStatus   = reviewStatus;
        this.createdAt      = createdAt;
        this.customerName   = customerName;
        this.helpfulCount   = helpfulCount;
        this.unhelpfulCount = unhelpfulCount;
    }

    public int getReviewId()       {
    	return reviewId; 
    	}
    
    public int    getProductId(){ 
    	return productId; 
    	}
    
    public int    getRating(){ 
    	return rating; 
    	}
    public String getComment(){ 
    	return comment; 
    	}
    public String getReviewStatus(){ 
    	return reviewStatus; 
    	}
    public String getCreatedAt(){ 
    	return createdAt; 
    	}
    public String getCustomerName(){ 
    	return customerName; 
    	}
    public int    getHelpfulCount(){ 
    	return helpfulCount; 
    	}
    public int    getUnhelpfulCount() { 
    	return unhelpfulCount; 
    	}
}