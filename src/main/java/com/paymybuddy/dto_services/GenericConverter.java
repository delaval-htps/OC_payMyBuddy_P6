package com.paymybuddy.dto_services;

public interface GenericConverter<S extends BaseEntity, T extends BaseDto> {
    
    public T entityToDto(S source);

    public S dtoToEntity(T target);
}
