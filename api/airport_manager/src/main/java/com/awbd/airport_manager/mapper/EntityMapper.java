package com.awbd.airport_manager.mapper;

import java.util.List;

public interface EntityMapper<E, D> {

    D toDto(E entity);

    E toEntity(D dto);

    default List<D> toList(List<E> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    default List<E> fromList(List<D> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }
}
