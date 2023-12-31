# Custom-ControllerAndORM
Custom-ControllerAndORM - Java Servlet
本專案是一個模仿類似 Spring Framework 的 Java Servlet 框架，內容包括了以下功能：

與 Spring 相似的 `@RestController` 、 `@RequestMapping` 、`@Service` 、`@Component` 、`@Entity` 、`@Table` 、`@Autowired`、 `@Id` 、 `@Transaction`等自定義的 Annotation。
模擬 JPA 的 `@Entity` 操作及交易控制的 `@Transactional` Annotation。

# 功能說明
1. RestController - 
`@MyRestController` 可以讓一個 Java 類別變成一個 RESTful 的 Web Service Controller。與 Spring 相似，透過 @RestController 告訴框架，這個類別需要被作為一個 RESTful 的 Web Service Controller 來對外提供服務。

2. RequestMapping -
`@MyRequestMapping` 可以指定一個 URL，透過這個 URL 可以呼叫指定的 Controller 方法。與 Spring 相似，可以指定 URL 路徑、HTTP 方法、Content-Type 等等。

3. Service - 
`@MyService` 定義新的服務實現，並在啟動過程中將它們自動檢測為Spring Bean。

4. Component -
`@MyComponent` 掃描我們的應用程序以查找帶有@Component 、實例化它們並將任何指定的依賴項注入到它們中、隨時隨地註入。

5. Autowired -
`@MyAutowired` 自動裝配指的就是使用將Spring容器中的bean自動的和我們需要這個bean的類組裝在一起。

6. Entity - 
`@MyEntity` 使用自定義的注解Entity來模擬JPA的實體操作。

7. Table - 
`@MyTable` 參數name來對應資料表的TableName。

8. Id -
`@MyId` 是屬性或方法級別的註解，該註解沒有參數，用於標註焦點的主鍵（映射到數據庫表的主鍵）。

9. Value - 
`@MyValue`  Spring框架中的一个常用功能,其作用是通過註解將常量、配置文件中的值、其他bean的属性值注入到變量中，作爲變量的初始值。

10. Controller -
`@MyController` 作用是將用戶提交來的請求通過URL匹配，分發給不同的接收器（具體的Controller），該接收器會對其進行相應處理，然後返回處理結果。

11. Column -
`@MYColumn` 對應到 Table 的欄位中的欄位名稱。

12. RequestParam -
`@RequestParam` 是Spring Framework 框架中的註解，用於將請求中的參數綁定到方法的參數。

13. RequestBody -
`@RequestBody` 主要用來接收前端使用POST傳給後端的json字串中的資料。

14. Transactional -
`@MyTransactional` 注解實現交易控制，當出現異常時會自動回滾。

# API URL 列表
- 查詢全部人員帳號
```
emp/query 
```

- 新增人員帳號
```
/emp/insert/{id}
```

- 刪除指定人員帳號
```
/emp/delete/{id}
```

- 測試insert錯誤時啟動Transaction機制rollback
```
/emp/insert/TransactionTest
```

# 注意事項：
在使用自定義的 annotation 時，需要注意命名和使用方式，避免和其他框架的 annotation 重名或混淆。
在使用 JPA 的 Entity 操作時，需要確保 Entity 和數據庫表的映射正確，否則會報錯。
在使用 Transactional annotation 進行交易控制時，需要確保事務管理器的配置正確，否則事務可能無法正常提交或回滾。
