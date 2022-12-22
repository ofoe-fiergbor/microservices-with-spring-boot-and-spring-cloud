package se.magnus.microservices.core.review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReviewServiceApplicationTests {
	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 213;
	private static final int PRODUCT_ID_INVALID = 0;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void shouldGetAllReviewsByProductId() {
		webTestClient.get()
				.uri(String.format("/review?productId=%s", PRODUCT_ID_OK))
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].productId").isEqualTo(PRODUCT_ID_OK);
	}

	@Test
	public void shouldGetEmptyResponse_whenProductNotFound() {
		webTestClient.get()
				.uri(String.format("/review?productId=%s", PRODUCT_ID_NOT_FOUND))
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void shouldGetInvalidResponse_whenProductIdIsInvalid() {
		webTestClient.get()
				.uri(String.format("/review?productId=%s", PRODUCT_ID_INVALID))
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo(String.format("Invalid productId: %s", PRODUCT_ID_INVALID));
	}

	@Test
	public void shouldGetBadRequest_whenProductIdIsOfWrongType() {
		webTestClient.get()
				.uri(String.format("/review?productId=%s", "PRODUCT_ID_INVALID"))
				.exchange()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review");
	}

}
