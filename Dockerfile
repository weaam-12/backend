FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# نسخ الملفات الأساسية
COPY mvnw pom.xml ./
COPY src ./src

# تثبيت Maven مباشرة (بدون الاعتماد على .mvn)
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline

# بناء التطبيق
RUN mvn package -DskipTests

# التشغيل
CMD ["java", "-jar", "target/service-management-0.0.1-SNAPSHOT.jar"]