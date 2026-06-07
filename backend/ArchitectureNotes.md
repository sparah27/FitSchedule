# FitSchedule Backend — Architecture Notes

## Architectural Pattern: Layered Architecture

The backend follows a strict **Layered Architecture** (also known as N-Tier Architecture), separating concerns into discrete layers where each layer only depends on the layer directly below it.

```
┌─────────────────────────────────────┐
│           Controller Layer          │  HTTP in/out, request validation, auth extraction
│  (AuthController, BookingController │
│   TrainerController, AdminController│
│   TrainerSelfController, etc.)      │
├─────────────────────────────────────┤
│            Service Layer            │  Business logic, transaction boundaries
│  (BookingService, TrainerService,   │
│   ReviewService, AdminService, etc.)│
├─────────────────────────────────────┤
│          Repository Layer           │  Data access via Spring Data JPA
│  (BookingRepository, UserRepository │
│   TrainerRepository, etc.)          │
├─────────────────────────────────────┤
│            Database Layer           │  MySQL 8.4 managed by Flyway migrations
└─────────────────────────────────────┘
```

### Package Structure

| Package | Responsibility |
|---|---|
| `config` | Spring Security, CORS, OpenAPI configuration |
| `controller` | REST endpoints — delegates all logic to services |
| `service` | Business rules, orchestration, transactional boundaries |
| `repository` | Spring Data JPA interfaces — no implementation required |
| `model/entity` | JPA entities mapped to database tables |
| `model/enums` | Shared domain enumerations |
| `dto/request` | Inbound data shapes (validated with Jakarta Bean Validation) |
| `dto/response` | Outbound data shapes (never expose entities directly) |
| `security` | JWT filter, UserDetails, authentication wiring |
| `exception` | Custom exceptions + global exception handler |
| `event` | Spring application events for decoupled notifications |
| `event/listener` | Event subscribers (Observer Pattern) |
| `service/filter` | Composable trainer filter strategies (Strategy Pattern) |

---

## Design Patterns

### Pattern 1 — Strategy Pattern (Trainer Filtering)

**Problem:** `TrainerService.getAllTrainers()` started with an if/else chain to handle different filter combinations (specialization, date range). Adding new filter types required modifying the service directly.

**Solution:** A `TrainerFilterStrategy` interface with `isApplicable(FilterCriteria)` and `filter(List<Trainer>, FilterCriteria)` methods. Each filter is a separate `@Component` that Spring injects as a `List<TrainerFilterStrategy>`. The service iterates and applies only applicable strategies.

**Implementations:**
- `SpecializationFilterStrategy` — filters trainers by exact specialization (case-insensitive)
- `DateRangeFilterStrategy` — keeps only trainers with AVAILABLE slots in a given date range

**Benefit:** New filter types (e.g., rating, distance) can be added by creating a new `@Component` without touching `TrainerService`.

---

### Pattern 2 — Observer Pattern (Booking Notifications)

**Problem:** `BookingService` was directly calling `NotificationService` for every booking event, coupling business logic to notification delivery. Testing required mocking `NotificationService`.

**Solution:** Spring's `ApplicationEventPublisher` decouples the two concerns. `BookingService` publishes `BookingCreatedEvent` or `BookingCancelledEvent`. Dedicated listener classes subscribe:

- `ClientNotificationListener` — sends notifications to the booking's client
- `TrainerNotificationListener` — sends notifications to the booking's trainer

**Benefit:** `BookingService` has no knowledge of how notifications are delivered. Listeners can be added, removed, or modified independently. Both listeners are tested in isolation.

---

## Security Model

- **JWT Bearer tokens** — stateless, 24-hour expiration
- **Role-based access** via `@PreAuthorize` on controller methods
  - `ROLE_CLIENT` — booking operations, review submission
  - `ROLE_TRAINER` — schedule, availability, complete/block endpoints
  - `ROLE_ADMIN` — trainer management, system statistics
- **Single Table Inheritance** — `users` table stores CLIENT, TRAINER, and ADMIN rows identified by `user_type` discriminator

## Database Migrations

All schema changes are managed by **Flyway** (sequential versioned SQL files in `src/main/resources/db/migration/`). Migrations are never edited after being applied to a shared environment — new changes require a new version file.
