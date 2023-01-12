package se.magnus.microservices.core.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.microservices.core.product.persistence.ProductEntity;
import se.magnus.microservices.core.product.persistence.ProductRepository;
import se.magnus.util.http.ServiceUtil;

@RestController
public class ProductServiceImpl implements ProductService {

    private final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductMapper mapper;
    private final ProductRepository repository;

    public ProductServiceImpl(ServiceUtil serviceUtil, ProductMapper mapper, ProductRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Product getProduct(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return repository.findByProductId(productId)
                .map(product -> {
                    Product response = mapper.entityToDto(product);
                    response.setServiceAddress(serviceUtil.getServiceAddress());
                    LOG.debug("getProduct: found productId: {}", product.getProductId());
                    return response;
                })
                .orElseThrow(
                        () -> {
                            LOG.debug("getProduct: productId: {} not found", productId);
                            throw new NotFoundException(String.format("Product with productId %s not found.", productId));
                        });
    }

    @Override
    public Product createProduct(Product body) {
        try {
            ProductEntity entity = mapper.dtoToEntity(body);
            ProductEntity saved = repository.save(entity);
            Product response = mapper.entityToDto(saved);
            response.setServiceAddress(serviceUtil.getServiceAddress());
            LOG.info("createProduct: Created entity for productId {}", response.getProductId());
            return response;
        } catch (DuplicateKeyException e) {
            LOG.debug("createProduct: Duplicate key, productId: {}", body.getProductId());
            throw new InvalidInputException(String.format("Duplicate key, Product Id: %s", body.getProductId()));
        }
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.info("deleteProduct: Will delete product with productId {}", productId);
        repository.findByProductId(productId)
                .ifPresent(product -> repository.delete(product));
    }

}
