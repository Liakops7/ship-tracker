# 🚢 Ship Tracker

A Spring Boot application that tracks vessels in the Aegean Sea in real time, using live AIS data from [AISStream.io](https://aisstream.io/).

When the application starts, it opens a WebSocket connection to AISStream and subscribes to `PositionReport` and `ShipStaticData` messages inside a bounding box covering the Aegean. Incoming JSON messages are mapped to typed Java records and kept in thread-safe in-memory stores. A REST API exposes the vessels currently in the Aegean and the ships expected to arrive at the port of Piraeus in the next 24 hours, with all times shown in Athens local time.

## ⚙️ How It Works

```
                       wss://stream.aisstream.io/v0/stream
                         (Aegean bounding box, live AIS)
                                       │
                                       ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                               Spring Boot                                │
│                                                                          │
│  AisStreamClient  (opens the WebSocket on startup)                       │
│          │                                                               │
│          ▼                                                               │
│  AisListener      (JSON → records, routes by message type)               │
│          │                                                               │
│          ├── PositionReport ──────────────────────────► VesselStore      │
│          │                                                               │
│          └── ShipStaticData ──► PiraeusMatcher + ETA ──► ArrivalStore    │
│                                                                          │
│  VesselController / ArrivalController  (HTTP layer, Athens local time)   │
└──────────────────────────────────┬───────────────────────────────────────┘
                                   │ JSON
                                   ▼
                    GET /vessels/aegean
                    GET /ports/piraeus/arrivals
```

### Piraeus arrival rules

A `ShipStaticData` message becomes an arrival only if it passes all of these rules:

1. **Destination is Piraeus:** the crew types the destination by hand, so `PiraeusMatcher` accepts the spellings seen in real AIS data (`PIRAEUS`, `PIREAS`, `PIREUS`, `GR PIR`, `GRPIR`, `PIRAEUS ANCH`, ...). For routes such as `HER-SER-PIR` or `FROM>TO`, only the final destination counts.
2. **ETA is available:** AIS uses month `0`, day `0`, hour `24` and minute `60` to mean "not available". These values are rejected.
3. **ETA is a real date:** AIS sends no year, so the ETA is placed in the current year. Dates that do not exist (e.g. 31 February) are dropped.
4. **ETA is in the next 24 hours:** only arrivals between now and now + 24 hours are returned, nearest first.

## 🛠️ Tech Stack

### Backend
| Tool | Purpose |
|------|---------|
| **Java 17** | Programming language |
| **Spring Boot 4** | Application framework (embedded Tomcat server, auto-configuration) |
| **Spring Web MVC** | REST controllers (`@RestController`, `@GetMapping`) |
| **Java `HttpClient` WebSocket** | Live connection to AISStream (`WebSocket.Listener`) |
| **Jackson** | JSON → Java object conversion (`JsonMapper`, `@JsonProperty`, `@JsonIgnoreProperties`) |
| **Java Records** | Immutable data models that mirror the AIS JSON structure |
| **`ConcurrentHashMap`** | Thread-safe in-memory storage (written by the WebSocket thread, read by web requests) |
| **`java.time` API** | `Instant`, `Duration`, `ZonedDateTime` for AIS timestamps, ETAs and Athens local time |

### External APIs
| API | Purpose |
|-----|---------|
| **[AISStream.io](https://aisstream.io/)** | Free real-time AIS data over WebSocket (requires a free API key) |

### Build & Development Tools
| Tool | Purpose |
|------|---------|
| **Maven** + **Maven Wrapper** | Dependency management and build (`mvnw` works without installing Maven) |
| **Spring Initializr** | Project generation ([start.spring.io](https://start.spring.io/)) |
| **IntelliJ IDEA** | IDE |
| **Git** | Version control |
| **GitHub** | Remote repository, feature branches and Pull Requests |

## 🚀 How to Run

**Requirements:** JDK 17 or newer and a free API key from [aisstream.io](https://aisstream.io/).

1. Clone the repository:
   ```bash
   git clone https://github.com/Liakops7/ship-tracker.git
   cd ship-tracker
   ```
2. Set your API key as an environment variable. The key is read from `application.properties` through `${AISSTREAM_API_KEY}`, so it is never stored in the repository:
   ```bash
   # Windows (PowerShell)
   $env:AISSTREAM_API_KEY="your-api-key"

   # Linux / macOS
   export AISSTREAM_API_KEY=your-api-key
   ```
3. Run the application with the Maven Wrapper:
   ```bash
   # Windows
   mvnw.cmd spring-boot:run

   # Linux / macOS
   ./mvnw spring-boot:run
   ```
4. Wait a few minutes for AIS messages to arrive, then open **http://localhost:8080/vessels/aegean** or **http://localhost:8080/ports/piraeus/arrivals**.

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/vessels/aegean` | Vessels in the Aegean that reported a position in the last 30 minutes |
| GET | `/ports/piraeus/arrivals` | Vessels expected at Piraeus in the next 24 hours, nearest first |

All times are in Athens local time, in the format `dd/MM/yyyy HH:mm:ss UTC +2` (`UTC +3` during daylight saving time).

### Example response: `GET /vessels/aegean`

```json
[
  {
    "mmsi": 239000001,
    "name": "SAMPLE VESSEL",
    "latitude": 37.9421,
    "longitude": 23.6312,
    "lastSeen": "10/11/2026 14:30:12 UTC +2"
  }
]
```

### Example response: `GET /ports/piraeus/arrivals`

```json
[
  {
    "mmsi": 241000002,
    "name": "SAMPLE CARGO",
    "destination": "PIRAEUS GRE",
    "eta": "11/11/2026 06:00:00 UTC +2"
  }
]
```

## 📁 Project Structure

```
src/main/
├── java/com/liakops/ship_tracker/
│   ├── ShipTrackerApplication.java    # Application entry point
│   ├── AisStreamClient.java           # Opens the AISStream WebSocket on startup
│   ├── AisListener.java               # Subscribes, parses messages and routes them to the stores
│   ├── controller/
│   │   ├── VesselController.java      # Aegean vessels endpoint (/vessels/aegean)
│   │   └── ArrivalController.java     # Piraeus arrivals endpoint (/ports/piraeus/arrivals)
│   ├── service/
│   │   ├── VesselStore.java           # In-memory latest position per vessel (MMSI)
│   │   ├── ArrivalStore.java          # In-memory expected arrival per vessel (MMSI)
│   │   └── PiraeusMatcher.java        # Decides if a crew-entered destination means Piraeus
│   ├── model/
│   │   ├── AisMessage.java            # AISStream message envelope
│   │   ├── Metadata.java              # "MetaData" part (MMSI, name, position, time)
│   │   ├── AisPayload.java            # "Message" part of the envelope
│   │   ├── ShipStaticData.java        # Destination and ETA declared by the crew
│   │   ├── Eta.java                   # AIS ETA (no year), validation and resolution
│   │   ├── Vessel.java                # A vessel's latest known position
│   │   └── Arrival.java               # An expected arrival at Piraeus
│   └── dto/
│       ├── VesselResponse.java        # API response for a vessel
│       ├── ArrivalResponse.java       # API response for an arrival
│       └── AthensTime.java            # Formats times in Athens local time
└── resources/
    └── application.properties         # Application configuration (API key from environment)
```

## 📚 Development Log

### Step 1 – Setup and API key
Generated the project with Spring Initializr and registered for a free AISStream API key. The key is read from the `AISSTREAM_API_KEY` environment variable through `application.properties`, so it never ends up in Git, and it is masked when the subscription message is logged.

### Step 2 – WebSocket connection
Created `AisStreamClient` as a `CommandLineRunner`, so the connection opens when the application starts, using Java's built-in `HttpClient` WebSocket. `AisListener` implements `WebSocket.Listener` and sends the subscription message (API key, Aegean bounding box, message types) right after the connection opens, since AISStream requires it within 3 seconds.

### Step 3 – Mapping JSON to Java objects
AISStream sends JSON as binary frames, so `onBinary` decodes them as UTF-8 before parsing. Created Java records (`AisMessage`, `Metadata`) that mirror the JSON envelope, with `@JsonProperty` for the AIS key names and `@JsonIgnoreProperties(ignoreUnknown = true)` to skip fields that are not needed.

### Step 4 – Storing vessels
Created the `Vessel` record and `VesselStore`, which keeps the latest position per MMSI in a `ConcurrentHashMap`. A thread-safe map is needed because the WebSocket thread writes while web request threads read. Parsed the AIS timestamp (e.g. `2026-11-07 11:25:47.62892849 +0000 UTC`, with 0 to 9 fraction digits) into an `Instant` with a custom `DateTimeFormatter`.

### Step 5 – Vessels endpoint
Added `VesselController` with `/vessels/aegean`, which returns vessels seen in the last 30 minutes. Separated the internal model from the API response with `VesselResponse`, and added `AthensTime` to show times in Athens local time, including the daylight saving time offset.

### Step 6 – Voyage data and ETA
Subscribed to `ShipStaticData` messages and added the `ShipStaticData`, `AisPayload` and `Eta` records. Since AIS ETAs have no year and use special values for "not available", `Eta` validates the values and resolves the date in the current year, returning `null` for impossible dates.

### Step 7 – Piraeus arrivals
Studied real destination values from AISStream and wrote `PiraeusMatcher` to recognise the many ways crews type Piraeus. Added `Arrival`, `ArrivalStore` and `ArrivalController` with `/ports/piraeus/arrivals`, returning arrivals in the next 24 hours sorted by ETA.

### Step 8 – Error handling
A bad message is caught and logged instead of closing the connection, and the listener always requests the next message, so the stream keeps running.

### Step 9 – Git branching and Pull Requests
Developed the Piraeus arrivals feature on a feature branch (`feature/piraeus-arrivals`) and merged it into `master`.

## 👤 Author

**Ilias Liakopoulos**, Computer Science graduate, Athens University of Economics and Business

GitHub: [@Liakops7](https://github.com/Liakops7)
