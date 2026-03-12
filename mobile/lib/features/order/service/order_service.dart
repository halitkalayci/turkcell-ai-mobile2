import 'dart:convert';
import 'package:http/http.dart' as http;
import '../model/order_model.dart';
import '../../../core/network/app_constants.dart';

class OrderService {
  Future<List<OrderModel>> fetchAll() async {
    final uri = Uri.parse('${AppConstants.baseUrl}${AppConstants.ordersPath}');
    final response = await http.get(uri);

    if (response.statusCode != 200) {
      throw Exception('Siparişler yüklenemedi: ${response.statusCode}');
    }

    final List<dynamic> body = jsonDecode(response.body) as List<dynamic>;
    return body
        .map((item) => OrderModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }

  Future<void> deleteOrder(String id) async {
    final uri = Uri.parse('${AppConstants.baseUrl}${AppConstants.ordersPath}/$id');
    final response = await http.delete(uri);

    if (response.statusCode != 204) {
      throw Exception('Sipariş silinemedi: ${response.statusCode}');
    }
  }
}
