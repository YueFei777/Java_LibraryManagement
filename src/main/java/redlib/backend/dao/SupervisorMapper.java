package redlib.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import redlib.backend.model.Supervisor;

@Mapper
public interface SupervisorMapper {
    Supervisor login(@Param("username") String username, @Param("password") String password);
    Supervisor selectById(Integer id);
    Integer getIdByUsername(@Param("username") String username);
    Supervisor selectByUsername(@Param("username") String username);
}
