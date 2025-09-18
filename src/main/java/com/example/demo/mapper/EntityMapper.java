package com.example.demo.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

public interface EntityMapper<D, E>{

    // This method is used to convert an entity to a dto
    D toDto(E entity);

    // This method is used to convert a dto to an entity
    E toEntity(D dto);

    // This method is used to convert a list of entity to a list of dto
    List<D> toDtos(List<E> entity);

    // This method is used to convert a list of dto to a list of entity
    List<E> toEntities(List<D> dto);

    // This method is used to update an entity with a dto
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget E entity, D dto);
}
