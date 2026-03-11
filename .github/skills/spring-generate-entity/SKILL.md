---
name: spring-generate-entity
description: Spring Boot projelerinde kurumsal standartlara uygun JPA entity oluşturma görevi için mimarisel ve kodsal standartları uygulayan yetenek.
---

## Ne Zaman Kullanılacak?

Kullanıcı aşağıdaki işlemlerden herhangi birini içeren bir istekte bulunursa bu yetenek devreye girer:

- entity oluşturma
- JPA modeli oluşturma
- veritabanı modeli / tablosu oluşturma
- domain model oluşturma

**Örnek tetikleyici promptlar:**

- `Ürün entitysini oluştur.`
- `Sipariş tablosu için JPA modeli yaz.`
- `Kullanıcı veritabanı nesnesi oluştur.`
- `Category entity'si ekle.`

---

## Adım Adım Uygulama Akışı

### Adım 1 — Bağlam Toplama

Üretim başlamadan önce şu soruları cevaplanmış olmalıdır. Kullanıcı sağlamamışsa sor:

1. Entity hangi servis altında yer alıyor? (paket yolu belirlenir)
2. Hangi alanlar bulunmalı? (tür ve kısıtlamalarıyla)
3. Başka entity'lerle ilişki var mı? (`@OneToMany`, `@ManyToOne`, vb.)
4. Benzersiz (unique) kısıtlama gerektiren alan var mı?

### Adım 2 — Dosya Dökümü Hazırla

Üretilecek / değiştirilecek dosyaları listele ve kullanıcıya sun, onay al:

| Dosya | İşlem | Neden |
|---|---|---|
| `<EntityName>.java` | Oluşturulacak | Ana entity sınıfı |
| `<EntityName>Repository.java` | Oluşturulacak (opsiyonel) | JPA repository arayüzü |

### Adım 3 — Üretim

Aşağıdaki tüm standartları uygulayarak kodu üret.

### Adım 4 — Doğrulama Kontrol Listesi

Üretim tamamlandıktan sonra aşağıdaki listeyi geç:

- [ ] Primary key UUID tipinde mi ve `@GeneratedValue(strategy = GenerationType.UUID)` kullanıyor mu?
- [ ] `@Entity` ve `@Table(name = "...")` anotasyonları mevcut mu?
- [ ] Tablo adı **çoğul** ve **snake_case** formatında mı? (`products`, `order_items`)
- [ ] Zorunlu alanlar `nullable = false` ile işaretlenmiş mi?
- [ ] Karakter alanlarına `length` kısıtlaması eklenmiş mi?
- [ ] Audit alanları (`createdAt`, `updatedAt`) eklenmiş mi?
- [ ] Lombok **kullanılmamış** mı?
- [ ] Getter/setter'lar **manuel** olarak yazılmış mı?
- [ ] JPA için zorunlu olan **no-arg constructor** mevcut mu?
- [ ] Tüm `import` ifadeleri `jakarta.persistence.*` paketinden mi?

---

## Standartlar ve Kurallar

### 1. JPA Anotasyonları

Tüm anotasyonlar `jakarta.persistence` paketinden kullanılmalıdır (Spring Boot 3+).

```java
import jakarta.persistence.*;
```

Zorunlu sınıf düzeyinde anotasyonlar:

```java
@Entity
@Table(name = "products")  // tablo adı: çoğul, snake_case
public class Product { ... }
```

### 2. Primary Key — Her Zaman UUID

```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

- `Long`, `Integer` ya da otomatik artan sayısal ID kullanılmaz.
- UUID sütunu veritabanında `CHAR(36)` veya `VARCHAR(36)` olarak karşılık bulur.

### 3. Lombok Yasak — Getter/Setter Manuel Yazılır

```java
// YANLIS - Kullanma
@Getter @Setter

// DOGRU
public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}
```

- `id` alanı için yalnızca getter yazılır, setter yazılmaz.

### 4. Sütun Kısıtlamaları (`@Column`)

Her alan için uygun kısıtlamalar tanımlanmalıdır:

```java
@Column(nullable = false, length = 200)        // zorunlu, max 200 karakter
@Column(nullable = false, unique = true, length = 64)  // zorunlu, benzersiz
@Column(length = 500)                           // opsiyonel, max 500 karakter
@Column(nullable = false)                       // zorunlu, sayısal
@Column(precision = 10, scale = 2)             // para birimi için BigDecimal
```

### 5. Audit Alanları (Zorunlu)

Her entity `createdAt` ve `updatedAt` alanlarını içermelidir:

```java
@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(nullable = false)
private LocalDateTime updatedAt;

@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
}
```

### 6. Constructor Kuralları

JPA için **no-arg constructor** zorunludur (erişim belirteci `protected` veya `public` olabilir):

```java
protected Product() {}  // JPA için zorunlu
```

Alan atama için ayrıca bir parameterized constructor yazılabilir:

```java
public Product(String name, double price, String sku) {
    this.name = name;
    this.price = price;
    this.sku = sku;
}
```

### 7. İlişkiler (Relationships)

```java
// ManyToOne (tercih edilen yükleme: LAZY)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
private Category category;

// OneToMany (cascade ve orphanRemoval dikkatli kullanılmalı)
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items = new ArrayList<>();
```

### 8. Paket Yapısı

```
com.turkcell.<service-name>.entity.<EntityName>.java
```

---

## Tam Entity Örneği — `Product`

```java
package com.turkcell.productservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Product() {}

    public Product(String name, String description, double price, int stock, String sku) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.sku = sku;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

---

## İlişkili Entity Örneği — `OrderItem` (ManyToOne)

```java
package com.turkcell.orderservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected OrderItem() {}

    public OrderItem(Order order, UUID productId, int quantity, double unitPrice) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

---

## Sık Yapılan Hatalar

| Hata | Dogru Kullanim |
|---|---|
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | `GenerationType.UUID` kullan |
| `import javax.persistence.*` | `import jakarta.persistence.*` kullan (Spring Boot 3+) |
| `@Getter @Setter` (Lombok) | Manuel getter/setter yaz |
| Tablo adı tekil: `@Table(name = "product")` | Çoğul kullan: `@Table(name = "products")` |
| `id` için setter yazmak | `id` setter'ı olmaz, yalnızca getter |
| Audit alanlarını unutmak | Her entity'de `createdAt` ve `updatedAt` zorunludur |