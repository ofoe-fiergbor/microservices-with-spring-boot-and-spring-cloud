package se.magnus.api.composite;

import java.util.List;

public class ProductAggregate {
    private final int productId;
    private final String name;
    private final int weight;
    private final ServiceAddresses serviceAddresses;
    private final List<ReviewSummary> reviews;
    private final List<RecommendationSummary> recommendations;

    public ProductAggregate(
            int productId,
            String name,
            int weight,
            ServiceAddresses serviceAddresses,
            List<ReviewSummary> reviews,
            List<RecommendationSummary> recommendations) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.serviceAddresses = serviceAddresses;
        this.reviews = reviews;
        this.recommendations = recommendations;
    }

    public int getProductId() {
        return this.productId;
    }


    public String getName() {
        return this.name;
    }


    public int getWeight() {
        return this.weight;
    }


    public ServiceAddresses getServiceAddresses() {
        return this.serviceAddresses;
    }


    public List<ReviewSummary> getReviews() {
        return this.reviews;
    }


    public List<RecommendationSummary> getRecommendations() {
        return this.recommendations;
    }

    
}
