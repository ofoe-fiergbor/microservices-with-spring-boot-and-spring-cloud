package se.magnus.microservices.core.review.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.microservices.core.review.persistence.ReviewEntity;
import se.magnus.microservices.core.review.persistence.ReviewRepository;
import se.magnus.util.http.ServiceUtil;

@RestController
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final ReviewMapper mapper;

    public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewRepository repository, ReviewMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Review> getReviews(int productId) {

        if (productId < 1) {
            LOG.debug("getReviews: Invalid productId: {}", productId);
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        List<ReviewEntity> reviewEntities = repository.findByProductId(productId);
        List<Review> reviews = reviewEntities.stream().map(entity -> mapper.entityToDto(entity)).toList();
        reviews.forEach(review -> review.setServiceAddress(serviceUtil.getServiceAddress()));
        LOG.debug("getReviews: response size: {}", reviews.size());
        return reviews;
    }

    @Override
    public void deleteReviews(int productId) {
        List<ReviewEntity> reviewEntities = repository.findByProductId(productId);
        LOG.debug("deleteReviews: Will delete {} review(s) for productId: {}", reviewEntities.size(), productId);
        repository.deleteAll(reviewEntities);
    }

    @Override
    public Review createReview(Review body) {
        try {
            ReviewEntity entity = mapper.dtoToEntity(body);
            ReviewEntity savedEntity = repository.save(entity);
            Review response = mapper.entityToDto(savedEntity);
            response.setServiceAddress(serviceUtil.getServiceAddress());
            LOG.debug("createReview: Review with productId: {}, reviewId: {} created", response.getProductId(), response.getReviewId());
            return response;
        } catch (DataIntegrityViolationException e) {
            LOG.debug("Duplicate Key, ProductId: {}, ReviewId: {}", body.getProductId(), body.getReviewId());
            throw new InvalidInputException("Duplicate Key, ProductId: "+ body.getProductId()+ ", ReviewId: "+ body.getReviewId());
        }
    }
}
