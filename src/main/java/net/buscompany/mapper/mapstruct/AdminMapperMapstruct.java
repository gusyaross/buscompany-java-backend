package net.buscompany.mapper.mapstruct;

import net.buscompany.dto.request.register.RegisterAdminDtoRequest;
import net.buscompany.dto.response.info.GetInfoAdminDtoResponse;
import net.buscompany.dto.response.register.RegisterAdminDtoResponse;
import net.buscompany.dto.response.update.UpdateAdminInfoDtoResponse;
import net.buscompany.model.Admin;
import net.buscompany.dto.response.login.LoginAdminDtoResponse;
import net.buscompany.model.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapperMapstruct {

    AdminMapperMapstruct INSTANCE = Mappers.getMapper(AdminMapperMapstruct.class);

    @Mapping(target = "id", ignore = true)
    Admin registerAdminDtoToAdmin(RegisterAdminDtoRequest request);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    RegisterAdminDtoResponse adminToRegisterAdminDto(Admin admin);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    LoginAdminDtoResponse adminToLoginAdminDto(Admin admin);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    GetInfoAdminDtoResponse adminToAdminInfoDto(Admin admin);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    UpdateAdminInfoDtoResponse adminToUpdateAdminDto(Admin admin);

    @Named("userTypeToString")
    default String userTypeToString(UserType userType){
        return userType.toString();
    }
}
