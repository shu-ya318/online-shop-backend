package com.project.demo.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.project.demo.dto.project.ProductFilterRequestDTO;
import com.project.demo.model.Product;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;

public class ProductSpecifications {

    public static Specification<Product> doFilter(ProductFilterRequestDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (StringUtils.hasText(filter.keyword())) {
                    String pattern = "%" + filter.keyword().toLowerCase() + "%";
                    EntityType<Product> model = root.getModel();
                    List<Predicate> keywordPredicates = new ArrayList<>();

                    for (Attribute<? super Product, ?> attribute : model.getAttributes()) {
                        if (attribute.isCollection()) {
                            continue;
                        }
                        keywordPredicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get(attribute.getName()).as(String.class)), pattern));
                    }

                    predicates.add(criteriaBuilder.or(keywordPredicates.toArray(new Predicate[0])));
                }

                if (filter.category() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("category"), filter.category()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
