package se.magnus.microservices.core.recommendation.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.microservices.core.recommendation.persistence.RecommendationEntity;
import se.magnus.microservices.core.recommendation.persistence.RecommendationRepository;
import se.magnus.util.http.ServiceUtil;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final RecommendationMapper mapper;
    private final RecommendationRepository repository;

    public RecommendationServiceImpl(ServiceUtil serviceUtil, RecommendationMapper mapper,
            RecommendationRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        if (productId < 1) {
            LOG.debug("getRecommendations: Invalid productId: {}", productId);
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        List<RecommendationEntity> recommendationEntities = repository.findByProductId(productId);
        List<Recommendation> recommendations = recommendationEntities.stream()
                .map(entity -> mapper.entityToDto(entity)).toList();

        recommendations
                .forEach(recommendation -> recommendation.setServiceAddress(serviceUtil.getServiceAddress()));
        LOG.debug("getRecommendations: response size: {}", recommendations.size());

        return recommendations;
    }

    @Override
    public void deleteRecommendations(int productId) {

        List<RecommendationEntity> entities = repository.findByProductId(productId);
        LOG.debug("deleteRecommendations: will delete {} recommendation(s) for the product with productId: {}",
                entities.size(), productId);
        repository.deleteAll(entities);

    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            RecommendationEntity entity = mapper.dtoToEntity(body);
            RecommendationEntity savedEntity = repository.save(entity);
            Recommendation response = mapper.entityToDto(savedEntity);
            response.setServiceAddress(serviceUtil.getServiceAddress());
            LOG.debug("createRecommendation: Recommendation with recommendationId: {} created for productId: {}", response.getRecommendationId(), response.getProductId());
            return response;
        } catch (DuplicateKeyException e) {
            LOG.debug("createRecommendation: Duplicate key, Product Id: {}, Recommendation Id: {}", body.getProductId(), body.getRecommendationId());
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id: " + body.getRecommendationId());
        }
    }

}
