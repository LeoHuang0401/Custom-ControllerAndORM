# Custom-ControllerAndORM
Custom-ControllerAndORM
Custom-ControllerAndORM - Java Servlet
本專案是一個模仿類似 Spring Framework 的 Java Servlet 框架，內容包括了以下功能：

與 Spring 相似的 @RestController、@RequestMapping、@Id、@Transaction等自定義的 Annotation。
模擬 JPA 的 Entity 操作及交易控制的 @Transactional Annotation。
功能說明
RestController
@MyRestController 可以讓一個 Java 類別變成一個 RESTful 的 Web Service Controller。與 Spring 相似，透過 @RestController 告訴框架，這個類別需要被作為一個 RESTful 的 Web Service Controller 來對外提供服務。

RequestMapping
@MyRequestMapping 可以指定一個 URL，透過這個 URL 可以呼叫指定的 Controller 方法。與 Spring 相似，可以指定 URL 路徑、HTTP 方法、Content-Type 等等。

Id
@MyId 是屬性或方法級別的註解，該註解沒有參數，用於標註焦點的主鍵（映射到數據庫表的主鍵）

Transactional
@MyTransactional 注解實現交易控制，當出現異常時會自動回滾。
