# Custom-ControllerAndORM
Custom-ControllerAndORM - Java Servlet
本專案是一個模仿類似 Spring Framework 的 Java Servlet 框架，內容包括了以下功能：

與 Spring 相似的 `@RestController` 、 `@RequestMapping` 、 `@Id` 、 `@Transaction`等自定義的 Annotation。
模擬 JPA 的 Entity 操作及交易控制的 `@Transactional` Annotation。

# 功能說明
1. RestController - 
`@MyRestController` 可以讓一個 Java 類別變成一個 RESTful 的 Web Service Controller。與 Spring 相似，透過 @RestController 告訴框架，這個類別需要被作為一個 RESTful 的 Web Service Controller 來對外提供服務。

2. RequestMapping -
`@MyRequestMapping` 可以指定一個 URL，透過這個 URL 可以呼叫指定的 Controller 方法。與 Spring 相似，可以指定 URL 路徑、HTTP 方法、Content-Type 等等。

3. Id -
`@MyId` 是屬性或方法級別的註解，該註解沒有參數，用於標註焦點的主鍵（映射到數據庫表的主鍵）

4. Transactional -
`@MyTransactional` 注解實現交易控制，當出現異常時會自動回滾。

# API URL 列表
- 查詢全部人員帳號
```
/emp/query 
```
****
```
/emp/query?id={id}
```
EntityManager -> findAll()

- 新增、修改人員帳號
```
/emp/insert/{id}
```
EntityManager -> save()

- 刪除指定人員帳號
```
/emp/delete/{id}
```
EntityManager -> delete()

- 測試insert錯誤時啟動Transaction機制rollback
```
/emp/insert/TransactionTest
```
EntityManager -> save()

# 注意事項：
在使用自定義的 annotation 時，需要注意命名和使用方式，避免和其他框架的 annotation 重名或混淆。
在使用 JPA 的 Entity 操作時，需要確保 Entity 和數據庫表的映射正確，否則會報錯。
在使用 Transactional annotation 進行交易控制時，需要確保事務管理器的配置正確，否則事務可能無法正常提交或回滾。
