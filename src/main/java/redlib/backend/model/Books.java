package redlib.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 描述:books表的实体类
 * @version
 * @author:  liang
 * @创建时间: 2025-03-31
 */
@Data
public class Books {
    /**
     * isbn as identifications
     */
    private String isbn;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String author;

    /**
     * 
     */
    private String publisher;

    /**
     * 
     */
    private LocalDate addedAt;

    /**
     * 
     */
    private LocalDate updatedAt;

    /**
     * 
     */
    private Integer totalCopiesInStock;

    /**
     * 
     */
    private String updatedBy;

    /**
     * 
     */
    private String coverUrl;

    /**
     * count for the times the book has been borrowed
     */
    private Integer borrowedNumber;
}