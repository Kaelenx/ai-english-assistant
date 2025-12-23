# AI English Assistant

AI-powered English learning assistant built as a Java microservices architecture.

## Project Structure

This is a Maven mono-repository containing multiple services and shared libraries:

```
ai-english-assistant/
├── libs/
│   ├── common-id/              # Snowflake ID generator library
│   └── common-events/          # Event envelope and constants
├── services/
│   ├── conversation-service/   # Conversation management service (Port: 8080)
│   └── ai-orchestrator-service/# AI orchestration service (Port: 8081)
└── pom.xml                     # Root Maven configuration
```

## Prerequisites

Before you begin, ensure you have the following installed:

- **JDK 17 or higher** - [Download from Adoptium](https://adoptium.net/)
- **Maven 3.6+** - [Download from Apache Maven](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** - [Download from MySQL](https://dev.mysql.com/downloads/mysql/)

### Verify Installation

```bash
java -version    # Should show Java 17 or higher
mvn -version     # Should show Maven 3.6+
mysql --version  # Should show MySQL 8.0+
```

## Database Setup

### 1. Start MySQL Server

Make sure your MySQL server is running.

### 2. Create Databases

Connect to MySQL and create two databases:

```bash
mysql -u root -p
```

Then execute:

```sql
CREATE DATABASE conversation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE ai_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Verify databases were created
SHOW DATABASES;
```

Exit MySQL:
```sql
EXIT;
```

### 3. Database Migration

The applications use Flyway for database migrations. Tables will be created automatically when the services start for the first time.

**Conversation Service** creates:
- `conversation` table - Stores conversation metadata
- `message` table - Stores messages in conversations

**AI Orchestrator Service** creates:
- `ai_request_log` table - Tracks all AI service calls for billing/usage

## Environment Configuration

Both services require environment variables for configuration. You can set them in your IDE or shell.

### Required Environment Variables

#### For Both Services:

```bash
# Snowflake ID Generator Worker ID (0-1023)
# Use different values for different service instances
export WORKER_ID=0
```

#### For Conversation Service:

```bash
# Database Configuration
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/conversation_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="your_mysql_password"

# AI Orchestrator Service URL
export AI_ORCHESTRATOR_BASE_URL="http://localhost:8081"

# Optional: Server Port (default: 8080)
export SERVER_PORT=8080
```

#### For AI Orchestrator Service:

```bash
# Database Configuration
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/ai_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="your_mysql_password"

# Optional: Server Port (default: 8081)
export SERVER_PORT=8081

# Optional: Qwen Configuration (for future real integration)
export QWEN_API_KEY="your-qwen-api-key"
export QWEN_MODEL="qwen-turbo"
export QWEN_TIMEOUT_MS=30000
```

### Setting Environment Variables in IDE

#### IntelliJ IDEA:

1. Open Run/Debug Configurations
2. Select your application
3. Add environment variables in the "Environment variables" field:
   ```
   WORKER_ID=0;SPRING_DATASOURCE_PASSWORD=your_password;AI_ORCHESTRATOR_BASE_URL=http://localhost:8081
   ```

#### VS Code:

Add to `.vscode/launch.json`:
```json
{
  "configurations": [
    {
      "type": "java",
      "name": "ConversationApplication",
      "env": {
        "WORKER_ID": "0",
        "SPRING_DATASOURCE_PASSWORD": "your_password",
        "AI_ORCHESTRATOR_BASE_URL": "http://localhost:8081"
      }
    }
  ]
}
```

## Building the Project

Build all modules from the root directory:

```bash
cd ai-english-assistant
mvn clean install
```

This will:
1. Build the common libraries (common-id, common-events)
2. Build both services
3. Run all tests
4. Create executable JAR files

## Running the Services

### Option 1: Using Maven (Recommended for Development)

Start services in separate terminals:

**Terminal 1 - AI Orchestrator Service:**
```bash
cd services/ai-orchestrator-service
mvn spring-boot:run
```

Wait for it to start (you'll see "Started AiOrchestratorApplication" in the logs).

**Terminal 2 - Conversation Service:**
```bash
cd services/conversation-service
mvn spring-boot:run
```

### Option 2: Using Executable JARs

After building, you can run the JAR files:

**Terminal 1:**
```bash
java -jar services/ai-orchestrator-service/target/ai-orchestrator-service-0.1.0-SNAPSHOT.jar
```

**Terminal 2:**
```bash
java -jar services/conversation-service/target/conversation-service-0.1.0-SNAPSHOT.jar
```

### Option 3: Using IDE

#### IntelliJ IDEA:

1. Import the project as a Maven project
2. Navigate to `AiOrchestratorApplication.java` → Right-click → Run
3. Navigate to `ConversationApplication.java` → Right-click → Run

Make sure to configure environment variables in Run/Debug Configurations!

## Testing the Services

### 1. Verify Services are Running

Check the logs to ensure both services started successfully:

- **AI Orchestrator Service**: Should be running on port 8081
- **Conversation Service**: Should be running on port 8080

### 2. Test with curl

#### Create a Conversation

```bash
curl -X POST http://localhost:8080/v1/conversations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "sceneId": 1,
    "difficulty": "EASY",
    "planTier": "FREE"
  }'
```

**Expected Response:**
```json
{
  "conversationId": 1234567890123456789,
  "status": "ACTIVE"
}
```

#### Send a Text Message

Replace `{conversationId}` with the ID from the previous response:

```bash
curl -X POST http://localhost:8080/v1/conversations/{conversationId}/messages:text \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "text": "Hello! I want to practice English."
  }'
```

**Expected Response:**
```json
{
  "userMessageId": 1234567890123456790,
  "assistantMessageId": 1234567890123456791,
  "replyText": "Hello! I'm your AI English assistant. How can I help you practice English today?"
}
```

### 3. Test with Postman

1. Import the following requests into Postman:

**POST Create Conversation:**
- URL: `http://localhost:8080/v1/conversations`
- Headers: 
  - `Content-Type: application/json`
  - `X-User-Id: 1`
- Body (raw JSON):
  ```json
  {
    "sceneId": 1,
    "difficulty": "EASY",
    "planTier": "FREE"
  }
  ```

**POST Send Message:**
- URL: `http://localhost:8080/v1/conversations/{conversationId}/messages:text`
- Headers:
  - `Content-Type: application/json`
  - `X-User-Id: 1`
- Body (raw JSON):
  ```json
  {
    "text": "Hello! I want to practice English."
  }
  ```

## Running Tests

Run all tests:

```bash
mvn test
```

Run tests for a specific module:

```bash
cd libs/common-id
mvn test

cd services/conversation-service
mvn test

cd services/ai-orchestrator-service
mvn test
```

## Architecture Overview

### Conversation Flow

1. **Client** → Creates conversation via REST API → **Conversation Service**
2. **Client** → Sends message → **Conversation Service**
3. **Conversation Service** → Saves user message to database
4. **Conversation Service** → Calls AI orchestrator via OpenFeign → **AI Orchestrator Service**
5. **AI Orchestrator Service** → Gets AI response (currently mock) → Returns to Conversation Service
6. **AI Orchestrator Service** → Logs request to `ai_request_log` table
7. **Conversation Service** → Saves assistant message → Returns response to Client

### Key Features

- **Snowflake ID Generation**: Distributed unique ID generation using worker IDs
- **Database Migrations**: Automatic schema management with Flyway
- **Mock AI Provider**: Returns configurable mock responses for quick testing
- **Request Logging**: All AI requests are logged for billing/usage tracking
- **OpenFeign Integration**: Type-safe HTTP client for inter-service communication

## AI Provider Integration

Currently, the AI Orchestrator Service uses a **mock provider** that returns pre-configured responses. This allows you to test the complete flow without an actual AI service.

### Future Integration with Qwen (Tongyi)

The code has clear extension points for integrating with the Qwen (Tongyi) official Java SDK:

**File**: `services/ai-orchestrator-service/src/main/java/com/kaelenx/aiorchestrator/provider/QwenLlmProvider.java`

**TODO Section** shows where to add the Qwen SDK integration:
```java
// TODO: Replace with actual Qwen SDK call
// Example integration point:
// 1. Build Qwen API request with request.getUserText() and request.getHistory()
// 2. Call Qwen API: Generation gen = Generation.builder()...build();
// 3. GenerationResult result = gen.call(...)
// 4. Extract tokens from result.getUsage()
```

**Required Configuration** (already in `application.yml`):
```yaml
qwen:
  api:
    key: ${QWEN_API_KEY:mock-api-key}
    model: ${QWEN_MODEL:qwen-turbo}
    timeout-ms: ${QWEN_TIMEOUT_MS:30000}
```

## Troubleshooting

### Database Connection Issues

**Problem**: `Access denied for user 'root'@'localhost'`

**Solution**: 
- Verify MySQL password in environment variables
- Ensure MySQL user has proper permissions:
  ```sql
  GRANT ALL PRIVILEGES ON conversation_db.* TO 'root'@'localhost';
  GRANT ALL PRIVILEGES ON ai_db.* TO 'root'@'localhost';
  FLUSH PRIVILEGES;
  ```

### Port Already in Use

**Problem**: `Port 8080 is already in use`

**Solution**: 
- Change port using environment variable:
  ```bash
  export SERVER_PORT=8082
  ```
- Or kill the process using the port:
  ```bash
  # Find process
  lsof -i :8080
  # Kill process
  kill -9 <PID>
  ```

### Flyway Migration Errors

**Problem**: `Flyway migration failed`

**Solution**:
- Drop and recreate the databases:
  ```sql
  DROP DATABASE conversation_db;
  DROP DATABASE ai_db;
  CREATE DATABASE conversation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  CREATE DATABASE ai_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

### Build Errors

**Problem**: `Could not resolve dependencies`

**Solution**:
```bash
# Clean and rebuild
mvn clean install -U
```

## Project Details

### Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.1
- **MyBatis-Plus**: 3.5.5 (ORM)
- **OpenFeign**: 13.1 (HTTP Client)
- **Flyway**: 10.4.1 (Database Migration)
- **MySQL**: 8.0+ (Database)
- **Lombok**: 1.18.30 (Code Generation)

### Database Schema

#### conversation_db

**conversation**
- `id` (BIGINT, PK): Snowflake ID
- `user_id` (BIGINT): User identifier
- `scene_id` (BIGINT): Scene/scenario identifier
- `difficulty` (VARCHAR): Difficulty level
- `status` (VARCHAR): Conversation status
- `plan_tier` (VARCHAR): User's plan tier
- `deleted_at`, `created_at`, `updated_at` (TIMESTAMP)

**message**
- `id` (BIGINT, PK): Snowflake ID
- `conversation_id` (BIGINT, FK): References conversation
- `sender_role` (VARCHAR): USER or ASSISTANT
- `content_type` (VARCHAR): TEXT, IMAGE, or AUDIO
- `text_content` (TEXT): Message content
- `status` (VARCHAR): Message status
- `provider_trace` (TEXT): AI provider metadata (JSON)
- `deleted_at`, `created_at`, `updated_at` (TIMESTAMP)

#### ai_db

**ai_request_log**
- `id` (BIGINT, PK): Snowflake ID
- `conversation_id`, `user_id`, `scene_id` (BIGINT)
- `difficulty`, `plan_tier` (VARCHAR)
- `provider`, `model`, `status` (VARCHAR)
- `token_in`, `token_out` (INT): Token usage (nullable for mock)
- `latency_ms` (BIGINT): Request latency
- `error_message` (TEXT): Error details if failed
- `created_at` (TIMESTAMP)

## Contributing

When adding new features:

1. Follow the existing code structure
2. Add appropriate tests
3. Update this README if adding new configuration or endpoints
4. Use database-neutral SQL where possible (for future PostgreSQL migration)

## License

This project is for educational and development purposes.
