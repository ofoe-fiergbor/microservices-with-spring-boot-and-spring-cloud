package se.magnus.microservices.core.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RecommendationServiceApplicationTests {
	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 113;
	private static final int PRODUCT_ID_INVALID = 0;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void shouldGetAllRecommendationsByProductId() {
		webTestClient.get()
				.uri(String.format("/recommendation?productId=%s", PRODUCT_ID_OK))
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].productId").isEqualTo(PRODUCT_ID_OK);
	}

	@Test
	public void shouldReturnEmptyResponse_WhenProductNotFound() {
		webTestClient.get()
				.uri(String.format("/recommendation?productId=%s", PRODUCT_ID_NOT_FOUND))
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void shouldReturnInvalidException_WhenProductIdInvalid() {
		webTestClient.get()
				.uri(String.format("/recommendation?productId=%s", PRODUCT_ID_INVALID))
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo(String.format("Invalid productId: %s", PRODUCT_ID_INVALID));
	}

	@Test
	public void shouldReturnBadRequest_WhenProductIdIsOfWrongType() {
		webTestClient.get()
				.uri("/recommendation?productId=STRING_PRODUCT_ID")
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation");
	}
}
