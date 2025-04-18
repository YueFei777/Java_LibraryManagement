package redlib.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import redlib.backend.model.Readers;

@Mapper
public interface ReadersMapper {
    Readers login(@Param("username") String username, @Param("password") String password);
    Readers selectByReaderId(@Param("readerId") String readerId);
    Readers selectByUsername(@Param("username") String username);
    Integer getIdByUsername(@Param("username") String username);
}
