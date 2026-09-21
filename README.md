# GraphQL Product Manager

Bài tập môn **Lập trình Web (WEBPR330479)** — HCMUTE, GVHD: ThS. Nguyễn Hữu Trung.
Quản lý **Category – User – Product** bằng **GraphQL API** trên **Spring Boot**, giao diện web dùng **AJAX (fetch)** — không reload trang.

## Công nghệ
Java 21 · Spring Boot 3.5 (Web, GraphQL, Data JPA, Validation) · H2 (nhúng, tự tạo file DB) · HTML/JS thuần (fetch, không framework)

## Mô hình dữ liệu
- `Category(id, name, images)` — N–N với `User`, 1–N với `Product`
- `User(id, fullname, email, password, phone)` — N–N với `Category`
- `Product(id, title, quantity, desc, price, userid)` — N–1 với `Category` và `User`

## Chức năng GraphQL
- `products` — tất cả sản phẩm, **giá tăng dần**
- `productsByCategory(categoryId)` — sản phẩm theo 1 danh mục
- CRUD đầy đủ cho `Product` và `Category` (`create/update/delete`), có validate + trả lỗi rõ ràng

## Chạy thử
```bash
mvn spring-boot:run
```
- Giao diện AJAX: `http://localhost:8080/`
- GraphQL endpoint: `http://localhost:8080/graphql`
- GraphiQL: `http://localhost:8080/graphiql`

## Ví dụ query
```graphql
{ products { id title price category { name } } }
{ productsByCategory(categoryId: 1) { id title price } }
mutation { createProduct(input: { title: "Chuột", quantity: 20, price: 250000, userId: 1, categoryId: 2 }) { id } }
```

## Cấu trúc
```
src/main/java/vn/iotstar/catalog/
├── entity/        # Category, Product, User (JPA)
├── repository/    # Spring Data JPA
├── dto/           # Input (ghi) / View (đọc)
├── service/       # nghiệp vụ + transaction
└── controller/     # @QueryMapping / @MutationMapping + xử lý lỗi
src/main/resources/
├── graphql/schema.graphqls
└── static/        # index.html + app.js (AJAX) + styles.css + images/
```
