# Flight Booking Service

一个基于 Quarkus 的航班预订系统 RESTful API 服务。

## 功能特性

### 核心功能
- **Customer Management (客户管理)**: 创建、查询、更新、删除客户
- **Commodity Management (商品/航班管理)**: 创建、查询、更新、删除商品（航班）
- **Booking Management (预订管理)**: 创建、查询、取消预订

### 高级特性
- **级联删除**: 删除客户或商品时，自动删除相关的预订记录
- **GuestBooking 端点**: 使用 JTA API 手动管理事务，在单一事务中创建客户和预订
- **Swagger UI**: 完整的 API 文档和交互式测试界面
- **REST Assured 测试**: 全面的单元测试和集成测试

## 技术栈

- **框架**: Quarkus 3.2.9
- **Java 版本**: Java 17
- **数据库**: H2 (内存数据库)
- **ORM**: Hibernate with Panache
- **API 文档**: OpenAPI/Swagger
- **测试**: JUnit 5 + REST Assured
- **事务管理**: JTA (Java Transaction API)

## 本地运行

### 前置要求
- Java 17
- Maven 3.9+

### 运行步骤

1. **克隆项目**
```bash
git clone <your-repo-url>
cd test11
```

2. **启动应用 (开发模式)**
```bash
./mvnw quarkus:dev
```

或者在 Windows 上:
```bash
mvnw.cmd quarkus:dev
```

3. **访问应用**
- API 端点: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui
- OpenAPI 规范: http://localhost:8080/openapi

### 运行测试
```bash
./mvnw test
```

## API 端点

### Customers (客户)
- `GET /customers` - 获取所有客户
- `GET /customers/{id}` - 根据 ID 获取客户
- `POST /customers` - 创建新客户
- `PUT /customers/{id}` - 更新客户
- `DELETE /customers/{id}` - 删除客户（级联删除相关预订）

### Commodities (商品/航班)
- `GET /commodities` - 获取所有商品
- `GET /commodities/available` - 获取有库存的商品
- `GET /commodities/{id}` - 根据 ID 获取商品
- `POST /commodities` - 创建新商品
- `PUT /commodities/{id}` - 更新商品
- `DELETE /commodities/{id}` - 删除商品（级联删除相关预订）

### Bookings (预订)
- `GET /bookings` - 获取所有预订
- `GET /bookings/{id}` - 根据 ID 获取预订
- `GET /bookings/customer/{customerId}` - 获取某客户的所有预订
- `POST /bookings?customerId={id}&commodityId={id}` - 创建新预订
- `DELETE /bookings/{id}` - 取消预订

### Guest Bookings (访客预订)
- `POST /guest-bookings` - 在单一事务中创建客户和预订（使用 JTA 手动事务管理）

## API 使用示例

### 创建客户
```bash
curl -X POST http://localhost:8080/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "1234567890"
  }'
```

### 创建预订
```bash
curl -X POST "http://localhost:8080/bookings?customerId=1&commodityId=1"
```

### 创建访客预订（事务性）
```bash
curl -X POST http://localhost:8080/guest-bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {
      "firstName": "Guest",
      "lastName": "User",
      "email": "guest@example.com",
      "phoneNumber": "9876543210"
    },
    "commodityId": 1
  }'
```

## 数据模型

### Customer (客户)
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890"
}
```

### Commodity (商品/航班)
```json
{
  "id": 1,
  "name": "Flight to London",
  "description": "Direct flight from NYC to London",
  "price": 599.99,
  "quantity": 50
}
```

### Booking (预订)
```json
{
  "id": 1,
  "bookingDate": "2025-11-12",
  "customer": { ... },
  "commodity": { ... }
}
```

## 部署到 OpenShift

### 前置条件
1. 已注册 Red Hat OpenShift 账号
2. 已配置 OpenShift 项目
3. 代码已推送到 GitHub

### 部署步骤

参考教程文档：
- [OpenShift 初始设置](https://github.com/NewcastleComputingScience/CSC8104-Quarkus-Specification/blob/main/tutorial.asciidoc)
- [重新部署指南](https://github.com/NewcastleComputingScience/CSC8104-Quarkus-Specification/blob/main/serverless-redeploy.md)

### 快速部署流程

1. **推送代码到 GitHub**
```bash
git add .
git commit -m "Initial commit"
git push origin main
```

2. **在 OpenShift 中创建应用**
- 登录 OpenShift 控制台
- 选择 "Import from Git"
- 输入 GitHub 仓库 URL
- 选择 "Knative Service"
- 点击 "Create"

3. **每次更新代码后重新部署**
- 推送代码到 GitHub
- 在 OpenShift 中触发新构建 (Builds → BuildConfigs → Start build)
- 更新 Knative Service 的 `buildVersion` 注解以触发重新部署

## 项目结构

```
test11/
├── src/
│   ├── main/
│   │   ├── java/uk/ac/newcastle/enterprisemiddleware/
│   │   │   ├── entity/          # 实体类
│   │   │   ├── repository/      # 数据访问层
│   │   │   ├── service/         # 业务逻辑层
│   │   │   └── rest/            # REST 端点
│   │   │       └── dto/         # 数据传输对象
│   │   └── resources/
│   │       ├── application.properties
│   │       └── import.sql       # 初始数据
│   └── test/
│       └── java/                # REST Assured 测试
├── .s2i/                        # OpenShift S2I 配置
├── .mvn/                        # Maven wrapper
├── pom.xml
└── README.md
```

## 特殊实现说明

### 级联删除
使用 JPA 的 `@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)` 注解实现。
删除 Customer 或 Commodity 时，所有相关的 Booking 会自动删除。

### JTA 事务管理
`GuestBookingResource` 使用 `UserTransaction` API 手动管理事务：
- 手动开始事务 (`userTransaction.begin()`)
- 执行业务操作
- 成功则提交 (`userTransaction.commit()`)
- 失败则回滚 (`userTransaction.rollback()`)

## 开发者信息

- **课程**: CSC8104 Enterprise Software and Services
- **学校**: Newcastle University

## 许可证

本项目仅用于教育目的。

