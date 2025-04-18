package redlib.backend.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookVO {
    @NotBlank
    @Size(min=10, max=13, message="ISBN长度必须为10-13位")
    private String isbn;

    @NotBlank
    @Size(max=50)
    private String title;

    private String author;

    @Size(max=255)
    private String publisher;

    private LocalDate addedAt;
    private LocalDate updatedAt;

    @PositiveOrZero
    @Builder.Default
    private Integer totalCopiesInStock = 1;

    private String updatedBy;
    private String coverUrl;

    @PositiveOrZero
    private Integer borrowedNumbers;
    private Integer total_copies;
}
