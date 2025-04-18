package redlib.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 描述:readers表的实体类
 * @version
 * @author:  20970
 * @创建时间: 2025-03-29
 */

@Entity
@Table(name = "readers")
@Data
public class Readers {
    /**
     * 
     */
    @Id
    private Integer readerId;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private String studentId;

    /**
     * 
     */
    private String contact;

    /**
     * 
     */
    private LocalDateTime createdAt;

    private String password;

}