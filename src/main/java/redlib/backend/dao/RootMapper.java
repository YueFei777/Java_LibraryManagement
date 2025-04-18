package redlib.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import redlib.backend.model.Root;

@Mapper
public interface RootMapper {
    Root login(@Param("username") String username, @Param("password") String password);
}

