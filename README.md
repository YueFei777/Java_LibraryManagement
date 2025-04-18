# web-backend program

## 介绍

山东大学崇新学堂开放创新实践项目-图书馆管理系统后端

## Introduction

This is the backend of a library management system in JAVA for the Open Innovation Practice Project of Chongxin College, Shandong University.

## 项目作者

梁昱洋

## Project Author

Liang Yuyang

## 项目依赖（基于swagger进行调试）

Springboot 3+ , jdk17+, Mybatis, openAPI 3

## Project Dependencies (Debugged based on Swagger)

Spring Boot 3+, JDK 17+, MyBatis, OpenAPI 3

## 项目亮点

1. 完善的安全管理体系
   - 严格的用户认证：采用先进的身份验证机制，确保只有合法用户能够访问系统。支持多种认证方式，如用户名 / 密码、OAuth 等，为用户提供了灵活、安全的登录体验；
   - 细粒度的权限管理：实现了细粒度的权限控制，对不同用户角色（如管理员、读者等）分配不同的操作权限。通过注解和拦截器的方式，确保用户只能访问其具有权限的功能模块，有效保护了图书馆数据的安全性和隐私性；
   - 全面的异常处理：完善的异常处理机制能够捕获和处理系统运行过程中出现的各种异常情况，并返回友好的错误信息。同时，日志记录功能方便开发人员进行问题排查和系统监控，确保系统的稳定性和可靠性。
2. 丰富的业务功能接口
   - 图书管理功能：提供了涵盖图书添加、修改、删除、查询等全面的图书管理接口。支持多条件查询，如图书名称、作者、ISBN 等，方便图书馆管理人员快速定位和管理图书信息。同时，自动记录图书的添加和更新时间，确保数据的准确性和及时性；
   - 读者管理功能：支持读者的注册、登录、信息修改等操作，方便读者使用图书馆服务。同时，对读者的借阅信息进行全面管理，包括借阅记录查询、逾期提醒等，提高了图书馆的服务质量和管理效率；
   - 借还记录管理功能：详细记录图书的借阅和归还信息，支持按读者、图书、时间等条件进行查询和统计。提供借阅和归还操作接口，确保借还流程的顺畅和准确。同时，对逾期情况进行自动监测和处理，提醒读者及时归还图书。
3. 高效的数据处理能力
   - Excel 导入导出功能：提供了 Excel 文件的导入导出功能，方便图书馆管理人员进行数据的批量处理。通过 XlsUtils 工具类，系统可以将数据库中的数据导出为 Excel 文件，也可以将 Excel 文件中的数据导入到数据库中，大大提高了数据处理的效率；
   - 分页查询和大数据处理：在数据查询方面，支持分页查询功能，能够将大量数据进行分页展示，避免了数据加载过慢的问题。同时，对大数据量的处理进行了优化，确保系统在高并发情况下的性能稳定。
4. 友好的用户体验
   - 统一的响应格式：通过 `MyControllerAdvice` 类，对系统的响应数据进行统一处理，确保所有接口返回的数据格式一致。这使得前端开发人员可以更加方便地处理和展示数据，提高了开发效率和用户体验;
   - 清晰的错误提示：在异常处理过程中，系统会返回清晰、友好的错误提示信息，帮助用户快速定位和解决问题。同时，日志记录功能方便开发人员进行问题排查和系统优化。

### Project Highlights

1. Comprehensive Security Management System
    - Rigorous User Authentication: Advanced authentication mechanisms are adopted to ensure that only legitimate users can access the system. Multiple authentication methods, such as username/password and OAuth, are supported, providing users with a flexible and secure login experience.
    - Fine - grained Permission Management: Fine - grained permission control is implemented. Different operation permissions are assigned to different user roles (e.g., administrators, readers). Through annotations and interceptors, users can only access the functional modules they have permission for, effectively protecting the security and privacy of library data.
    - Comprehensive Exception Handling: A comprehensive exception - handling mechanism can capture and handle various exceptions that occur during system operation and return user - friendly error messages. Meanwhile, the logging function facilitates developers to troubleshoot problems and monitor the system, ensuring the stability and reliability of the system.

2. Rich Business Function Interfaces
    - Book Management Function: Comprehensive book management interfaces are provided, including book addition, modification, deletion, and query. Multi - condition queries, such as by book title, author, and ISBN, are supported, which enables library administrators to quickly locate and manage book information. Additionally, the addition and update times of books are automatically recorded to ensure the accuracy and timeliness of data.
    - Reader Management Function: Operations such as reader registration, login, and information modification are supported, making it convenient for readers to use library services. At the same time, comprehensive management of readers' borrowing information is carried out, including borrowing record query and overdue reminder, which improves the service quality and management efficiency of the library.
    - Borrowing and Returning Record Management Function: Detailed records of book borrowing and returning information are maintained, supporting queries and statistics by reader, book, and time. Borrowing and returning operation interfaces are provided to ensure the smooth and accurate borrowing and returning process. Moreover, overdue situations are automatically monitored and handled, reminding readers to return books in time.

3. High - efficiency Data Processing Capability
    - Excel Import and Export Function: An Excel file import and export function is provided, which facilitates library administrators to perform batch data processing. Through the `XlsUtils` utility class, the system can export data from the database to an Excel file and import data from an Excel file into the database, greatly improving the efficiency of data processing.
    - Pagination Query and Big Data Processing: In terms of data query, the pagination query function is supported, which can display a large amount of data in pages, avoiding the problem of slow data loading. At the same time, the processing of large data volumes is optimized to ensure the system's performance stability under high concurrency.

4. User - friendly Experience
    - Unified Response Format: Through the `MyControllerAdvice` class, the system's response data is uniformly processed to ensure that all interfaces return data in a consistent format. This makes it more convenient for front - end developers to process and display data, improving development efficiency and user experience.
    - Clear Error Messages: During the exception handling process, the system returns clear and user - friendly error messages to help users quickly locate and solve problems. Meanwhile, the logging function facilitates developers to troubleshoot problems and optimize the system.
