FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# نسخ الملفات الأساسية
COPY pom.xml .
COPY src ./src

# تثبيت Maven
RUN apt-get update && apt-get install -y maven

# بناء التطبيق
RUN mvn package -DskipTests

# التشغيل
CMD ["java", "-jar", "target/service-management-0.0.1-SNAPSHOT.jar"]