<h1>SPRING TRANSACTION ENGINE</h1>
<p>
  <b>Spring Transaction Engine</b> is a high-performance e-commerce transaction, customer support, and multi-item cart engine built with Spring Boot, PostgreSQL, and Apache Kafka. Engineered for high-concurrency environments, it eliminates overselling race conditions via database-level pessimistic locking, manages stock TTL reservations, processes return workflows with automated inventory reconciliation, and decouples transactional email notifications via an event-driven Kafka architecture.
</p>

<h2>System Capabilities</h2>
<p>
  <b>Concurrency Control & Pessimistic Locking:</b> Utilizes JPA pessimistic write locks (<code>FOR UPDATE</code>) on product inventory records during order placement to eliminate race conditions under heavy traffic.<br>
  <b>Event-Driven Asynchronous Architecture (Apache Kafka):</b> Emits <code>OrderEvent</code> records to Kafka topics upon status transitions, offloading email dispatches and secondary logic to background consumers (<code>OrderEventsConsumer</code>).<br>
  <b>Multi-Item Shopping Cart Processing:</b> Handles multi-product orders using <code>OrderItem</code> entities that snapshot unit prices and quantities upon checkout.<br>
  <b>Timed Inventory Reservation (TTL):</b> Automatically reserves stock for pending orders using a 15-minute expiration timer (TTL) to prevent permanent stock hold-ups.<br>
  <b>Automated Stock Reclamation Scheduler:</b> Runs background tasks (<code>@Scheduled</code>) to identify expired <code>PENDING</code> orders, update their status to <code>CANCELLED</code>, and restore stock quantities atomically.<br>
  <b>Order Returns Lifecycle:</b> Manages 14-day return workflows (<code>IN_RETURN</code>) and automatically replenishes inventory counts upon confirmation (<code>RETURNED</code>).<br>
  <b>Customer Support Ticketing System:</b> Features a dedicated ticketing workflow allowing customers to log support issues linked to order UUIDs.<br>
  <b>Asynchronous Transactional E-mails:</b> Triggers notification dispatches in non-blocking background threads across all core lifecycle events (order confirmation, payment receipts, dispatches, return processing, newsletter, support tickets).<br>
  <b>Centralized Exception Handling:</b> Intercepts domain and validation errors globally via <code>@RestControllerAdvice</code>, delivering uniform JSON responses.<br>
  <b>Data Pre-seeding (DataInitializer):</b> Automatically seeds initial category and product records on startup for instant Postman testing.
</p>

<h2>Tech Stack</h2>
<p>
  <b>Language & Framework:</b> Java 26 / Spring Boot 3.x / 4.x (Web MVC, Data JPA, Security, Validation, JavaMailSender, Spring Kafka)<br>
  <b>Messaging & Streaming:</b> Apache Kafka (Spring Kafka, Custom DTO Event Records)<br>
  <b>Database:</b> PostgreSQL 16 & Redis (Docker Containers)<br>
  <b>ORM & Persistence:</b> Hibernate 7 / JPA (Pessimistic Locking, UUID Primary Keys, One-To-Many OrderItems)<br>
  <b>Tooling & Verification:</b> Lombok, Docker, Maven, <code>@RestControllerAdvice</code>, Postman API Collection
</p>

<h2>Architecture & Technical Solutions</h2>
<p>
  <b>1. Multi-Item Checkout & Pessimistic Locking (ProductRepository & OrderService)</b><br>
  A <code>@Lock(LockModeType.PESSIMISTIC_WRITE)</code> query (<code>SELECT ... FOR UPDATE</code>) locks product rows sequentially during <code>placeOrder()</code>, validates inventory, and freezes unit pricing inside <code>OrderItem</code> instances.
</p>
<p>
  <b>2. Non-Blocking Event-Driven Orders (OrderEvent & OrderEventsConsumer)</b><br>
  Order lifecycle state changes (<code>placeOrder</code>, <code>payOrder</code>, <code>returnOrderRequest</code>, <code>returnOrderSuccessful</code>) persist changes to PostgreSQL and immediately emit an <code>OrderEvent</code> record to the <code>transaction-order-events</code> Kafka topic. The <code>OrderEventsConsumer</code> handles mail generation in the background without delaying HTTP responses.
</p>
<p>
  <b>3. Atomic Reservation & Payment Workflow</b><br>
  Orders enter a <code>PENDING</code> state with a calculated <code>expiresAt</code> timestamp (15-minute window). Payments processed via <code>/api/orders/{id}/pay</code> prior to expiration transition the order to <code>PAID</code> and notify Kafka.
</p>
<p>
  <b>4. Automated Cleanup & Inventory Replenishment (OrderCleanupScheduler)</b><br>
  A background process (<code>@Scheduled(fixedRate = 60000)</code>) detects expired <code>PENDING</code> transactions, marks them <code>CANCELLED</code>, re-acquires pessimistic write locks, and restores stock back to the database.
</p>
<p>
  <b>5. Return Reconciliation & Support Ticketing</b><br>
  Enforces a 14-day return eligibility policy (<code>IN_RETURN</code>), automatically replenishing warehouse stock upon acceptance (<code>RETURNED</code>). The ticket module validates order existence before attaching customer inquiries.
</p>

<h2>Installation & Configuration</h2>
<p>
  <b>1. Prerequisites</b><br>
  Ensure you have <b>JDK 26</b>, <b>Maven</b>, and <b>Docker Desktop</b> installed on your environment.
</p>
<p>
  <b>2. Start PostgreSQL & Kafka via Docker</b><br>
  Run the PostgreSQL and Kafka containers:
</p>
<pre><code>docker compose up -d</code></pre>
<p>
  <b>3. Run the Application</b><br>
  Build and start the Spring Boot backend using Maven:
</p>
<pre><code>mvn clean spring-boot:run</code></pre>
<p>
  Once started, the backend API will be available at <code>http://localhost:8080/api/</code> for Postman requests.
</p>

<hr>

<h1>SPRING TRANSACTION ENGINE</h1>
<p>
  <b>Spring Transaction Engine</b> to wysokowydajny silnik transakcyjny e-commerce z obsługą wielopozycyjnego koszyka, czasową rezerwacją stanów magazynowych (TTL), bezobsługową obsługą zwrotów, integracją zgłoszeń wsparcia klienta (ticketów) oraz asynchroniczną magistralą zdarzeń opartą na Apache Kafka. System eliminuje zjawisko oversellingu na poziomie bazy danych (PostgreSQL) poprzez blokady pesymistyczne oraz gwarantuje pełne rozsprzęglenie powiadomień e-mail.
</p>

<h2>Możliwości Systemu</h2>
<p>
  <b>Blokady Pesymistyczne (Pessimistic Locking):</b> Wykorzystanie blokad zapisu JPA (<code>FOR UPDATE</code>) na wierszach produktów podczas zamawiania, całkowicie zapobiegające wyścigom wątków (race conditions).<br>
  <b>Asynchroniczna Architektura Zdarzeniowa (Apache Kafka):</b> Publikacja rekordów zdarzeń <code>OrderEvent</code> do topików Kafki przy zmianach stanu zamówienia, przekazująca obsługę maili i pobocznej logiki do konsumentów w tle (<code>OrderEventsConsumer</code>).<br>
  <b>Wielopozycyjny Koszyk Zgłoszeń:</b> Przetwarzanie zamówień wielu produktów z automatycznym zamrażaniem cen jednostkowych i ilości w encjach <code>OrderItem</code>.<br>
  <b>Czasowa Rezerwacja Magazynu (TTL):</b> Automatyczna rezerwacja towaru dla zamówień oczekujących z 15-minutowym timerem wygasania.<br>
  <b>Automatyczny Scheduler Zwrotów (OrderCleanupScheduler):</b> Harmonogram <code>@Scheduled</code> wykrywający wygasłe zamówienia <code>PENDING</code>, zmieniający ich status na <code>CANCELLED</code> oraz atomowo przywracający towar na magazyn.<br>
  <b>Cykl Zwrotów i Rekoncylacja Magazynu:</b> Obsługa procedury zwrotów w oknie 14 dni (<code>IN_RETURN</code>) z automatycznym przywracaniem stanów magazynowych po zatwierdzeniu (<code>RETURNED</code>).<br>
  <b>System Zgłoszeń Wspierających (Ticketing):</b> Dedykowany moduł zgłoszeń pomocy technicznej powiązany z UUID zamówienia i adresem e-mail.<br>
  <b>Asynchroniczne Powiadomienia E-mail:</b> Generowanie powiadomień transakcyjnych (potwierdzenie zamówienia, opłacenie, wyjazd towaru, zwroty, subskrypcja newslettera, zgłoszenia wsparcia) w nieblokujących wątkach w tle.<br>
  <b>Centralna Obsługa Błędów:</b> Przechwytywanie wyjątków domenowych i walidacyjnych przez <code>@RestControllerAdvice</code> ze spójną strukturą JSON.<br>
  <b>Automatyczna Inicjalizacja Danych (DataInitializer):</b> Automatyczne zasilenie bazy przykładowymi kategoriami i produktami dla szybkiego testowania w Postmanie.
</p>

<h2>Stos Technologiczny</h2>
<p>
  <b>Język i Framework:</b> Java 26 / Spring Boot 3.x / 4.x (Web MVC, Data JPA, Security, Validation, JavaMailSender, Spring Kafka)<br>
  <b>Architektura Zdarzeniowa:</b> Apache Kafka (Spring Kafka, Rekordy DTO Zdarzeń)<br>
  <b>Baza Danych:</b> PostgreSQL 16 & Redis (Kontenery Docker)<br>
  <b>ORM i Trwałość Danych:</b> Hibernate 7 / JPA (Blokady pesymistyczne, UUID dla zamówień, relacje One-To-Many)<br>
  <b>Narzędzia i Testy:</b> Lombok, Docker, Maven, <code>@RestControllerAdvice</code>, Postman API Collection
</p>

<h2>Architektura i Rozwiązania Techniczne</h2>
<p>
  <b>1. Blokowanie Pesymistyczne i Obsługa Koszyka (ProductRepository & OrderService)</b><br>
  Zapytanie <code>@Lock(LockModeType.PESSIMISTIC_WRITE)</code> (<code>SELECT ... FOR UPDATE</code>) sekwencyjnie blokuje wiersze zamawianych produktów podczas <code>placeOrder()</code>, weryfikuje stany magazynowe i utrwala snapshoty cen w <code>OrderItem</code>.
</p>

<p>
  <b>2. Asynchroniczna Obsługa Zdarzeń Zamówień (OrderEvent & OrderEventsConsumer)</b><br>
  Każda zmiana cyklu życia zamówienia (<code>placeOrder</code>, <code>payOrder</code>, <code>returnOrderRequest</code>, <code>returnOrderSuccessful</code>) aktualizuje baze PostgreSQL i publikuje rekord <code>OrderEvent</code> na topik Kafki <code>transaction-order-events</code>. Konsument <code>OrderEventsConsumer</code> odpowiada za wysyłkę e-maili w tle bez opóźniania odpowiedzi HTTP.
</p>

<p>
  <b>3. Atomowa Rezerwacja i Cykl Życia Płatności</b><br>
  Zamówienia inicjalizowane są w stanie <code>PENDING</code> z czasem wygaśnięcia <code>expiresAt</code> (15 minut). Opłacenie przez <code>/api/orders/{id}/pay</code> przed upływem limitu przestawia status na <code>PAID</code> i wysyła zdarzenie do Kafki.
</p>

<p>
  <b>4. Harmonogram Przywracania Stanów (OrderCleanupScheduler)</b><br>
  Proces w tle (<code>@Scheduled(fixedRate = 60000)</code>) wyszukuje przeterminowane zamówienia <code>PENDING</code>, zmienia ich status na <code>CANCELLED</code>, ponownie nakłada blokady pesymistyczne i zwraca zarezerwowane ilości towaru na magazyn.
</p>

<p>
  <b>5. Procedura Zwrotów i Zgłoszenia Klientów</b><br>
  System umożliwia weryfikację 14-dniowego okna na zwrot (<code>IN_RETURN</code>) i atomowo uzupełnia magazyn przy zatwierdzeniu (<code>RETURNED</code>). Dodatkowo moduł ticketowy weryfikuje poprawność zamówień i powiązuje zgłoszenia klienckie bezpośrednio z adresem e-mail.
</p>

<h2>Instalacja i Konfiguracja</h2>
<p>
  <b>1. Wymagania Wstępne</b><br>
  Upewnij się, że w Twoim środowisku zainstalowane są: <b>JDK 26</b>, <b>Maven</b> oraz <b>Docker Desktop</b>.
</p>
<p>
  <b>2. Uruchomienie PostgreSQL i Kafki w Dockerze</b><br>
  Uruchom kontenery PostgreSQL i Kafka:
</p>
<pre><code>docker compose up -d</code></pre>
<p>
  <b>3. Uruchomienie Aplikacji</b><br>
  Skompiluj i uruchom aplikację Spring Boot przy użyciu Mavena:
</p>
<pre><code>mvn clean spring-boot:run</code></pre>
<p>
  Po pomyślnym uruchomieniu API będzie dostępne pod adresem <code>http://localhost:8080/api/</code> do testów w Postmanie.
</p>