package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.model.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAll();
}
