# GraphQL Product Manager — Bài tập Spring Boot + GraphQL + AJAX

Bài tập môn **Lập trình Web (WEBPR330479)** — HCMUTE, GVHD: ThS. Nguyễn Hữu Trung.

Ứng dụng quản lý **Category – User – Product** bằng **GraphQL API** viết trên **Spring Boot**, giao diện web dùng **AJAX (fetch)** gọi thẳng API và render lại trang mà không reload.

## 1. Mô hình dữ liệu

| Bảng | Trường | Quan hệ |
|---|---|---|
| `Category` | id, name, images | N–N với `User` (bảng trung gian `category_users`); 1–N với `Product` |
| `User` | id, fullname, email, password, phone | N–N với `Category` |
| `Product` | id, title, quantity, desc, price, userid | N–1 với `Category`, N–1 với `User` |

ORM: **Spring Data JPA** + **Hibernate**, chạy trên **H2** (file `./data/product-manager`, tự tạo khi chạy lần đầu). Dữ liệu mẫu được nạp tự động bởi `DemoData` (bật/tắt qua `app.seed-data` trong `application.properties`).

## 2. Công nghệ

- Java 21, Spring Boot 3.5 (`spring-boot-starter-web`, `spring-boot-starter-graphql`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`)
- GraphQL Java qua Spring for GraphQL — schema khai báo tại `src/main/resources/graphql/schema.graphqls`
- Frontend: HTML thuần + JavaScript (`fetch`) — không dùng framework, gọi thẳng endpoint `/graphql` bằng AJAX
- Cơ sở dữ liệu: H2 (nhúng, không cần cài đặt)

## 3. Các chức năng đã xử lý bằng GraphQL (theo yêu cầu đề bài)

- **Hiển thị tất cả product, giá tăng dần**: query `products` (`ORDER BY price ASC, id ASC`)
- **Lấy tất cả product của 1 category**: query `productsByCategory(categoryId: ID!)`
- **CRUD Category**: `createCategory`, `updateCategory`, `deleteCategory` (chặn xóa nếu category còn product)
- **CRUD Product**: `createProduct`, `updateProduct`, `deleteProduct`
- Validate dữ liệu đầu vào (`@NotBlank`, `@NotNull`, `@Min`, `@DecimalMin`…) và trả lỗi GraphQL rõ ràng qua `GraphQlExceptionResolver`

## 4. Cách chạy

```bash
mvn spring-boot:run
```

Mặc định chạy tại `http://localhost:8080`:

- Giao diện AJAX: `http://localhost:8080/`
- Endpoint GraphQL: `http://localhost:8080/graphql`
- GraphiQL (thử query trực tiếp): `http://localhost:8080/graphiql`

## 5. Ví dụ query/mutation

```graphql
# Tất cả sản phẩm, giá tăng dần
{ products { id title price quantity category { name } user { fullname } } }

# Sản phẩm theo 1 category
{ productsByCategory(categoryId: 1) { id title price } }

# Thêm sản phẩm
mutation {
  createProduct(input: { title: "Chuột không dây", quantity: 20, price: 250000, userId: 1, categoryId: 2 }) {
    id title
  }
}

# Sửa / xóa category
mutation { updateCategory(id: 1, input: { name: "Laptop", images: "/images/laptop.svg", userIds: [1,2] }) { id } }
mutation { deleteCategory(id: 3) }
```

## 6. Cấu trúc thư mục

```
src/main/java/vn/iotstar/catalog/
├── CatalogApplication.java
├── config/DemoData.java         # seed dữ liệu mẫu
├── controller/                  # @Controller GraphQL (QueryMapping/MutationMapping) + xử lý lỗi
├── dto/                         # Input (ghi) và View (đọc) tách biệt entity
├── entity/                      # Category, Product, User (JPA)
├── repository/                  # Spring Data JPA repository
└── service/CatalogService.java  # nghiệp vụ, transaction

src/main/resources/
├── application.properties
├── graphql/schema.graphqls      # schema GraphQL
└── static/                      # index.html + app.js (AJAX) + styles.css + images/
```

## 7. Ghi chú nộp bài

Repository GitHub commit thường xuyên theo từng phần (entity → schema → service/controller → frontend AJAX) để đáp ứng tiêu chí quản lý mã nguồn của môn học.
