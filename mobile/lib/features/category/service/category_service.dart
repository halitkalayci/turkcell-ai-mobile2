import 'dart:convert';
import 'package:http/http.dart' as http;
import '../model/category_model.dart';

class CategoryService {
  static const String _baseUrl = 'http://localhost:8080/api/categories';

  Future<List<CategoryModel>> fetchAll() async {
    final response = await http.get(Uri.parse(_baseUrl));

    if (response.statusCode != 200) {
      throw Exception('Kategoriler yüklenemedi: ${response.statusCode}');
    }

    final List<dynamic> body = jsonDecode(response.body) as List<dynamic>;
    return body
        .map((item) => CategoryModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }
}
