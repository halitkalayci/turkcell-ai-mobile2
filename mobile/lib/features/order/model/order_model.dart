class OrderItemModel {
  const OrderItemModel({
    this.id,
    this.productId,
    this.quantity,
    this.unitPrice,
  });

  final String? id;
  final String? productId;
  final int? quantity;
  final double? unitPrice;

  factory OrderItemModel.fromJson(Map<String, dynamic> json) => OrderItemModel(
        id: json['id'] as String?,
        productId: json['productId'] as String?,
        quantity: json['quantity'] as int?,
        unitPrice: json['unitPrice'] == null
            ? null
            : (json['unitPrice'] as num).toDouble(),
      );
}

class OrderModel {
  const OrderModel({
    this.id,
    this.orderDate,
    this.customerId,
    this.totalPrice,
    this.items,
  });

  final String? id;
  final String? orderDate;
  final String? customerId;
  final double? totalPrice;
  final List<OrderItemModel>? items;

  factory OrderModel.fromJson(Map<String, dynamic> json) => OrderModel(
        id: json['id'] as String?,
        orderDate: json['orderDate'] as String?,
        customerId: json['customerId'] as String?,
        totalPrice: json['totalPrice'] == null
            ? null
            : (json['totalPrice'] as num).toDouble(),
        items: json['items'] == null
            ? null
            : (json['items'] as List<dynamic>)
                .map((e) => OrderItemModel.fromJson(e as Map<String, dynamic>))
                .toList(),
      );
}
