package se.magnus.microservices.core.product;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductServiceApplicationTests {
	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 13;
	private static final int PRODUCT_ID_INVALID = 0;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void shouldGetProductById() {
		webTestClient.get()
				.uri("/product/" + PRODUCT_ID_OK)
				.exchange()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
				.jsonPath("$.name").isEqualTo("name-" + PRODUCT_ID_OK);

	}

	@Test
	public void shouldThrowInvalidException_withInvalidId() {
		webTestClient.get()
				.uri("/product/" + PRODUCT_ID_INVALID)
				.exchange()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product/" + PRODUCT_ID_INVALID)
				.jsonPath("$.message").isEqualTo("Invalide productId: " + PRODUCT_ID_INVALID);
	}

	@Test
	public void shouldThrowNotFoundException_withIdOfNonExistingProduct() {
		webTestClient.get()
				.uri("/product/" + PRODUCT_ID_NOT_FOUND)
				.exchange()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectStatus().isNotFound()
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product/" + PRODUCT_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("No product found for productId: " + PRODUCT_ID_NOT_FOUND);

	}

	@Test
	public void shouldThrowBadRequestException_withIdThatIsOfTypeString() {
		webTestClient.get()
				.uri("/product/" + "STRING_PATH_VARIABLE")
				.exchange()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product/" + "STRING_PATH_VARIABLE");
	}

}
