# API Rate Limiting Demo (Bucket4j In-Memory)

Simple Spring Boot demo project illustrating API Rate Limiting using Bucket4j with an In-Memory stategy.

## Run the Project

1.  **Clone Repository:**

    ```bash
    git clone https://github.com/GiangPhanNguyenTra/grow-together.git
    git fetch
    git checkout -b api-rate-limiting
    ```

2.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application runs on port 8080.

## Testing Rate Limiting

Use `curl` or Postman/Insomnia.

1.  **Endpoint:** `GET http://localhost:8080/api/greeting`
2.  **Header:** Requires `X-api-key` (e.g., `FREE-USER-123`, `BSC-USER-456`, `PRO-USER-789`).
3.  **Send requests continuously:**

    ```bash
    curl -v -H "X-api-key: FREE-USER-123" http://localhost:8080/api/greeting
    ```

4.  **Observe:**
    - **Success (200 OK):** The `X-Rate-Limit-Remaining` header value decreases.
    - **Limit Exceeded (429 Too Many Requests):**
      - Error JSON contains the `"message"` field.
      - `X-Rate-Limit-Remaining: 0` header.
      - `Retry-After: <seconds>` header.

**_Thank you for visiting!_**
