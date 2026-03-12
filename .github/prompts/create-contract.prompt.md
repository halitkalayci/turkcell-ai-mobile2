---
name: create-contract
description: >
  Contract-first yaklaşımıyla yeni bir OpenAPI 3.0.3 sözleşmesi oluşturur.
  Kullanıcıdan kaynak adı, CRUD operasyonları, alan tanımları ve iş kurallarını toplayarak
  projedeki mevcut sözleşmelerle tutarlı bir YML dosyası üretir.
agent: "agent"
argument-hint: "Kaynak adı (ör: Payment, Shipment, Customer)"
---

# Sözleşme Oluşturma Prompt'u

Sen contract-first yaklaşımında OpenAPI 3.0.3 sözleşmesi üreten bir uzmansın.
Sözleşmeler `docs/openapi/` dizininde `{kaynak}-v1.yml` adıyla saklanır.

## Referans Yapı

Mevcut sözleşmeleri referans olarak kullan:
- [product-v1.yml](../../docs/openapi/product-v1.yml)
- [category-v1.yml](../../docs/openapi/category-v1.yml)
- [order-v1.yml](../../docs/openapi/order-v1.yml)

## Zorunlu Kurallar

1. OpenAPI sürümü **3.0.3** olmalıdır.
2. Server URL sabit: `http://localhost:8080/api/v1`
3. Tüm ID alanları `type: string, format: uuid` olmalıdır.
4. `ErrorResponse` şeması her sözleşmede standart olarak yer almalıdır (timestamp, status, error, message, path).
5. Standart hata response ref'leri kullanılmalıdır: `BadRequest`, `NotFound`, `InternalServerError` ve gerekirse `Conflict`.
6. Dosya adı formatı: `{kaynak-adı-küçük}-v1.yml`
7. `operationId` değerleri camelCase ve tutarlı olmalıdır (ör: `getAllProducts`, `createProduct`).

## Kullanıcıdan Toplanacak Bilgiler

Sözleşme oluşturmadan önce aşağıdaki bilgileri kullanıcıdan **sırayla** sor. Her adımda cevabı al, sonra bir sonrakine geç.

### Adım 1 — Kaynak Tanımı
- **Kaynak adı** (tekil, PascalCase): ör. `Payment`, `Customer`, `Shipment`
- **API açıklaması** (info.description): Kısa, Türkçe açıklama
- **Base path**: ör. `/payments` (çoğul, kebab-case)
- **Tag adı ve açıklaması**: ör. `Payments` — "Ödeme yönetimi işlemleri"

### Adım 2 — CRUD Operasyonları
Aşağıdakilerden hangilerinin dahil edileceğini sor:

| Operasyon | HTTP | Path | Varsayılan |
|-----------|------|------|------------|
| Tümünü listele | GET | `/{base}` | ✅ |
| ID ile getir | GET | `/{base}/{id}` | ✅ |
| Oluştur | POST | `/{base}` | ✅ |
| Güncelle | PUT | `/{base}/{id}` | ✅ |
| Sil | DELETE | `/{base}/{id}` | ✅ |

Kullanıcı ek custom endpoint isteyebilir (ör: `PATCH /{base}/{id}/status`). Bunları da kaydet.

### Adım 3 — Request Şeması (`{Kaynak}Request`)
Her alan için şunları topla:

| Bilgi | Açıklama |
|-------|----------|
| **Alan adı** | camelCase (ör: `unitPrice`) |
| **Tip** | `string`, `integer`, `number`, `boolean`, `array`, `object` |
| **Format** | Varsa: `uuid`, `date-time`, `decimal`, `email` vb. |
| **Zorunlu mu?** | `required` listesine eklenecek mi? |
| **Validasyon** | `minLength`, `maxLength`, `minimum`, `maximum`, `pattern`, `minItems` vb. |
| **Açıklama** | Türkçe kısa açıklama |
| **Örnek değer** | `example` alanı |

Eğer iç içe (nested) bir schema varsa (ör: `OrderItemRequest`), aynı süreci o schema için de tekrarla.

### Adım 4 — Response Şeması (`{Kaynak}Response`)
Request'ten farklı olabilecek alanları belirle:
- Hesaplanan alanlar (ör: `totalPrice`)
- Zaman damgaları (ör: `createdAt`, `updatedAt`)
- İlişki alanları (nested response schema)
- Request'te olmayan ama response'ta dönen ek alanlar

### Adım 5 — İş Kuralları ve Hata Durumları
- **Benzersizlik kısıtları**: Hangi alan(lar) unique? → `409 Conflict` gerektirir
- **İlişki bağımlılıkları**: Başka bir kaynağa referans var mı? → `404 NotFound` gerektirebilir
- **Özel iş kuralları**: Stok kontrolü, durum geçişleri vb.

## Çıktı Üretimi

Tüm bilgiler toplandıktan sonra:

1. **Dosya dökümü** sun: `docs/openapi/{kaynak}-v1.yml` — neden oluşturuluyor
2. Referans sözleşmelerden (product-v1.yml) yapısal tutarlılığı koru
3. YML dosyasını **tam ve geçerli** olarak üret
4. Üretilen sözleşmenin **happy-path test** senaryosunu listele:
   - Her endpoint için örnek istek/yanıt akışı
5. `AGENTS.MD` kurallarına uygunluğu doğrula:
   - Swagger UI uyumluluğu
   - Contract-first yaklaşımı
   - `AppConstants` için gerekecek yeni path sabiti önerisi

## Örnek Kullanım

```
Kullanıcı: /create-contract Payment

Agent: Harika! Payment sözleşmesini oluşturacağız.

**Adım 1 — Kaynak Tanımı**
- Kaynak adı: `Payment` ✓
- API açıklaması önerim: "Mini E-Ticaret - Payment servis API sözleşmesi (contract-first)"
  Bu uygun mu, yoksa değiştirmek ister misin?
- Base path: `/payments` uygun mu?
- Tag: `Payments` — "Ödeme yönetimi işlemleri" uygun mu?
```