# Bumper

**Bumper** is a lightweight, developer-friendly Java library that simplifies working with relational and NoSQL databases. It offers a clean, fluent API for connecting to databases, building queries, executing them, and managing results — with support for popular databases like MySQL, PostgreSQL, SQLite, MongoDB, Redis, and more.

---

## Features

- Simple and pluggable database connectors  
- Fluent Query Builder API (SQL-style syntax)  
- Support for multiple databases  
- Modular and extensible architecture  
- Built-in connection pooling via HikariCP  
- JUnit testable and lightweight  
- Supports Maven, Gradle, UberJar, and plain Java projects  
- Well-documented with JavaDocs and GitHub Wiki  

---

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.amosnyirenda</groupId>
    <artifactId>bumper</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
Add this to your build.gradle dependencies block:

```xml
implementation 'com.amosnyirenda:bumper:1.0.0'
```

### UberJar (Manual Installation)
Download the latest bumper.jar file from the Releases page.
Compile and run your application using:

```xml
javac -cp bumper.jar YourApp.java
java -cp bumper.jar:. YourApp
```
Plain Java Project (Without Maven or Gradle)
Place the bumper.jar in your project’s libs/ directory.

Ensure your IDE or command-line setup includes it in the classpath.


## Supported Databases
- MySQL
- PostgreSQL
- SQLite
- Oracle (Planned)
- SQL Server (Planned)
- MongoDB (Planned)
- Redis (Planned)

## Contributing
We welcome contributions!
Feel free to fork the repository, open issues, or submit pull requests with improvements.

## License
This project is licensed under the MIT License.

## Author
Amos Nyirenda
Email: amos.nyirenda@email.com
