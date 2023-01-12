package se.magnus.microservices.composite.product.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import se.magnus.api.composite.ProductAggregate;
import se.magnus.api.composite.ProductCompositeService;
import se.magnus.api.composite.RecommendationSummary;
import se.magnus.api.composite.ReviewSummary;
import se.magnus.api.composite.ServiceAddresses;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final ProductCompositeIntegration serviceIntegration;
    private final ServiceUtil serviceUtil;

    public ProductCompositeServiceImpl(ProductCompositeIntegration serviceIntegration, ServiceUtil serviceUtil) {
        this.serviceIntegration = serviceIntegration;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        Product product = serviceIntegration.getProduct(productId);

        if (product == null) {
            throw new NotFoundException("Product with productId: " + productId + " not found");
        }

        List<Recommendation> recommendations = serviceIntegration.getRecommendations(productId);
        List<Review> reviews = serviceIntegration.getReviews(productId);
        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(
            Product product,
            List<Recommendation> recommendations,
            List<Review> reviews,
            String serviceAddress) {

        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        List<RecommendationSummary> recommendationSsSummaries = recommendations == null ? null
                : recommendations
                        .stream()
                        .map(recommendation -> new RecommendationSummary(recommendation.getRecommendationId(),
                                recommendation.getAuthor(), recommendation.getRate(), recommendation.getContent()))
                        .toList();
        List<ReviewSummary> reviewSummaries = reviews == null ? null
                : reviews.stream()
                        .map(review -> new ReviewSummary(review.getReviewId(), review.getAuthor(), review.getSubject(),
                                review.getContent()))
                        .toList();

        String reviewServiceAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
        String recommendationServiceAddress = (recommendations != null && recommendations.size() > 0)
                ? recommendations.get(0).getServiceAddress()
                : "";

        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, product.getServiceAddress(),
                reviewServiceAddress, recommendationServiceAddress);
        return new ProductAggregate(productId, name, weight, serviceAddresses, reviewSummaries,
                recommendationSsSummaries);
    }

    @Override
    public void createProduct(ProductAggregate body) {
        try {
            Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
            serviceIntegration.createProduct(product);

            if (body.getRecommendations() != null) {
                body.getRecommendations().forEach(rec -> {
                    Recommendation recommendation = new Recommendation(body.getProductId(), rec.getRecommendationId(),
                            rec.getAuthor(), rec.getRate(), rec.getContent(), null);
                    serviceIntegration.createRecommendation(recommendation);
                });
            }
            if (body.getReviews() != null) {
                body.getReviews().forEach(rev -> {
                    Review review = new Review(body.getProductId(), rev.getReviewId(), rev.getAuthor(),
                            rev.getSubject(), rev.getContent(), null);
                    serviceIntegration.createReview(review);
                });
            }
        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }

    }

    @Override
    public void deleteProduct(int productId) {
        try {
            serviceIntegration.deleteProduct(productId);
            serviceIntegration.deleteRecommendations(productId);
            serviceIntegration.deleteReviews(productId);
        } catch (RuntimeException e) {
            LOG.warn("createCompositeProduct failed", e);
            throw e;
        }

    }

}
