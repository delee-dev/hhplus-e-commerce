package kr.hhplus.be.server.infrastructure.product.persistence;

import kr.hhplus.be.server.domain.product.CategoryRepository;
import kr.hhplus.be.server.domain.product.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryCustomRepository implements CategoryRepository {
    private final CategoryJapRepository categoryJapRepository;

    @Override
    public List<Category> findAll() {
        return categoryJapRepository.findAll();
    }
}
