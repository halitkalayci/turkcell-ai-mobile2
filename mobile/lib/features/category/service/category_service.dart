import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:ecommerce/core/network/app_constants.dart';
import '../model/category_model.dart';

class CategoryService {
  Future<List<CategoryModel>> fetchAll() async {
    final uri = Uri.parse('${AppConstants.baseUrl}${AppConstants.categoriesPath}');
    final response = await http.get(uri);

    if (response.statusCode != 200) {
      throw Exception('Kategoriler yüklenemedi: ${response.statusCode}');
    }

    final List<dynamic> body = jsonDecode(response.body) as List<dynamic>;
    return body
        .map((item) => CategoryModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }
}
