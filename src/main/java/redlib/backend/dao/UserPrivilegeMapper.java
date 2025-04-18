package redlib.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import redlib.backend.model.UserPrivilege;
import redlib.backend.model.UserType;

import java.util.List;

@Mapper
public interface UserPrivilegeMapper {
    List<UserPrivilege> listPrivileges(
            @Param("userType") UserType userType,
            @Param("userId") Integer userId
    );
    void suspendUser(
            @Param("userType") UserType userType,
            @Param("userId") Integer userId,
            @Param("modId") String modId,
            @Param("priv") String priv
    );
    Boolean hasSuspend(
            @Param("userType") UserType userType,
            @Param("userId") Integer userId,
            @Param("modId") String modId,
            @Param("priv") String priv);
    void buildPrivilege(
            @Param("userType") UserType userType,
            @Param("userId") Integer userId,
            @Param("modId") String modId,
            @Param("priv") String priv
    );
    void deletePrivilege(
            @Param("userType") UserType userType,
            @Param("userId") Integer userId,
            @Param("modId") String modId,
            @Param("priv") String priv
    );
}
