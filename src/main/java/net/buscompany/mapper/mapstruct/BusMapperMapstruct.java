package net.buscompany.mapper.mapstruct;

import net.buscompany.dto.request.admin.AddBusDtoRequest;
import net.buscompany.model.Bus;
import net.buscompany.dto.response.admin.BusDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BusMapperMapstruct {
    BusMapperMapstruct INSTANCE = Mappers.getMapper(BusMapperMapstruct.class);
    
    @Mapping(target = "id", ignore = true)
    Bus busDtoToBus(AddBusDtoRequest busDto);

    BusDtoResponse busToBusDto(Bus bus);
}
