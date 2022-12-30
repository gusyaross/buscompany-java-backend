package net.buscompany.mapper.mapstruct;

import net.buscompany.dto.request.register.RegisterClientDtoRequest;
import net.buscompany.dto.response.info.GetInfoClientDtoResponse;
import net.buscompany.dto.response.register.RegisterClientDtoResponse;
import net.buscompany.dto.response.update.UpdateClientInfoDtoResponse;
import net.buscompany.model.Client;
import net.buscompany.dto.response.login.LoginClientDtoResponse;
import net.buscompany.model.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapperMapstruct {

    ClientMapperMapstruct INSTANCE = Mappers.getMapper(ClientMapperMapstruct.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Client registerClientDtoToClient(RegisterClientDtoRequest request);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    RegisterClientDtoResponse clientToRegisterClientDto(Client client);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    LoginClientDtoResponse clientToLoginClientDto(Client client);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    GetInfoClientDtoResponse clientToClientInfoDto(Client client);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    UpdateClientInfoDtoResponse clientToUpdateClientDto(Client client);

    @Named("userTypeToString")
    default String userTypeToString(UserType userType){
        return userType.toString();
    }
}
